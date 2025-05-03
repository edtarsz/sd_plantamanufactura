document.addEventListener('DOMContentLoaded', () => {

    const currencyUsdBtn = document.getElementById('currency-usd');
    const currencyMxnBtn = document.getElementById('currency-mxn');
    const filterSelects = document.querySelectorAll('.filters select');
    const tableBody = document.querySelector('.table-body');
    const summaryContent = document.querySelector('.summary-content');
    const exportFormatSelect = document.getElementById('export-format-select');
    const exportBtnMain = document.getElementById('export-btn-main');
    const dateInicialInput = document.getElementById('date-inicial');
    const dateFinalInput = document.getElementById('date-final');
    const exportBtnDate = document.getElementById('export-btn-date');

    let currentCurrency = 'USD';
    let currentFilters = {};


    function getFilters() {
        const filters = {
            moneda: currentCurrency
        };
        filterSelects.forEach(select => {
            filters[select.id.replace('filter-', '')] = select.value;
        });
        return filters;
    }

    function displayTableData(data = []) {
        tableBody.innerHTML = '';

        if (data.length === 0) {
            tableBody.innerHTML = '<div class="loading-placeholder" style="text-align: center; padding: 20px; color: #555;">No se encontraron datos con los filtros seleccionados.</div>';
            return;
        }

        data.forEach(item => {
            const row = document.createElement('div');
            row.classList.add('table-row', 'dark');

            const idCell = document.createElement('div');
            idCell.classList.add('td', 'id');
            idCell.textContent = item.id || 'N/A';
            row.appendChild(idCell);

            const piezaCell = document.createElement('div');
            piezaCell.classList.add('td', 'pieza');
            piezaCell.textContent = item.pieza || 'N/A';
            row.appendChild(piezaCell);

            const inspectorCell = document.createElement('div');
            inspectorCell.classList.add('td', 'inspector');
            inspectorCell.textContent = item.inspector || 'N/A';
            row.appendChild(inspectorCell);

            const loteCell = document.createElement('div');
            loteCell.classList.add('td', 'lote');
            loteCell.textContent = item.lote || 'N/A';
            row.appendChild(loteCell);

            const defectoCell = document.createElement('div');
            defectoCell.classList.add('td', 'defecto');
            defectoCell.textContent = item.tipoDefecto || 'N/A';
            row.appendChild(defectoCell);

            tableBody.appendChild(row);
        });
    }

    function displaySummaryData(summary = null) {
        summaryContent.innerHTML = '';

        if (!summary) {
             summaryContent.innerHTML = '<div class="loading-placeholder" style="text-align: center; padding: 20px; color: #aaa;">No hay datos para el resumen.</div>';
            return;
        }

        function createSummaryRow(label, value) {
            const row = document.createElement('div');
            row.classList.add('summary-row');
            const labelSpan = document.createElement('span');
            labelSpan.classList.add('summary-label');
            labelSpan.textContent = label + ':';
            const valueSpan = document.createElement('span');
            valueSpan.classList.add('summary-value');
            valueSpan.textContent = value || 'N/A';
            row.appendChild(labelSpan);
            row.appendChild(valueSpan);
            return row;
        }
         function createDivider() {
             const div = document.createElement('div');
             div.classList.add('summary-divider');
             return div;
         }

        summaryContent.appendChild(createSummaryRow('ID del Lote', summary.loteId));
        summaryContent.appendChild(createSummaryRow('Total Defectos', summary.totalDefectos));
        summaryContent.appendChild(createSummaryRow('Costo Total', `${summary.costoTotal || 0} ${currentCurrency}`));
        summaryContent.appendChild(createDivider());

        if (summary.detallesPorDefecto && summary.detallesPorDefecto.length > 0) {
             summary.detallesPorDefecto.forEach(detalle => {
                 summaryContent.appendChild(createSummaryRow('Defecto', detalle.tipo));
                 summaryContent.appendChild(createSummaryRow('Cantidad', detalle.cantidad));
                 summaryContent.appendChild(createSummaryRow('Costo', `${detalle.costo || 0} ${currentCurrency}`));
                 summaryContent.appendChild(createDivider());
             });
        }
    }

    async function cargarReportes() {
        currentFilters = getFilters();
        console.log('Cargando reportes con filtros:', currentFilters);
        tableBody.innerHTML = '<div class="loading-placeholder" style="text-align: center; padding: 20px; color: #555;">Cargando datos...</div>'; // Mostrar carga
        summaryContent.innerHTML = '<div class="loading-placeholder" style="text-align: center; padding: 20px; color: #aaa;">Cargando resumen...</div>';

        // Aquí tenemos que hacer llamada a la API backend
        // const url = `/api/v1/reportes?moneda=${currentFilters.moneda}&fecha=${currentFilters.fecha}...`; // Construir URL con filtros
        try {
            // const response = await fetch(url); // Descomentar para llamada real
            // if (!response.ok) throw new Error('Error al cargar datos');
            // const result = await response.json();

            // Datos de ejemplo para simular por ahora, aqui debemos recuperar de la BD
            await new Promise(resolve => setTimeout(resolve, 700));
             const result = {
                 tableData: [
                     { id: 'D001', pieza: 'Cepillo X', inspector: 'Ana C.', lote: 'LOTE01', tipoDefecto: 'Rayado' },
                     { id: 'D002', pieza: 'Esponja Y', inspector: 'Juan P.', lote: 'LOTE02', tipoDefecto: 'Manchado' },
                     { id: 'D003', pieza: 'Cepillo X', inspector: 'Ana C.', lote: 'LOTE01', tipoDefecto: 'Roto' }
                 ],
                 summaryData: {
                    loteId: currentFilters.lote || 'Varios',
                    totalDefectos: 3,
                    costoTotal: 150.75,
                    detallesPorDefecto: [
                        { tipo: 'Rayado', cantidad: 1, costo: 50.25 },
                        { tipo: 'Manchado', cantidad: 1, costo: 40.50 },
                        { tipo: 'Roto', cantidad: 1, costo: 60.00 }
                    ]
                 }
            };

            displayTableData(result.tableData);
            displaySummaryData(result.summaryData);

        } catch (error) {
            console.error("Error cargando reportes:", error);
            tableBody.innerHTML = `<div class="loading-placeholder" style="text-align: center; padding: 20px; color: red;">Error al cargar datos: ${error.message}</div>`;
            summaryContent.innerHTML = `<div class="loading-placeholder" style="text-align: center; padding: 20px; color: orange;">Error al cargar resumen.</div>`;
        }
    }



    currencyUsdBtn.addEventListener('click', () => {
        if (currentCurrency !== 'USD') {
            currencyUsdBtn.classList.add('active');
            currencyMxnBtn.classList.remove('active');
            currentCurrency = 'USD';
            cargarReportes();
        }
    });

    currencyMxnBtn.addEventListener('click', () => {
         if (currentCurrency !== 'MXN') {
            currencyMxnBtn.classList.add('active');
            currencyUsdBtn.classList.remove('active');
            currentCurrency = 'MXN';
            cargarReportes();
        }
    });

    // Cambio en Filtros
    filterSelects.forEach(select => {
        select.addEventListener('change', () => {

            cargarReportes();
        });
    });


    // Botón Exportar Vista Actual
    exportBtnMain.addEventListener('click', () => {
        const format = exportFormatSelect.value;
        const filters = getFilters();
        console.log(`Exportando vista actual en formato ${format} con filtros:`, filters);
        // Aquí vamos a ocupar hacer la llamada fetch al endpoint de exportación, pasando filtros y formato
        alert(`Simulación de exportación en ${format}. Revisa la consola.`);
        // window.location.href = `/api/v1/reportes/exportar?formato=${format}&moneda=${filters.moneda}...`; // Ejemplo
    });

    // Botón Exportar por Fechas
    exportBtnDate.addEventListener('click', () => {
        const fechaInicial = dateInicialInput.value;
        const fechaFinal = dateFinalInput.value;
        const format = 'pdf';

        if (!fechaInicial || !fechaFinal) {
            alert('Por favor, seleccione ambas fechas para exportar por rango.');
            return;
        }
        console.log(`Exportando rango de fechas (${fechaInicial} - ${fechaFinal}) en formato ${format}`);
         alert(`Simulación de exportación por fechas en ${format}. Revisa la consola.`);
         // window.location.href = `/api/v1/reportes/exportar-rango?formato=${format}&inicio=${fechaInicial}&fin=${fechaFinal}`; // Ejemplo
    });

    cargarReportes();

});