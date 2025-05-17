document.addEventListener('DOMContentLoaded', () => {

    const token = localStorage.getItem('authToken');
    if (!token) {
        window.location.href = '/login';
        return;
    }

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

    const defectosListContainer = document.querySelector('.defectos-list');
    const nuevoDefectoTextarea = document.getElementById('nuevo-defecto-texto');
    const addBtn = document.getElementById('add-btn');
    const clearBtnNuevo = document.getElementById('clear-btn-nuevo');
    let editandoId = null;

    // Función para crear elementos de defecto
    function crearDefectoItem(defecto) {
        const item = document.createElement('div');
        item.classList.add('defecto-item');
        item.dataset.idDefecto = defecto.idTipoDefecto;

        const actionsDiv = document.createElement('div');
        actionsDiv.classList.add('defecto-actions');

        const editBtn = document.createElement('button');
        editBtn.classList.add('action-btn', 'edit-btn');
        editBtn.innerHTML = '<i class="fas fa-pencil-alt"></i>';

        const deleteBtn = document.createElement('button');
        deleteBtn.classList.add('action-btn', 'delete-btn');
        deleteBtn.innerHTML = '<i class="fas fa-trash-alt"></i>';

        actionsDiv.appendChild(editBtn);
        actionsDiv.appendChild(deleteBtn);

        const contentDiv = document.createElement('div');
        contentDiv.classList.add('defecto-content');

        const numberSpan = document.createElement('span');
        numberSpan.classList.add('defecto-number');
        numberSpan.textContent = defecto.idTipoDefecto.toString().padStart(2, '0');

        const textSpan = document.createElement('span');
        textSpan.classList.add('defecto-text');
        textSpan.textContent = defecto.nombre;

        contentDiv.appendChild(numberSpan);
        contentDiv.appendChild(textSpan);

        item.appendChild(actionsDiv);
        item.appendChild(contentDiv);

        return item;
    }

    // Función para cargar defectos desde el backend
    async function cargarDefectos() {
        try {
            defectosListContainer.innerHTML = '<div class="loading-placeholder">Cargando defectos...</div>';

            const response = await fetch('/api/v1/tipo-defectos', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok)
                throw new Error('Error al cargar defectos');

            const defectos = await response.json();

            defectosListContainer.innerHTML = '';
            if (defectos.length === 0) {
                defectosListContainer.innerHTML = '<div class="loading-placeholder">No hay defectos registrados</div>';
            } else {
                defectos.forEach(defecto => {
                    const item = crearDefectoItem(defecto);
                    defectosListContainer.appendChild(item);
                });
            }
        } catch (error) {
            console.error('Error:', error);
            defectosListContainer.innerHTML = `<div class="error-placeholder">${error.message}</div>`;
        }
    }

    // Evento para añadir/actualizar defectos
    addBtn.addEventListener('click', async () => {
        const nombre = nuevoDefectoTextarea.value.trim();

        if (!nombre) {
            alert('Ingrese el nombre del defecto');
            return;
        }

        try {
            const url = editandoId
                    ? `/api/v1/tipo-defectos/${editandoId}`
                    : '/api/v1/tipo-defectos';

            const method = editandoId ? 'PUT' : 'POST';

            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({nombre})
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Error al guardar');
            }

            // Resetear formulario
            nuevoDefectoTextarea.value = '';
            editandoId = null;
            addBtn.innerHTML = 'Añadir Tipo de Defecto <i class="fas fa-plus"></i>';

            await cargarDefectos();
            alert(editandoId ? '¡Defecto actualizado!' : '¡Defecto creado!');

        } catch (error) {
            alert(`Error: ${error.message}`);
            console.error('Error:', error);
        }
    });

    // Eventos para editar/eliminar
    defectosListContainer.addEventListener('click', async (e) => {
        // Manejar edición
        const editButton = e.target.closest('.edit-btn');
        if (editButton) {
            const defectoItem = editButton.closest('.defecto-item');
            const defectoId = defectoItem.dataset.idDefecto;
            const defectoTexto = defectoItem.querySelector('.defecto-text').textContent;

            nuevoDefectoTextarea.value = defectoTexto;
            editandoId = defectoId;
            addBtn.innerHTML = 'Actualizar Defecto <i class="fas fa-save"></i>';
            return;
        }

        // Manejar eliminación
        const deleteButton = e.target.closest('.delete-btn');
        if (deleteButton) {
            const defectoItem = deleteButton.closest('.defecto-item');
            const defectoId = defectoItem.dataset.idDefecto;

            if (confirm('¿Está seguro de eliminar este tipo de defecto?')) {
                try {
                    const response = await fetch(`/api/v1/tipo-defectos/${defectoId}`, {
                        method: 'DELETE',
                        headers: {'Authorization': `Bearer ${token}`}
                    });

                    if (response.status === 204) {
                        await cargarDefectos();
                        alert('Tipo de defecto eliminado exitosamente');
                        return;
                    }

                    if (response.status === 409) {
                        const errorData = await response.json();
                        throw new Error(errorData.message);
                    }

                    if (!response.ok) {
                        throw new Error(`Error ${response.status}: ${response.statusText}`);
                    }

                } catch (error) {
                    alert(error.message);
                    console.error('Error eliminando:', error);
                }
            }
        }
    });

    // Limpiar formulario
    clearBtnNuevo.addEventListener('click', () => {
        nuevoDefectoTextarea.value = '';
        editandoId = null;
        addBtn.innerHTML = 'Añadir Tipo de Defecto <i class="fas fa-plus"></i>';
    });


    // Cargar defectos al iniciar
    cargarDefectos();
});