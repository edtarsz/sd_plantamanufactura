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
    let uniqueInspectors = [];
    let uniqueLotes = [];
    let currentCurrency = 'USD';
    let conversionRate = 1;
    let currentFilters = {};
    let selectedReportId = null; // Track selected report

    // Elementos del DOM
    const tableBody = document.querySelector('.table-body');
    const summaryContent = document.querySelector('.summary-content');
    const currencyUSD = document.getElementById('currency-usd');
    const currencyMXN = document.getElementById('currency-mxn');
    const exportBtn = document.getElementById('export-btn-main');
    const filterCosto = document.getElementById('filter-costo');
    const filterFecha = document.getElementById('filter-fecha');
    const filterInspector = document.getElementById('filter-inspector');
    const filterLote = document.getElementById('filter-lote');

    // Actualizar texto del botón de exportar
    function updateExportButtonText() {
        if (selectedReportId) {
            exportBtn.innerHTML = `
                Exportar Reporte #${selectedReportId}
                <i class="fas fa-download"></i>
            `;
            exportBtn.classList.add('active');
        } else {
            exportBtn.innerHTML = `
                Exportar
                <i class="fas fa-download"></i>
            `;
            exportBtn.classList.remove('active');
        }
    }

    // Cargar datos iniciales
    loadReports();
    loadConversionRate();

    // Event Listeners
    filterCosto.addEventListener('change', applyFilters);
    filterFecha.addEventListener('change', applyFilters);
    filterInspector.addEventListener('change', applyFilters);
    filterLote.addEventListener('change', applyFilters);

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
            
            // Extract unique inspectors and lotes for filters
            uniqueInspectors = [...new Set(allReports.map(report => report.inspector))].filter(Boolean);
            uniqueLotes = [...new Set(allReports.map(report => report.loteId))].filter(Boolean);
            
            // Populate filter dropdowns
            populateFilterDropdowns();
            
            renderTable(allReports);
            updateSummary(allReports);
        } catch (error) {
            console.error('Error cargando reportes:', error);
            // Mostrar mensaje de error en la UI
            tableBody.innerHTML = `<div class="error">${error.message}</div>`;
        }
    }
    
    // Populate the filter dropdowns with data from the reports
    function populateFilterDropdowns() {
        // Clear existing options (keep the first empty option)
        while (filterInspector.options.length > 1) {
            filterInspector.remove(1);
        }
        
        while (filterLote.options.length > 1) {
            filterLote.remove(1);
        }
        
        // Add inspector options
        uniqueInspectors.forEach(inspector => {
            const option = document.createElement('option');
            option.value = inspector;
            option.textContent = inspector;
            filterInspector.appendChild(option);
        });
        
        // Add lote options
        uniqueLotes.forEach(lote => {
            const option = document.createElement('option');
            option.value = lote;
            option.textContent = lote;
            filterLote.appendChild(option);
        });
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
        // Clear selected report when filters change
        selectedReportId = null;
        updateExportButtonText();
        
        currentFilters = {
            costSort: filterCosto.value,
            dateFilter: filterFecha.value,
            inspector: filterInspector.value,
            lote: filterLote.value
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
        if (currentFilters.costSort === 'asc') {
            filtered.sort((a, b) => a.costoTotal - b.costoTotal);
        } else if (currentFilters.costSort === 'desc') {
            filtered.sort((a, b) => b.costoTotal - a.costoTotal);
        }

        renderTable(filtered);
        updateSummary(filtered);
    }

    // Renderizar tabla
    function renderTable(reports) {
        tableBody.innerHTML = '';

        if (reports.length === 0) {
            tableBody.innerHTML = '<div class="no-data">No se encontraron reportes que coincidan con los criterios de búsqueda.</div>';
            return;
        }

        reports.forEach(report => {
            const row = document.createElement('div');
            row.className = 'table-row';
            row.dataset.reportId = report.idReporte;
            row.innerHTML = `
                <div class="td id" data-label="ID">${report.idReporte}</div>
                <div class="td costo" data-label="COSTO TOTAL">${formatCurrency(report.costoTotal * conversionRate)}</div>
                <div class="td inspector" data-label="INSPECTOR">${report.inspector}</div>
                <div class="td lote" data-label="LOTE">${report.loteId}</div>
            `;

            row.addEventListener('click', async () => {
                // Clear previous selections
                document.querySelectorAll('.table-row').forEach(r => r.classList.remove('selected'));
                
                // Mark this row as selected
                row.classList.add('selected');
                
                // Update the selected report ID
                selectedReportId = report.idReporte;
                console.log("Selected report ID:", selectedReportId);
                
                // Update the export button text
                updateExportButtonText();
                
                try {
                    const response = await fetch(`/api/v1/reportes/${report.idReporte}`);
                    if (!response.ok) {
                        throw new Error('Error cargando detalle');
                    }
                    
                    const reporteDetalle = await response.json();
                    updateSummary(reporteDetalle);
                } catch (error) {
                    console.error('Error loading report details:', error);
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

    // Fix currency conversion to match the working implementation
    async function loadConversionRate() {
        try {
            if (currentCurrency === 'MXN') {
                // Use the same endpoint that works in registroDefectos.js
                const response = await fetch(`/api/v1/conversion/rate?from=USD&to=MXN`);
                const data = await response.json();
                
                if (data && data.exchangeRate) {
                    conversionRate = parseFloat(data.exchangeRate);
                    console.log("Conversion rate loaded:", conversionRate);
                } else {
                    console.warn("Exchange rate not found in response, using default");
                    conversionRate = 17.05; // Default fallback rate
                }
            } else {
                conversionRate = 1; // For USD
            }
        } catch (error) {
            console.error("Error loading conversion rate:", error);
            conversionRate = currentCurrency === 'MXN' ? 17.05 : 1; // Fallback rate
        }
    }

    function handleCurrencyChange(e) {
        currentCurrency = e.target.id === 'currency-usd' ? 'USD' : 'MXN';
        currencyUSD.classList.toggle('active', currentCurrency === 'USD');
        currencyMXN.classList.toggle('active', currentCurrency === 'MXN');
        loadConversionRate().then(applyFilters);
    }

    async function handleExport() {
        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                alert('Usuario no autenticado');
                window.location.href = '/login';
                return;
            }

            // Get user ID from token
            const userId = getUserId(token);
            const format = document.getElementById('export-format-select').value;
            
            // Check if a specific report is selected
            if (selectedReportId) {
                // Export just the selected report
                console.log(`Exporting single report ID: ${selectedReportId}`);
                
                // Show loading indicator
                exportBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Exportando...';
                exportBtn.disabled = true;
                
                const params = new URLSearchParams();
                params.append('format', format);
                params.append('currency', currentCurrency);
                params.append('reporteId', selectedReportId); // Add the selected report ID
                params.append('idUsuario', userId); // Required by the backend
                
                const url = `/api/v1/reportes/exportar?${params.toString()}`;
                console.log("URL for exporting single report:", url);
                
                // Use window.open to open in new tab to see any errors
                window.open(url, '_blank');
                
                // Reset button after a delay
                setTimeout(() => {
                    updateExportButtonText();
                    exportBtn.disabled = false;
                }, 1500);
                
                return;
            }
            
            // If no report is selected, export based on filters (original behavior)
            const params = new URLSearchParams();
            params.append('format', format);
            params.append('currency', currentCurrency);
            params.append('idUsuario', userId);

            // Add filters if present
            if (currentFilters.costSort === 'asc')
                params.append('costSort', 'loteA');  // Backend expects 'loteA' for ascending
            else if (currentFilters.costSort === 'desc')
                params.append('costSort', 'loteB');  // Backend expects 'loteB' for descending
                
            if (currentFilters.dateFilter)
                params.append('dateFilter', currentFilters.dateFilter);
            if (currentFilters.inspector)
                params.append('inspector', currentFilters.inspector);
            if (currentFilters.lote)
                params.append('lote', currentFilters.lote);

            // Redirect to download
            const url = `/api/v1/reportes/exportar?${params.toString()}`;
            console.log("URL for exporting filtered reports:", url);

            window.open(url, '_blank');
        } catch (error) {
            console.error('Error exporting:', error);
            alert('Error generating report: ' + error.message);
            
            // Reset button
            updateExportButtonText();
            exportBtn.disabled = false;
        }
    }

    function handleDateExport() {
        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                alert('Usuario no autenticado');
                window.location.href = '/login';
                return;
            }

            const start = document.getElementById('date-inicial').value;
            const end = document.getElementById('date-final').value;
            const format = document.getElementById('export-format-select').value;

            if (!start || !end) {
                alert('Seleccione ambas fechas');
                return;
            }

            // Show loading indicator
            const exportBtn = document.getElementById('export-btn-date');
            exportBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Exportando...';
            exportBtn.disabled = true;

            const params = new URLSearchParams();
            params.append('format', format || 'pdf');
            params.append('currency', currentCurrency);
            params.append('allUsers', 'true'); // Special parameter to include all users' reports
            params.append('fechaInicio', start);
            params.append('fechaFin', end);

            const url = `/api/v1/reportes/exportar?${params.toString()}`;
            console.log("URL for date range export:", url);
            
            // Use window.open to allow download in new tab
            window.open(url, '_blank');
            
            // Reset button after a delay
            setTimeout(() => {
                exportBtn.innerHTML = 'Exportar Rango <i class="fas fa-download"></i>';
                exportBtn.disabled = false;
            }, 1500);
        } catch (error) {
            console.error('Error exporting by date:', error);
            alert('Error al generar reporte: ' + error.message);
            
            // Reset button if error
            const exportBtn = document.getElementById('export-btn-date');
            exportBtn.innerHTML = 'Exportar Rango <i class="fas fa-download"></i>';
            exportBtn.disabled = false;
        }
    }

    // Function to get user ID from token or localStorage
    function getUserId(token = localStorage.getItem('authToken')) {
        try {
            if (!token) return null;
            
            // Try to get from token first
            const payload = JSON.parse(atob(token.split('.')[1]));
            console.log("Token payload:", payload);
            
            if (payload && payload.userId) {
                return payload.userId;
            }
            
            // Fallback: Try to get from localStorage
            const storedUserId = localStorage.getItem('userId');
            if (storedUserId) {
                return parseInt(storedUserId, 10);
            }
            
            // If all fails, return default
            console.warn("Could not determine user ID, using default: 1");
            return 1;
        } catch (error) {
            console.error('Error getting userId:', error);
            return 1; // Default fallback
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