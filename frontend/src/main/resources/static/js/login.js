document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const errorElement = document.getElementById('errorMessage');
    const successElement = document.getElementById('successMessage');

    // Mostrar mensaje de registro exitoso
    if (urlParams.get('registered') === 'true') {
        showSuccess('¡Registro exitoso! Por favor inicia sesión');
    }

    // Manejar envío del formulario
    document.getElementById('loginForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        clearMessages();

        const credentials = {
            username: document.getElementById('username').value.trim(),
            password: document.getElementById('password').value.trim()
        };

        // Validación básica de campos
        if (!credentials.username || !credentials.password) {
            showError('Todos los campos son requeridos');
            return;
        }

        try {
            const response = await fetch('/api/v1/auth/token', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(credentials)
            });

            const data = await response.json(); // Siempre parsear como JSON

            if (!response.ok) {
                throw new Error(data.error || 'Error de autenticación');
            }

            // Guardar token en localStorage y cookie
            localStorage.setItem('authToken', data.token);
            document.cookie = `authToken=${data.token}; path=/; Secure; SameSite=Strict`;

            // Forzar recarga completa para aplicar seguridad
            window.location.replace('/index');

        } catch (error) {
            showError(error.message || 'Error en el servidor');
        }
    });

    function showError(message) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';
        setTimeout(() => errorElement.style.display = 'none', 5000);
    }

    function showSuccess(message) {
        successElement.textContent = message;
        successElement.style.display = 'block';
        setTimeout(() => successElement.style.display = 'none', 5000);
    }

    function clearMessages() {
        errorElement.style.display = 'none';
        successElement.style.display = 'none';
    }
});