document.addEventListener('DOMContentLoaded', () => {

    const quantityInput = document.querySelector('.quantity-input');
    const decreaseBtn = document.querySelector('.quantity-btn.decrease');
    const increaseBtn = document.querySelector('.quantity-btn.increase');

    decreaseBtn.addEventListener('click', () => {
        let currentValue = parseInt(quantityInput.value);
        if (currentValue > 1) {
            quantityInput.value = currentValue - 1;
        }
    });

    increaseBtn.addEventListener('click', () => {
        let currentValue = parseInt(quantityInput.value);
        quantityInput.value = currentValue + 1;
    });

    const currencyToggle = document.querySelector('.currency-toggle');
    const currencyBtns = currencyToggle.querySelectorAll('.currency-btn');

    currencyToggle.addEventListener('click', (event) => {
        if (event.target.classList.contains('currency-btn')) {
            currencyBtns.forEach(btn => btn.classList.remove('active'));
            event.target.classList.add('active');
            console.log('Moneda seleccionada:', event.target.textContent);
        }
    });

    const clearBtn = document.querySelector('.clear-btn');
    const formSection = document.querySelector('.form-section'); // Contenedor del formulario

    clearBtn.addEventListener('click', () => {
        const inputs = formSection.querySelectorAll('input[type="text"], input[type="radio"], select, textarea');
        inputs.forEach(input => {
            if (input.type === 'radio') {
                input.checked = false;
            } else if (input.tagName === 'SELECT') {
                 if (input.options.length > 0) {
                    input.selectedIndex = 0;
                 }
            } else if (input.classList.contains('quantity-input')){
                 input.value = '1';
            }
             else {
                input.value = '';
            }
        });
         currencyBtns.forEach(btn => btn.classList.remove('active'));
         document.querySelector('.currency-toggle .currency-btn:first-child').classList.add('active'); // Asume USD es el primero

        console.log('Formulario limpiado');
    });


    const processBtn = document.querySelector('.process-btn');

    processBtn.addEventListener('click', () => {
        console.log('Procesando defectos (simulación)...');
         const lote = document.querySelector('.search-input').value;
         const piezaSelect = document.querySelector('.form-group select');
         const pieza = piezaSelect.options[piezaSelect.selectedIndex].text;
         const defectoRadio = document.querySelector('.radio-option input[name="defect"]:checked');
         const tipoDefecto = defectoRadio ? defectoRadio.labels[0].textContent : 'N/A';
         const detalles = document.querySelector('.form-group textarea').value;
         const cantidad = document.querySelector('.quantity-input').value;
         const monedaActiva = document.querySelector('.currency-btn.active').textContent;

         if (!lote || !defectoRadio) {
             alert('Por favor, complete ID del Lote y Tipo de Defecto.');
             return;
         }

         console.log('Datos Recogidos:', { lote, pieza, tipoDefecto, detalles, cantidad, monedaActiva });

         alert('Defecto procesado (simulado). Revisar la consola.');
    });

    const rejectBtn = document.querySelector('.reject-btn');

    rejectBtn.addEventListener('click', () => {
        console.log('Registrando pieza rechazada (simulación)...');
         // Recoger TODOS los datos
         // y enviarlos al backend usando fetch
         alert('Simulación de registro de pieza rechazada.');

        /* Ejemplo Fetch (se requiere el endpoint):
        const datosFinales = { ... }; // Objeto con todos los datos a enviar
        fetch('/api/v1/tu-endpoint-registro', {
             method: 'POST',
             headers: { 'Content-Type': 'application/json' },
             body: JSON.stringify(datosFinales)
         })
         .then(response => response.json())
         .then(data => {
             console.log('Éxito:', data);
             alert('Registro exitoso!');
             // Limpiar todo o redirigir
         })
         .catch(error => {
             console.error('Error:', error);
             alert('Error en el registro.');
         });
        */
    });

    const searchBtn = document.querySelector('.search-btn');
    const searchInput = document.querySelector('.search-input');

    searchBtn.addEventListener('click', () => {
        const loteId = searchInput.value;
        if (loteId) {
            console.log(`Buscando información del lote: ${loteId} (simulación)...`);

            alert(`Simulación de búsqueda para lote: ${loteId}`);
        } else {
            alert('Ingrese un ID de Lote para buscar.');
        }
    });


});