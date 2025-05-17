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
    let tasaCambioMXN = 1;
    let monedaBloqueada = false;

    let currentReport = {
        loteId: null,
        moneda: "USD",
        defectos: [],
        idUsuario: obtenerUsuarioId(token),
        costoTotalUSD: 0,
        costoTotalMostrar: 0
    };

    // Elementos UI
    const quantityInput = document.getElementById('cantidad');
    const currencyBtns = document.querySelectorAll('.currency-btn');
    const piezaSelect = document.getElementById('piezaSelect');
    const defectosContainer = document.getElementById('defectosContainer');
    const defectosResumen = document.getElementById('defectosResumen');

    // Cargar datos iniciales
    await cargarPiezas();
    await cargarTiposDefecto();

    currencyBtns.forEach(btn => {
        if (btn.textContent === 'USD') {
            btn.classList.add('active');
            currentReport.moneda = 'USD';
        }
    });

    // Event Listeners
    document.querySelector('.quantity-btn.decrease').addEventListener('click', () => ajustarCantidad(-1));
    document.querySelector('.quantity-btn.increase').addEventListener('click', () => ajustarCantidad(1));
    currencyBtns.forEach(btn => btn.addEventListener('click', () => seleccionarMoneda(btn)));
    document.querySelector('.clear-btn').addEventListener('click', limpiarFormulario);
    document.querySelector('.process-btn').addEventListener('click', procesarDefecto);
    document.querySelector('.reject-btn').addEventListener('click', registrarRechazo);

    // Funciones
    function ajustarCantidad(valor) {
        let nuevaCantidad = parseInt(quantityInput.value) + valor;
        quantityInput.value = nuevaCantidad < 1 ? 1 : nuevaCantidad;
    }

    async function seleccionarMoneda(btn) {
        if (monedaBloqueada)
            return;

        monedaBloqueada = true;
        currencyBtns.forEach(b => {
            b.disabled = true;
            b.style.opacity = '0.5';
            b.style.cursor = 'not-allowed';
        });

        try {
            const nuevaMoneda = btn.textContent;

            currencyBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentReport.moneda = nuevaMoneda;

            if (currentReport.defectos.length > 0) {
                await actualizarTasaCambio();
                actualizarValoresMostrados();
            }

            actualizarResumen(currentReport);
        } finally {
            setTimeout(() => {
                currencyBtns.forEach(b => {
                    b.disabled = false;
                    b.style.opacity = '1';
                    b.style.cursor = 'pointer';
                });
                monedaBloqueada = false;
            }, 1000);
        }
    }

    async function actualizarTasaCambio() {
        try {
            const response = await fetch(`/api/v1/conversion/rate?from=USD&to=MXN`);
            const data = await response.json();

            if (data && data.exchangeRate) {
                tasaCambioMXN = parseFloat(data.exchangeRate);
            }
        } catch (error) {
            console.error('Error actualizando tasa:', error);
            mostrarError('No se pudo actualizar la tasa de cambio');
            throw error;
        }
    }

    function actualizarValoresMostrados() {
        // Actualizar costo total
        currentReport.costoTotalMostrar = currentReport.moneda === 'USD'
                ? currentReport.costoTotalUSD
                : currentReport.costoTotalUSD * tasaCambioMXN;

        // Actualizar defectos
        currentReport.defectos.forEach(defecto => {
            defecto.costoMostrar = currentReport.moneda === 'USD'
                    ? defecto.costoUSD
                    : defecto.costoUSD * tasaCambioMXN;
        });
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
                option.textContent = `${pieza.nombre} (${pieza.costo} USD)`;
                piezaSelect.appendChild(option);
            });
        } catch (error) {
            mostrarError('Error cargando piezas');
        }
    }

    async function cargarTiposDefecto() {
        try {
            const response = await fetch('/api/v1/tipo-defectos', {
                headers: {'Authorization': `Bearer ${token}`}
            });
            tiposDefectoList = await response.json();
            tiposDefectoList.forEach((tipo, index) => {
                const divRow = document.createElement('div');
                divRow.className = 'radio-row';
                divRow.innerHTML = `
                    <div class="radio-option">
                        <input type="radio" id="defecto${index}" name="defecto" value="${tipo.idTipoDefecto}">
                        <label for="defecto${index}">${tipo.nombre}</label>
                    </div>
                `;
                defectosContainer.appendChild(divRow);
            });
        } catch (error) {
            mostrarError('Error cargando tipos de defecto');
        }
    }

    function procesarDefecto() {
        const loteId = document.getElementById('loteId').value.trim();

        if (!loteId) {
            mostrarError('Ingrese el ID del Lote');
            return;
        }

        currentReport.loteId = loteId;

        const piezaId = parseInt(piezaSelect.value) || 0;
        const defectoTipoId = parseInt(document.querySelector('input[name="defecto"]:checked')?.value) || 0;
        const detalles = document.getElementById('detalles').value;
        const cantidad = parseInt(quantityInput.value) || 0;
        const moneda = document.querySelector('.currency-btn.active').textContent;

        if (isNaN(piezaId)) {
            mostrarError('Seleccione una pieza válida');
            return;
        }

        if (!validarFormulario(loteId, piezaId, defectoTipoId, detalles))
            return;

        const pieza = piezasList.find(p => p.idPieza === piezaId);
        if (!pieza) {
            mostrarError('Pieza no encontrada');
            return;
        }

        const costoUSD = pieza.costo * cantidad;

        const nuevoDefecto = {
            tipoDefecto: {idTipoDefecto: defectoTipoId},
            detalles,
            cantidad_piezas: cantidad,
            pieza: {idPieza: piezaId},
            costoUSD: costoUSD,
            costoMostrar: currentReport.moneda === 'USD'
                    ? costoUSD
                    : costoUSD * tasaCambioMXN
        };

        currentReport.defectos.push(nuevoDefecto);
        currentReport.costoTotalUSD += costoUSD;
        currentReport.costoTotalMostrar += nuevoDefecto.costoMostrar;

        actualizarResumen(currentReport);
        limpiarCamposDefecto();
        mostrarExito('Defecto agregado');
    }

    async function registrarRechazo() {
        if (currentReport.costoTotalUSD <= 0) {
            mostrarError('El costo total debe ser mayor a cero');
            return;
        }

        if (!currentReport.loteId) {
            mostrarError('Primero debe procesar al menos un defecto con un ID de lote válido');
            return;
        }

        if (currentReport.defectos.length === 0) {
            mostrarError('Agrega al menos un defecto');
            return;
        }

        try {
            const reporteData = {
                loteId: currentReport.loteId,
                idUsuario: currentReport.idUsuario,
                moneda: currentReport.moneda,
                costoTotal: currentReport.moneda === 'USD'
                        ? currentReport.costoTotalUSD
                        : currentReport.costoTotalUSD * tasaCambioMXN,
                defectos: currentReport.defectos.map(d => ({
                        ...d,
                        costo: d.costoUSD
                    }))
            };

            const response = await fetch('/api/v1/reportes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(reporteData)
            });

            if (!response.ok)
                throw new Error(await response.text());

            const data = await response.json();
            mostrarExito('Reporte registrado exitosamente');
            limpiarFormulario();

            let mensaje = `📦 REPORTE COMPLETO\n`;
            mensaje += `Lote ID: ${data.loteId}\n`;
            mensaje += `Usuario ID: ${data.idUsuario}\n`;
            mensaje += `Moneda: ${data.moneda}\n`;
            mensaje += `Costo Total: ${data.costoTotal.toFixed(2)}\n`;
            mensaje += `Defectos:\n`;

            data.defectos.forEach((d, i) => {
                mensaje += `  ${i + 1}. Pieza ID: ${d.pieza.idPieza}, Tipo Defecto: ${d.tipoDefecto.idTipoDefecto}, Cantidad: ${d.cantidad_piezas}, Detalles: ${d.detalles}\n`;
            });

            enviarNotificacion(mensaje);

        } catch (error) {
            mostrarError(error.message);
        }
    }

    function actualizarResumen(reporte) {
        document.getElementById('resumenLote').textContent = reporte.loteId || '-';
        document.getElementById('resumenCosto').textContent =
                `${reporte.costoTotalMostrar.toFixed(2)} ${reporte.moneda || ''}`;

        defectosResumen.innerHTML = reporte.defectos.map(defecto => {
            const pieza = piezasList.find(p => p.idPieza === defecto.pieza.idPieza);
            const tipo = tiposDefectoList.find(t => t.idTipoDefecto === defecto.tipoDefecto.idTipoDefecto);

            return `
                <div class="summary-row">
                    <span>${pieza?.nombre || 'Desconocido'}</span>
                    <span>${tipo?.nombre || 'Desconocido'}</span>
                    <span>x${defecto.cantidad_piezas}</span>
                    <span>${defecto.costoMostrar.toFixed(2)} ${reporte.moneda}</span>
                </div>
            `;
        }).join('');
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
        currencyBtns[0].click();
        defectosResumen.innerHTML = '';

        currentReport = {
            loteId: null,
            moneda: 'USD',
            defectos: [],
            idUsuario: obtenerUsuarioId(token),
            costoTotalUSD: 0,
            costoTotalMostrar: 0
        };

        actualizarResumen(currentReport);
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