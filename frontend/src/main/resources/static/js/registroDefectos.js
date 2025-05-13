import { conectarNotificaciones, enviarNotificacion } from './notificacionesSockets.js';

document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('authToken');

    if (!token) {
        window.location.href = '/login';
        return;
    }

    // Variables globales
    let piezasList = [];
    let tiposDefectoList = [];

    let currentReport = {
        loteId: null,
        moneda: null,
        defectos: [],
        idUsuario: obtenerUsuarioId(token)
    };

    // Elementos UI
    const quantityInput = document.getElementById('cantidad');
    const decreaseBtn = document.querySelector('.quantity-btn.decrease');
    const increaseBtn = document.querySelector('.quantity-btn.increase');
    const currencyBtns = document.querySelectorAll('.currency-btn');
    const clearBtn = document.querySelector('.clear-btn');
    const processBtn = document.querySelector('.process-btn');
    const rejectBtn = document.querySelector('.reject-btn');
    const piezaSelect = document.getElementById('piezaSelect');
    const defectosContainer = document.getElementById('defectosContainer');
    const defectosResumen = document.getElementById('defectosResumen');

    // Cargar datos iniciales
    await cargarPiezas();
    await cargarTiposDefecto();

    // Event Listeners
    decreaseBtn.addEventListener('click', () => ajustarCantidad(-1));
    increaseBtn.addEventListener('click', () => ajustarCantidad(1));
    currencyBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            currencyBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
        });
    });

    clearBtn.addEventListener('click', limpiarFormulario);
    processBtn.addEventListener('click', procesarDefecto);
    rejectBtn.addEventListener('click', registrarRechazo);

    // Funciones
    function ajustarCantidad(valor) {
        let nuevaCantidad = parseInt(quantityInput.value) + valor;
        quantityInput.value = nuevaCantidad < 1 ? 1 : nuevaCantidad;
    }

    async function cargarPiezas() {
        try {
            const response = await fetch('/api/v1/piezas', {
                headers: {'Authorization': `Bearer ${token}`}
            });
            piezasList = await response.json();
            piezasList.forEach(pieza => {
                const option = document.createElement('option');
                option.value = pieza.idPieza;
                option.textContent = pieza.nombre;
                piezaSelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error cargando piezas:', error);
            mostrarError('No se pudieron cargar las piezas');
        }
    }

    async function cargarTiposDefecto() {
        try {
            // Nota: Se cambió la URL de endpoint a tipo-defectos según el ejemplo
            const response = await fetch('/api/v1/tipo-defectos', {
                headers: {'Authorization': `Bearer ${token}`}
            });
            tiposDefectoList = await response.json();
            tiposDefectoList.forEach((tipo, index) => {
                const divRow = document.createElement('div');
                divRow.className = 'radio-row';
                const divOption = document.createElement('div');
                divOption.className = 'radio-option';

                const input = document.createElement('input');
                input.type = 'radio';
                input.id = `defecto${index}`;
                input.name = 'defecto';
                input.value = tipo.idTipoDefecto;

                const label = document.createElement('label');
                label.htmlFor = `defecto${index}`;
                label.textContent = tipo.nombre;

                divOption.appendChild(input);
                divOption.appendChild(label);
                divRow.appendChild(divOption);
                defectosContainer.appendChild(divRow);
            });
        } catch (error) {
            console.error('Error cargando tipos de defecto:', error);
            mostrarError('No se pudieron cargar los tipos de defecto');
        }
    }

    function procesarDefecto() {
        const loteId = document.getElementById('loteId').value;
        const piezaId = piezaSelect.value;
        const defectoTipoId = document.querySelector('input[name="defecto"]:checked')?.value;
        const detalles = document.getElementById('detalles').value;
        const cantidad = parseInt(quantityInput.value);
        const moneda = document.querySelector('.currency-btn.active').textContent;

        if (!validarFormulario(loteId, piezaId, defectoTipoId, detalles))
            return;

        let loteIdNumerico;
        try {
            loteIdNumerico = parseInt(loteId);
            if (isNaN(loteIdNumerico)) {
                mostrarError('El ID del lote debe ser un número válido');
                return;
            }
        } catch (e) {
            mostrarError('El ID del lote debe ser un número válido');
            return;
        }

        if (currentReport.loteId && currentReport.loteId !== loteIdNumerico) {
            mostrarError('No puedes cambiar el ID del lote');
            return;
        }
        if (!currentReport.loteId) {
            currentReport.loteId = loteIdNumerico;
            currentReport.moneda = moneda;
        } else if (currentReport.moneda !== moneda) {
            mostrarError('No puedes cambiar la moneda');
            return;
        }

        const piezaIdNumerico = parseInt(piezaId);
        const defectoTipoIdNumerico = parseInt(defectoTipoId);

        currentReport.defectos.push({
            tipoDefecto: {idTipoDefecto: defectoTipoIdNumerico},
            detalles,
            cantidad_piezas: cantidad,
            pieza: {idPieza: piezaIdNumerico},
            costo: 0
        });

        actualizarResumen(currentReport);
        mostrarExito('Defecto añadido al reporte');
        limpiarCamposDefecto();

        // 🚀 Enviar notificación WebSocket
        enviarNotificacion(`✅ Defecto procesado para el lote ${currentReport.loteId}`);
    }


    async function registrarRechazo() {
        if (currentReport.defectos.length === 0) {
            mostrarError('Agrega al menos un defecto');
            return;
        }

        try {
            // Asegurarnos que loteId sea un número (Long)
            let loteIdNumerico;
            try {
                loteIdNumerico = parseInt(currentReport.loteId);
                if (isNaN(loteIdNumerico)) {
                    throw new Error('El ID del lote debe ser un número');
                }
            } catch (e) {
                mostrarError('El ID del lote debe ser un número válido');
                return;
            }

            const reporteData = {
                loteId: loteIdNumerico,
                idUsuario: currentReport.idUsuario,
                moneda: currentReport.moneda,
                costoTotal: 0,
                defectos: currentReport.defectos
            };

            console.log('Enviando reporte:', JSON.stringify(reporteData));

            const response = await fetch('/api/v1/reportes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(reporteData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al registrar el reporte: ${errorText}`);
            }

            const data = await response.json();
            actualizarResumen(data);
            mostrarExito('Reporte registrado exitosamente');
            limpiarFormulario();

            enviarNotificacion(`🔧 Se registró un nuevo reporte para el lote ${data.loteId}`);
        } catch (error) {
            console.error('Error:', error);
            mostrarError(error.message || 'Error al procesar el reporte');
        }
    }

    function actualizarResumen(reporte) {
        document.getElementById('resumenLote').textContent = reporte.loteId || '-';
        document.getElementById('resumenCosto').textContent = `${reporte.costoTotal?.toFixed(2) || '0.00'} ${reporte.moneda || ''}`;
        defectosResumen.innerHTML = '';

        reporte.defectos.forEach(defecto => {
            const pieza = piezasList.find(p => p.idPieza == defecto.pieza.idPieza);
            const tipoDefecto = tiposDefectoList.find(t => t.idTipoDefecto == defecto.tipoDefecto.idTipoDefecto);

            const defectoHTML = `
                <div class="summary-row">
                    <span class="summary-label">Pieza:</span>
                    <span class="summary-value">${pieza?.nombre || 'Desconocido'}</span>
                </div>
                <div class="summary-row">
                    <span class="summary-label">Defecto:</span>
                    <span class="summary-value">${tipoDefecto?.nombre || 'Desconocido'}</span>
                </div>
                <div class="summary-row">
                    <span class="summary-label">Cantidad:</span>
                    <span class="summary-value">x${defecto.cantidad_piezas}</span>
                </div>
                <div class="summary-row">
                    <span class="summary-label">Detalles:</span>
                    <span class="summary-value">${defecto.detalles}</span>
                </div>
                <div class="summary-divider"></div>
            `;
            defectosResumen.insertAdjacentHTML('beforeend', defectoHTML);
        });
    }

    function limpiarCamposDefecto() {
        piezaSelect.selectedIndex = 0;
        document.querySelectorAll('input[name="defecto"]').forEach(input => input.checked = false);
        document.getElementById('detalles').value = '';
        quantityInput.value = 1;
    }

    function limpiarFormulario() {
        document.getElementById('loteId').value = '';
        limpiarCamposDefecto();
        currencyBtns[0].click(); // Seleccionar primera moneda por defecto
        defectosResumen.innerHTML = '';
        // Reiniciar el reporte actual
        currentReport = {
            loteId: null,
            moneda: null,
            defectos: [],
            idUsuario: obtenerUsuarioId(token)
        };
    }

    function mostrarError(mensaje) {
        const errorElement = document.createElement('div');
        errorElement.className = 'error-message';
        errorElement.textContent = mensaje;
        document.body.appendChild(errorElement);
        setTimeout(() => errorElement.remove(), 5000);
    }

    function mostrarExito(mensaje) {
        const successElement = document.createElement('div');
        successElement.className = 'success-message';
        successElement.textContent = mensaje;
        document.body.appendChild(successElement);
        setTimeout(() => successElement.remove(), 5000);
    }

    function validarFormulario(loteId, piezaId, defectoTipoId, detalles) {
        if (!loteId) {
            mostrarError('Ingrese el ID del Lote');
            return false;
        }

        // Verificar que loteId sea un número válido
        if (isNaN(parseInt(loteId))) {
            mostrarError('El ID del lote debe ser un número válido');
            return false;
        }

        if (!piezaId) {
            mostrarError('Seleccione una pieza');
            return false;
        }
        if (!defectoTipoId) {
            mostrarError('Seleccione un tipo de defecto');
            return false;
        }
        if (!detalles) {
            mostrarError('Ingrese los detalles del defecto');
            return false;
        }
        return true;
    }

    function obtenerUsuarioId(token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            console.log("Payload del token:", payload);
            return payload.userId;
        } catch (error) {
            console.error('Error al obtener userId:', error);
            return null;
        }
    }
});

// Conectar WebSocket y manejar mensajes entrantes
conectarNotificaciones((mensaje) => {
    console.log("Mensaje recibido en registroDefectos:", mensaje);
    // Aquí podrías mostrar una notificación visual si lo deseas
});
