document.addEventListener('DOMContentLoaded', () => {

    const defectosListContainer = document.querySelector('.defectos-list');
    const nuevoDefectoTextarea = document.getElementById('nuevo-defecto-texto');
    const addBtn = document.getElementById('add-btn');
    const clearBtnNuevo = document.getElementById('clear-btn-nuevo');

    function crearDefectoItem(defecto) {
        const item = document.createElement('div');
        item.classList.add('defecto-item');
        item.dataset.idDefecto = defecto.id;

        const actionsDiv = document.createElement('div');
        actionsDiv.classList.add('defecto-actions');

        const editBtn = document.createElement('button');
        editBtn.classList.add('action-btn', 'edit-btn');
        editBtn.innerHTML = '<i class="fas fa-pencil-alt"></i>';
        editBtn.setAttribute('aria-label', 'Editar defecto'); // Para accesibilidad

        const deleteBtn = document.createElement('button');
        deleteBtn.classList.add('action-btn', 'delete-btn');
        deleteBtn.innerHTML = '<i class="fas fa-trash-alt"></i>';
        deleteBtn.setAttribute('aria-label', 'Eliminar defecto'); // Para accesibilidad

        actionsDiv.appendChild(editBtn);
        actionsDiv.appendChild(deleteBtn);

        const contentDiv = document.createElement('div');
        contentDiv.classList.add('defecto-content');

        const numberSpan = document.createElement('span');
        numberSpan.classList.add('defecto-number');
        numberSpan.textContent = defecto.id.toString().padStart(2, '0');

        const textSpan = document.createElement('span');
        textSpan.classList.add('defecto-text');
        textSpan.textContent = defecto.descripcion;

        contentDiv.appendChild(numberSpan);
        contentDiv.appendChild(textSpan);

        item.appendChild(actionsDiv);
        item.appendChild(contentDiv);

        return item;
    }

    async function cargarDefectos() {
        console.log('Cargando lista de defectos...');
        defectosListContainer.innerHTML = '<div class="loading-placeholder" style="text-align: center; padding: 20px; color: #555;">Cargando defectos...</div>'; // Placeholder

        // Fetch get
        // Aquí tenemos que hacer la llamada para recuperar los defectos
        try {
            //Algo asi:
            // const response = await fetch('/api/v1/catalogo/defectos');
            // if (!response.ok) throw new Error('Error al cargar');
            // const defectos = await response.json();

            // Datos de ejemplo de mientras
            await new Promise(resolve => setTimeout(resolve, 500));
            const defectos = [
                {id: 1, descripcion: 'Rayadura Superficial'},
                {id: 2, descripcion: 'Mancha de Grasa'},
                {id: 3, descripcion: 'Pieza Rota'},
                {id: 4, descripcion: 'Etiqueta Incorrecta'}
            ];

            defectosListContainer.innerHTML = '';
            if (defectos.length === 0) {
                defectosListContainer.innerHTML = '<div class="loading-placeholder" style="text-align: center; padding: 20px; color: #555;">No hay defectos en el catálogo.</div>';
            } else {
                defectos.forEach(defecto => {
                    const itemElement = crearDefectoItem(defecto);
                    defectosListContainer.appendChild(itemElement);
                });
            }

        } catch (error) {
            console.error('Error cargando defectos:', error);
            defectosListContainer.innerHTML = `<div class="loading-placeholder" style="text-align: center; padding: 20px; color: red;">Error al cargar defectos: ${error.message}</div>`;
        }
    }


    addBtn.addEventListener('click', async () => {
        const descripcion = nuevoDefectoTextarea.value.trim();
        if (!descripcion) {
            alert('Por favor, introduzca la descripción del defecto.');
            nuevoDefectoTextarea.focus();
            return;
        }

        console.log('Añadiendo nuevo defecto:', descripcion);

        // Fetch post
        // Aquí lo mismo que el get se ocupa hacer la llamada real al post
        try {
            //Como lo siguiente:
            // const response = await fetch('/api/v1/catalogo/defectos', {
            //     method: 'POST',
            //     headers: { 'Content-Type': 'application/json' },
            //     body: JSON.stringify({ descripcion: descripcion })
            // });
            // if (!response.ok) throw new Error('Error al añadir');
            // const nuevoDefecto = await response.json(); // El backend debería devolver el nuevo defecto con su ID

            await new Promise(resolve => setTimeout(resolve, 300));
            console.log('Defecto añadido (simulado). ID asignado:', Math.floor(Math.random() * 1000));

            nuevoDefectoTextarea.value = '';
            cargarDefectos();

        } catch (error) {
            console.error('Error añadiendo defecto:', error);
            alert(`Error al añadir defecto: ${error.message}`);
        }
    });

    clearBtnNuevo.addEventListener('click', () => {
        nuevoDefectoTextarea.value = '';
    });

    defectosListContainer.addEventListener('click', async (event) => {
        const targetButton = event.target.closest('.action-btn');

        if (!targetButton)
            return;

        const defectoItem = targetButton.closest('.defecto-item');
        const defectoId = defectoItem.dataset.idDefecto;

        if (!defectoId)
            return;

        // Acción: Eliminar
        if (targetButton.classList.contains('delete-btn')) {
            if (confirm(`¿Está seguro de que desea eliminar el defecto con ID ${defectoId}? Esta acción no se puede deshacer.`)) {
                console.log('Eliminando defecto con ID:', defectoId);

                // Fetch delete
                try {
                    //Tipo asi
                    // const response = await fetch(`/api/v1/catalogo/defectos/${defectoId}`, { method: 'DELETE' });
                    // if (!response.ok) throw new Error('Error al eliminar');

                    await new Promise(resolve => setTimeout(resolve, 300));
                    console.log('Defecto eliminado (simulado).');

                    cargarDefectos();

                } catch (error) {
                    console.error('Error eliminando defecto:', error);
                    alert(`Error al eliminar defecto: ${error.message}`);
                }
            }
        }
        // Editar
        else if (targetButton.classList.contains('edit-btn')) {
            const defectoTexto = defectoItem.querySelector('.defecto-text').textContent;
            console.log('Editar defecto ID:', defectoId, 'Texto actual:', defectoTexto);

            // Lógica de Edición
            nuevoDefectoTextarea.value = defectoTexto;
            nuevoDefectoTextarea.focus();

            // Cambiar el botón "Añadir" a "Actualizar"
            // Necesitariamos guardar el ID que estemos editando
            // y cambiar el texto o el event listener del botón.
        }
    });

    cargarDefectos();

});