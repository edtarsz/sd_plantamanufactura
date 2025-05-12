document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('authToken');
    if (!token) {
        window.location.href = '/login';
        return;
    }

    // Elementos UI
    const quantityInput = document.getElementById('cantidad');
    const decreaseBtn = document.querySelector('.quantity-btn.decrease');
    const increaseBtn = document.querySelector('.quantity-btn.increase');
    const currencyBtns = document.querySelectorAll('.currency-btn');
    const clearBtn = document.querySelector('.clear-btn');
    const processBtn = document.querySelector('.process-btn');
    const rejectBtn = document.querySelector('.reject-btn');
    const searchBtn = document.querySelector('.search-btn');
    const piezaSelect = document.getElementById('piezaSelect');
    const defectosContainer = document.getElementById('defectosContainer');

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
    searchBtn.addEventListener('click', buscarLote);

    // Funciones
    function ajustarCantidad(valor) {
        let nuevaCantidad = parseInt(quantityInput.value) + valor;
        if (nuevaCantidad < 1)
            nuevaCantidad = 1;
        quantityInput.value = nuevaCantidad;
    }

    async function cargarPiezas() {
        try {
            const response = await fetch('/api/v1/piezas', {
                headers: {'Authorization': `Bearer ${token}`}
            });
            const piezas = await response.json();

            piezas.forEach(pieza => {
                const option = document.createElement('option');
                option.value = pieza.idPieza;
                option.textContent = pieza.nombre;
                piezaSelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error cargando piezas:', error);
        }
    }

    async function cargarTiposDefecto() {
        try {
            const response = await fetch('/api/v1/tipos-defecto', {
                headers: {'Authorization': `Bearer ${token}`}
            });
            const tipos = await response.json();

            tipos.forEach((tipo, index) => {
                const divRow = document.createElement('div');
                divRow.className = 'radio-row';

                const divOption = document.createElement('div');
                divOption.className = 'radio-option';

                const input = document.createElement('input');
                input.type = 'radio';
                input.id = `defecto${index}`;
                input.name = 'defecto';
                input.value = tipo;

                const label = document.createElement('label');
                label.htmlFor = `defecto${index}`;
                label.textContent = tipo;

                divOption.appendChild(input);
                divOption.appendChild(label);
                divRow.appendChild(divOption);
                defectosContainer.appendChild(divRow);
            });
        } catch (error) {
            console.error('Error cargando tipos de defecto:', error);
        }
    }

    async function procesarDefecto() {
        const loteId = document.getElementById('loteId').value;
        const piezaId = piezaSelect.value;
        const defectoTipo = document.querySelector('input[name="defecto"]:checked')?.value;
        const detalles = document.getElementById('detalles').value;
        const cantidad = parseInt(quantityInput.value);
        const moneda = document.querySelector('.currency-btn.active').textContent;

        if (!validarFormulario(loteId, piezaId, defectoTipo, detalles))
            return;

        try {
            const reporteData = {
                idUsuario: obtenerUsuarioId(token),
                defectos: [{
                        tipoDefecto: defectoTipo,
                        detalles: detalles,
                        cantidad_piezas: cantidad,
                        pieza: {idPieza: piezaId},
                        costo: 0 // Se calculará en backend
                    }],
                moneda: moneda,
                loteId: loteId
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

            const reporteCreado = await response.json();
            actualizarResumen(reporteCreado);
            mostrarExito('Defecto registrado exitosamente!');

        } catch (error) {
            console.error('Error:', error);
            mostrarError(error.message || 'Error al procesar defecto');
        }
    }

    function validarFormulario(loteId, piezaId, defectoTipo, detalles) {
        if (!loteId) {
            mostrarError('Ingrese el ID del Lote');
            return false;
        }
        if (!piezaId) {
            mostrarError('Seleccione una pieza');
            return false;
        }
        if (!defectoTipo) {
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
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.sub; // Asegurar que el JWT contenga el ID de usuario en "sub"
    }

    function actualizarResumen(reporte) {
        document.getElementById('resumenLote').textContent = reporte.loteId;
        document.getElementById('resumenCosto').textContent =
                `${reporte.costoTotal.toFixed(2)} ${reporte.moneda}`;
        document.getElementById('resumenPieza').textContent =
                reporte.defectos[0].pieza.nombre;
        document.getElementById('resumenDefecto').textContent =
                reporte.defectos[0].tipoDefecto;
        document.getElementById('resumenCantidad').textContent =
                `x${reporte.defectos[0].cantidad_piezas}`;
        document.getElementById('resumenDetalles').textContent =
                reporte.defectos[0].detalles;
    }

    function limpiarFormulario() {
        document.getElementById('loteId').value = '';
        piezaSelect.selectedIndex = 0;
        document.querySelectorAll('input[name="defecto"]').forEach(input => input.checked = false);
        document.getElementById('detalles').value = '';
        quantityInput.value = 1;
        currencyBtns[0].click();
        document.querySelectorAll('.summary-value').forEach(el => el.textContent = '-');
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

    async function buscarLote() {
        const loteId = document.getElementById('loteId').value;
        if (!loteId)
            return mostrarError('Ingrese un ID de Lote');

        try {
            const response = await fetch(`/api/v1/lotes/${loteId}`, {
                headers: {'Authorization': `Bearer ${token}`}
            });

            if (!response.ok)
                throw new Error('Lote no encontrado');

            const lote = await response.json();
            mostrarExito(`Lote encontrado: ${lote.nombre}`);

        } catch (error) {
            mostrarError(error.message);
        }
    }

    async function registrarRechazo() {
        // Implementación similar a procesarDefecto con lógica específica
        mostrarExito('Pieza rechazada registrada (simulación)');
    }
});