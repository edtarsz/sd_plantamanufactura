/**
 * Módulo para la gestión de reportes y visualización en la interfaz de usuario.
 * 
 * Este script maneja la carga, filtrado, visualización y exportación de reportes
 * de defectos. Permite al usuario filtrar por diferentes criterios, seleccionar
 * reportes individuales, y exportarlos en diferentes formatos y monedas.
 * 
 * @author Ramos
 * @version 1.0
 */
document.addEventListener('DOMContentLoaded', () => {
    // Configuración de navegación y menú
    const menuToggle = document.querySelector('.menu-toggle');
    const dropdownMenu = document.querySelector('.dropdown-menu');

    /**
     * Variables y estado global de la aplicación
     */
    let allReports = [];             // Todos los reportes cargados
    let uniqueInspectors = [];       // Lista de inspectores únicos para filtros
    let uniqueLotes = [];            // Lista de lotes únicos para filtros
    let currentCurrency = 'USD';     // Moneda actual para mostrar valores
    let conversionRate = 1;          // Tasa de conversión actual
    let currentFilters = {};         // Filtros actualmente aplicados
    let selectedReportId = null;     // ID del reporte seleccionado actualmente

    /**
     * Referencias a elementos del DOM frecuentemente usados
     */
    const tableBody = document.querySelector('.table-body');
    const summaryContent = document.querySelector('.summary-content');
    const currencyUSD = document.getElementById('currency-usd');
    const currencyMXN = document.getElementById('currency-mxn');
    const exportBtn = document.getElementById('export-btn-main');
    const filterCosto = document.getElementById('filter-costo');
    const filterFecha = document.getElementById('filter-fecha');
    const filterInspector = document.getElementById('filter-inspector');
    const filterLote = document.getElementById('filter-lote');

    // Funciones para manejo del menú
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

    /**
     * Actualiza el texto y estado del botón de exportar.
     * Habilita o deshabilita el botón según haya un reporte seleccionado.
     */
    function updateExportButtonText() {
        if (selectedReportId) {
            exportBtn.innerHTML = `
                Exportar Reporte #${selectedReportId}
                <i class="fas fa-download"></i>
            `;
            exportBtn.classList.add('active');
            exportBtn.disabled = false;
        } else {
            exportBtn.innerHTML = `
                Seleccione un reporte
                <i class="fas fa-download"></i>
            `;
            exportBtn.classList.remove('active');
            exportBtn.disabled = true; // Deshabilitar cuando no hay selección
        }
    }

    // Inicialización: cargar datos y configurar estado inicial
    loadReports();
    loadConversionRate();

    // Event Listeners para filtros y acciones de usuario
    filterCosto.addEventListener('change', applyFilters);
    filterFecha.addEventListener('change', applyFilters);
    filterInspector.addEventListener('change', applyFilters);
    filterLote.addEventListener('change', applyFilters);

    document.querySelectorAll('.currency-btn').forEach(btn => {
        btn.addEventListener('click', handleCurrencyChange);
    });

    document.getElementById('export-btn-main').addEventListener('click', handleExport);
    document.getElementById('export-btn-date').addEventListener('click', handleDateExport);

    /**
     * Carga los reportes desde el backend y los muestra en la interfaz.
     * También extrae datos para los filtros de inspectores y lotes.
     */
    async function loadReports() {
        try {
            const userId = getUserId();

            if (!userId) {
                window.location.href = '/login'; // Redireccionar si no hay usuario autenticado
                return;
            }

            const response = await fetch(`/api/v1/reportes`);

            if (!response.ok) {
                throw new Error('Error al cargar reportes');
            }

            allReports = await response.json();
            
            // Extraer valores únicos para los filtros
            uniqueInspectors = [...new Set(allReports.map(report => report.inspector))].filter(Boolean);
            uniqueLotes = [...new Set(allReports.map(report => report.loteId))].filter(Boolean);
            
            // Llenar los dropdowns de filtros
            populateFilterDropdowns();
            
            renderTable(allReports);
            updateSummary(allReports);
        } catch (error) {
            console.error('Error cargando reportes:', error);
            tableBody.innerHTML = `<div class="error">${error.message}</div>`;
        }
    }
    
    /**
     * Llena los dropdowns de filtros con datos de inspectores y lotes.
     */
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

    /**
     * Aplica filtros a la lista de reportes según las selecciones del usuario.
     * Resetea la selección de reporte actual.
     */
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

    /**
     * Renderiza la tabla de reportes con los datos proporcionados.
     * 
     * @param {Array} reports - Lista de reportes a mostrar
     */
    function renderTable(reports) {
        tableBody.innerHTML = '';
        selectedReportId = null; // Resetear selección cuando la tabla se vuelve a renderizar
        updateExportButtonText(); // Actualizar estado del botón de exportar

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

    /**
     * Actualiza la sección de resumen con la información apropiada.
     * 
     * @param {Object|Array} data - Un reporte individual o un array de reportes
     */
    function updateSummary(data) {
        if (Array.isArray(data)) {
            const total = data.reduce((sum, r) => sum + r.costoTotal, 0);
            summaryContent.innerHTML = generateGeneralSummary(data, total);
        } else {
            summaryContent.innerHTML = generateReportSummary(data);
        }
    }

    /**
     * Genera un resumen general para múltiples reportes.
     * 
     * @param {Array} reports - Lista de reportes
     * @param {number} total - Costo total de todos los reportes
     * @returns {string} HTML para la sección de resumen
     */
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

    /**
     * Encuentra el lote con mayor costo en un conjunto de reportes.
     * 
     * @param {Array} reports - Lista de reportes
     * @returns {string} ID del lote con mayor costo acumulado
     */
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

    /**
     * Formatea un valor numérico como moneda según la configuración actual.
     * 
     * @param {number} amount - Monto a formatear
     * @returns {string} Valor formateado como moneda
     */
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

    /**
     * Carga la tasa de conversión de moneda desde el servicio correspondiente.
     */
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

    /**
     * Maneja el cambio de moneda seleccionada por el usuario.
     * 
     * @param {Event} e - Evento de clic
     */
    function handleCurrencyChange(e) {
        currentCurrency = e.target.id === 'currency-usd' ? 'USD' : 'MXN';
        currencyUSD.classList.toggle('active', currentCurrency === 'USD');
        currencyMXN.classList.toggle('active', currentCurrency === 'MXN');
        loadConversionRate().then(applyFilters);
    }

    /**
     * Maneja la exportación del reporte seleccionado o reportes filtrados.
     */
    async function handleExport() {
        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                alert('Usuario no autenticado');
                window.location.href = '/login';
                return;
            }

            // Verificar si hay un reporte seleccionado
            if (selectedReportId) {
                // Exportar solo el reporte seleccionado
                console.log(`Exportando reporte ID: ${selectedReportId}`);
                
                // Mostrar indicador de carga
                exportBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Exportando...';
                exportBtn.disabled = true;
                
                const params = new URLSearchParams();
                params.append('format', document.getElementById('export-format-select').value || 'pdf');
                params.append('currency', currentCurrency);
                params.append('reporteId', selectedReportId);
                params.append('idUsuario', getUserId(token));
                
                const url = `/api/v1/reportes/exportar?${params.toString()}`;
                console.log("URL para exportación de reporte individual:", url);
                
                window.open(url, '_blank');
                
                // Restablecer botón después de un retraso
                setTimeout(() => {
                    updateExportButtonText();
                }, 1500);
            } else {
                // Mostrar mensaje de que se debe seleccionar un reporte
                alert('Por favor, seleccione un reporte para exportar');
            }
        } catch (error) {
            console.error('Error al exportar:', error);
            alert('Error al generar reporte: ' + error.message);
            
            // Restablecer botón
            updateExportButtonText();
        }
    }

    /**
     * Maneja la exportación de reportes por rango de fechas.
     * Este tipo de exportación incluye reportes de todos los usuarios.
     */
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

    /**
     * Obtiene el ID de usuario del token JWT o localStorage.
     * 
     * @param {string} token - Token JWT opcional (si no se proporciona, se obtiene del localStorage)
     * @returns {number|null} ID del usuario o null si no se puede determinar
     */
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

    /**
     * Filtra reportes por fecha según el tipo de filtro seleccionado.
     * 
     * @param {Array} reports - Lista de reportes a filtrar
     * @param {string} filterType - Tipo de filtro (hoy, semana, mes)
     * @returns {Array} Reportes filtrados por fecha
     */
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

    /**
     * Genera HTML para mostrar el resumen de un reporte individual.
     * 
     * @param {Object} report - Reporte a mostrar
     * @returns {string} HTML para la vista detallada del reporte
     */
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