async function encryptFile() {
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];

    if (!file) {
        alert('Selecione um arquivo para ser criptografado.');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await fetch('/encrypt', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            document.getElementById('status').innerText = 'Arquivo criptografado com sucesso.';
        } else {
            document.getElementById('status').innerText = 'Falha ao criptografar o arquivo';
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('status').innerText = 'Ocorreu um erro.';
    }
}

async function decryptFile() {
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];

    if (!file) {
        alert('Selecione um arquivo para ser descriptografado.');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await fetch('/decrypt', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            document.getElementById('status').innerText = 'Arquivo descriptografado com sucesso.';
        } else {
            document.getElementById('status').innerText = 'Falha ao descriptografar o arquivo.';
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('status').innerText = 'Ocorreu um erro.';
    }
}
