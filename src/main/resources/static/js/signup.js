const submitBtn = document.getElementById("submit");

submitBtn.addEventListener('click', () => {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const fullName = document.getElementById('fullName').value;

    const user = {
        username,
        password,
        fullName
    };

    if (password === confirmPassword) {
        const jsonData = JSON.stringify(user);

        fetch('/req/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: jsonData
        })
        .then(response => {
            //
        });
    }
});
