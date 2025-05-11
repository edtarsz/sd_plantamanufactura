document.getElementById('signupForm').addEventListener('submit', async (e) => {
    e.preventDefault(); // Evita el envío tradicional del formulario

    const user = {
        fullName: document.getElementById('fullName').value,
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (user.password !== confirmPassword) {
        showError('Las contraseñas no coinciden');
        return;
    }

    if (user.password.length < 6) {
        showError('La contraseña debe tener al menos 6 caracteres');
        return;
    }

    try {
        const response = await fetch('/api/v1/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || 'Error en el registro');
        }

        // Registro exitoso - redirigir a login
        window.location.href = '/login?registered=true';
    } catch (error) {
        showError(error.message);
    }
});

function showError(message) {
    const errorElement = document.getElementById('errorMessage');
    errorElement.textContent = message;
    errorElement.style.display = 'block';
}