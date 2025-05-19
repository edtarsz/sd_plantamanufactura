document.addEventListener('DOMContentLoaded', () => {
    const menuToggle = document.querySelector('.menu-toggle');
    const dropdownMenu = document.querySelector('.dropdown-menu');

    // Toggle del menú
    menuToggle.addEventListener('click', function (e) {
        e.stopPropagation();
        dropdownMenu.classList.toggle('active');
    });

    // Cerrar menú al hacer click fuera
    document.addEventListener('click', function (e) {
        if (!menuToggle.contains(e.target) && !dropdownMenu.contains(e.target)) {
            dropdownMenu.classList.remove('active');
        }
    });

    // Cerrar menú al hacer scroll
    window.addEventListener('scroll', function () {
        dropdownMenu.classList.remove('active');
    });

    // Prevenir cierre al hacer click dentro del menú
    dropdownMenu.addEventListener('click', function (e) {
        e.stopPropagation();
    });

    let allReports = [];
    let currentCurrency = 'USD';
    let conversionRate = 1;
    let currentFilters = {};
    let selectedReportId = null;

    // Elementos del DOM
    const tableBody = document.querySelector('.table-body');
    const summaryContent = document.querySelector('.summary-content');
    const currencyUSD = document.getElementById('currency-usd');
    const currencyMXN = document.getElementById('currency-mxn');

    // Cargar datos iniciales
    loadReports();
    loadConversionRate();

    // Event Listeners
    document.querySelectorAll('select').forEach(select => {
        select.addEventListener('change', applyFilters);
    });

    document.querySelectorAll('.currency-btn').forEach(btn => {
        btn.addEventListener('click', handleCurrencyChange);
    });

    document.getElementById('export-btn-main').addEventListener('click', handleExport);
    document.getElementById('export-btn-date').addEventListener('click', handleDateExport);

    // Cargar reportes

    async function loadReports() {
        try {
            const userId = getUserId();

            if (!userId) {
                window.location.href = '/login'; // Redirigir si no hay usuario
                return;
            }

            const response = await fetch(`/api/v1/reportes`);

            if (!response.ok) {
                throw new Error('Error al cargar reportes');
            }

            allReports = await response.json();
            applyFilters();
        } catch (error) {
            console.error('Error cargando reportes:', error);
            // Mostrar mensaje de error en la UI
            tableBody.innerHTML = `<div class="error">${error.message}</div>`;
        }
    }

    async function loadReportDetails(reportId) {
        try {
            const response = await fetch(`/api/v1/reportes/detalle/${reportId}`);
            return await response.json();
        } catch (error) {
            console.error('Error cargando detalles:', error);
            return null;
        }
    }

    // Aplicar filtros
    function applyFilters() {
        currentFilters = {
            costSort: document.getElementById('filter-lote').value,
            dateFilter: document.getElementById('filter-fecha').value,
            inspector: document.getElementById('filter-inspector').value,
            lote: document.getElementById('filter-lote').value
        };

        let filtered = [...allReports];

        // Aplicar filtros
        if (currentFilters.dateFilter) {
            filtered = filterByDate(filtered, currentFilters.dateFilter);
        }

        if (currentFilters.inspector) {
            filtered = filtered.filter(r => r.inspector === currentFilters.inspector);
        }

        if (currentFilters.lote) {
            filtered = filtered.filter(r => r.loteId === currentFilters.lote);
        }

        // Ordenar
        if (currentFilters.costSort === 'loteA') {
            filtered.sort((a, b) => a.costoTotal - b.costoTotal);
        } else if (currentFilters.costSort === 'loteB') {
            filtered.sort((a, b) => b.costoTotal - a.costoTotal);
        }

        renderTable(filtered);
        updateSummary(filtered);
    }

    // Renderizar tabla
    function renderTable(reports) {
        tableBody.innerHTML = '';

        reports.forEach(report => {
            const row = document.createElement('div');
            row.className = `table-row ${selectedReportId === report.idReporte ? 'selected' : ''}`;
            row.innerHTML = `
            <div class="td id" data-label="ID">${report.idReporte}</div>
            <div class="td costo" data-label="COSTO TOTAL">${report.costoTotal}</div>
            <div class="td inspector" data-label="INSPECTOR">${report.inspector}</div>
            <div class="td lote" data-label="LOTE">${report.loteId}</div>
        `;

            row.addEventListener('click', async () => {
                try {
                    const response = await fetch(`/api/v1/reportes/${report.idReporte}`);
                    if (!response.ok)
                        throw new Error('Error cargando detalle');

                    const reporteDetalle = await response.json();
                    updateSummary(reporteDetalle);

                } catch (error) {
                    console.error(error);
                    summaryContent.innerHTML = `<div class="error">${error.message}</div>`;
                }
            });

            tableBody.appendChild(row);
        });
    }

    // Actualizar resumen
    function updateSummary(data) {
        if (Array.isArray(data)) {
            const total = data.reduce((sum, r) => sum + r.costoTotal, 0);
            summaryContent.innerHTML = generateGeneralSummary(data, total);
        } else {
            summaryContent.innerHTML = generateReportSummary(data);
        }
    }

    function generateGeneralSummary(reports, total) {
        const average = reports.length > 0 ? total / reports.length : 0;

        return `
        <div class="summary-row">
            <span class="summary-label">Reportes Totales:</span>
            <span>${reports.length}</span>
        </div>
        <div class="summary-row">
            <span class="summary-label">Costo Total:</span>
            <span>${formatCurrency(total * conversionRate)}</span>
        </div>
        <div class="summary-row">
            <span class="summary-label">Costo Promedio:</span>
            <span>${formatCurrency(average * conversionRate)}</span>
        </div>
        <div class="summary-row">
            <span class="summary-label">Lote con Mayor Costo:</span>
            <span>${getMostExpensiveLot(reports)}</span>
        </div>
    `;
    }

    function getMostExpensiveLot(reports) {
        if (reports.length === 0)
            return 'N/A';

        const lotMap = reports.reduce((acc, report) => {
            acc[report.loteId] = (acc[report.loteId] || 0) + report.costoTotal;
            return acc;
        }, {});

        const [maxLot] = Object.entries(lotMap).reduce(
                (max, entry) => entry[1] > max[1] ? entry : max,
                ['', -Infinity]
                );

        return maxLot || 'N/A';
    }

    // Funciones de ayuda
    function formatCurrency(amount) {
        if (isNaN(amount))
            return "$0.00";

        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: currentCurrency,
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(amount);
    }

    async function loadConversionRate() {
        if (currentCurrency === 'MXN') {
            const response = await fetch('/api/v1/conversion/usd-a-mxn');
            const data = await response.json();
            conversionRate = data.tasa;
        }
    }

    function handleCurrencyChange(e) {
        currentCurrency = e.target.id === 'currency-usd' ? 'USD' : 'MXN';
        currencyUSD.classList.toggle('active', currentCurrency === 'USD');
        currencyMXN.classList.toggle('active', currentCurrency === 'MXN');
        loadConversionRate().then(applyFilters);
    }

    async function handleExport() {
        const format = document.getElementById('export-format-select').value;
        const params = new URLSearchParams({
            ...currentFilters,
            currency: currentCurrency,
            format
        });

        window.location.href = `/api/v1/reportes/exportar?${params}`;
    }

    function handleDateExport() {
        const start = document.getElementById('date-inicial').value;
        const end = document.getElementById('date-final').value;

        if (!start || !end) {
            alert('Seleccione ambas fechas');
            return;
        }

        window.location.href = `/api/v1/reportes/exportar?fechaInicio=${start}&fechaFin=${end}&currency=${currentCurrency}`;
    }

    function getUserId() {
        const token = localStorage.getItem('authToken');

        if (!token) {
            window.location.href = '/login';
            return null;
        }

        try {
            // Decodificar el payload del JWT
            const payload = JSON.parse(atob(token.split('.')[1]));

            // Obtener el userId del claim
            return payload.userId; // Esto coincide con el claim "userId" del Java

        } catch (error) {
            console.error('Error decodificando token:', error);
            localStorage.removeItem('authToken');
            window.location.href = '/login';
            return null;
        }
    }

    function filterByDate(reports, filterType) {
        const today = new Date();

        return reports.filter(report => {
            const reportDate = new Date(report.fecha);
            switch (filterType) {
                case 'hoy':
                    return reportDate.toDateString() === today.toDateString();
                case 'semana':
                    const oneWeekAgo = new Date(today);
                    oneWeekAgo.setDate(today.getDate() - 7);
                    return reportDate >= oneWeekAgo;
                case 'mes':
                    const oneMonthAgo = new Date(today);
                    oneMonthAgo.setMonth(today.getMonth() - 1);
                    return reportDate >= oneMonthAgo;
                default:
                    return true;
            }
        });
    }

    function generateReportSummary(report) {
        const fechaFormateada = new Date(report.fecha).toLocaleDateString('es-MX', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        return `
        <div class="summary-header-detalle">
            <h3>Reporte #${report.idReporte}</h3>
            <span class="fecha-reporte">${fechaFormateada}</span>
        </div>
        
        <div class="detalle-section">
            <div class="detalle-item">
                <span class="detalle-label">Inspector:</span>
                <span class="detalle-value">${report.inspector}</span>
            </div>
            <div class="detalle-item">
                <span class="detalle-label">Lote:</span>
                <span class="detalle-value">${report.loteId}</span>
            </div>
            <div class="detalle-item">
                <span class="detalle-label">Total:</span>
                <span class="detalle-value highlight">${formatCurrency(report.costoTotal * conversionRate)}</span>
            </div>
        </div>

        <div class="defectos-container">
            <h4>Defectos Detectados (${report.defectos.length})</h4>
            ${report.defectos.map(defecto => `
                <div class="defecto-card">
                    <div class="defecto-header">
                        <span class="propiedad-label">Tipo de defecto:</span>
                        <span class="tipo-defecto ${defecto.tipoDefecto.nombre.toLowerCase()}">
                            ${defecto.tipoDefecto.nombre}
                        </span>
                    </div>
                    
                    <div class="defecto-detalles">
                        <div class="defecto-propiedad">
                            <span class="propiedad-label">Cantidad:</span>
                            <span class="propiedad-valor">${defecto.cantidad_piezas} piezas</span>
                        </div>
                        <div class="defecto-propiedad">
                            <span class="propiedad-label">Descripción:</span>
                            <span class="propiedad-valor">${defecto.detalles || 'Sin descripción'}</span>
                        </div>
                        <div class="defecto-propiedad">
                            <span class="propiedad-label">Inspector:</span>
                            <span class="propiedad-valor">${report.inspector}</span>
                        </div>
        
                        <div class="defecto-propiedad">
                            <span class="propiedad-label">Costo:</span>
                            <span class="defecto-costo">
                            ${formatCurrency(defecto.costo * conversionRate)}
                        </span>
                        </div>
                    </div>
                </div>
            `).join('')}
        </div>
    `;
    }
});