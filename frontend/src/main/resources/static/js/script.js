fetch('api/v1/usuarios')
        .then(response => response.json())
        .then(data => {
            console.log(data.mensaje);
            document.getElementById("mensaje").innerText = data[0].fullName;
        });