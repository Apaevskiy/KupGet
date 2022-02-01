function saveData(blob, fileName)
{
    let a = document.createElement("a");
    document.body.appendChild(a);
    a.style = "display: none";

    let url = window.URL.createObjectURL(blob);
    a.href = url;
    a.download = fileName;
    a.click();
    window.URL.revokeObjectURL(url);
}

let xhr = new XMLHttpRequest();
xhr.open("GET", 'reportExcel');
xhr.responseType = "blob";

xhr.onload = function () {
    saveData(this.response, 'Отчёт');
};
let getExcelFileButton = document.querySelector("#getExcelFileButton");
getExcelFileButton.onclick = function () {
    if ($('#dest').html()!=="") {
        xhr.send();
    } else {
        error(null, messageTF, "Таблица пустая");
    }
};
