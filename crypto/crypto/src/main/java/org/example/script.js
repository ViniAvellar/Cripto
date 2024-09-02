document.addEventListener('DOMContentLoaded', function() {
    const uploadButton = document.getElementById('uploadButton');
    const encryptButton = document.getElementById('encryptButton');
    const decryptButton = document.getElementById('decryptButton');
    const fileInput = document.getElementById('fileInput');


    uploadButton.addEventListener('click', function() {
        handleFileUpload('/upload');
    });

    encryptButton.addEventListener('click', function() {
        handleFileUpload('/upload/encript');
    });

    decryptButton.addEventListener('click', function() {
        handleFileUpload('/upload/decript');
    });

    function handleFileUpload(url) {
        const file = fileInput.files[0];
        if (!file) {
            alert('Por favor selecione um arquivo.');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        fetch(`http://localhost:8080${url}`, {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            alert(`Operação bem sucedida! ${data.message}`);
        })
        .catch(error => {
            console.error('Erro:', error);
            alert(`Um erro ocorreu: ${error.message}`);
        });
    }
});
