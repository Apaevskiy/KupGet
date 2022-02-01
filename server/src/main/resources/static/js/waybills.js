let addProductButton = document.querySelector("#addProductButton");
let addProductToTableButton = document.querySelector("#addProductToTableButton");
let addWaybillsButton = document.querySelector("#addWaybillsButton");
let backButton = document.querySelector("#backButton");

let productPage = document.querySelector("#productPage");
let waybillsPage = document.querySelector("#waybillsPage");

let numberRecordTF = document.querySelector("#numberRecordTF");
let nameTF = document.querySelector("#nameTF");
let unitTF = document.querySelector("#unitTF");
let statusTF = document.querySelector("#statusTF");
let numberRequireTF = document.querySelector("#numberRequireTF");
let numberReleasedTF = document.querySelector("#numberReleasedTF");
let priceTF = document.querySelector("#priceTF");
let messageTF = document.querySelector("#message");

const headers = new Headers({
    'Content-Type': 'application/json'
});
let token = document.querySelector("#_csrf").attributes.getNamedItem('content').value;
let header = document.querySelector("#_csrf_header").attributes.getNamedItem('content').value;
headers.append(header,token);

let idOfTimer;
let listError = [];

function clearError() {
    for (let i = 0; i < listError.length; i++) {
        if (listError[i].input != null) {
            listError[i].input.classList.remove("invalid");
        }
        if (listError[i].label != null) {
            listError[i].label.innerHTML = null;
            listError[i].label.classList.remove("error");
            listError[i].label.classList.remove("message");
        }
        listError.splice(i--, 1);
    }
    idOfTimer = null;
}

function error(input, label, message) {
    if (input!=null){
        input.classList.add("invalid");
    }
    if(label!=null){
        label.innerHTML = message;
        label.classList.add("error");
    }
    listError.push({label: label, input: input});
    if (idOfTimer !== null) clearTimeout(idOfTimer);
    idOfTimer = setTimeout(clearError, 3000);
}

function message(label, message) {
    label.innerHTML = message;
    label.classList.add("message");
    listError.push({label: label, input: null});
    if (idOfTimer !== null) clearTimeout(idOfTimer);
    idOfTimer = setTimeout(clearError, 3000);
}

function generateCell(element) {
    let td = document.createElement("td");
    td.textContent = element.value;
    element.value = "";
    return td;
}

function getValueOfElement(idTF) {
    let element = document.getElementById(idTF);
    return element.value;
}

let products = [];

function addProductToTable() {
    let tr = document.createElement("tr");
    let sum = document.createElement("td");
    sum.textContent = String((Number(numberReleasedTF.value) * Number(priceTF.value)).toFixed(2));

    products.push({
        status: getValueOfElement('statusTF'),
        numberRequire: getValueOfElement('numberRequireTF'),
        numberReleased: getValueOfElement('numberReleasedTF'),
        price: getValueOfElement('priceTF'),
        type: {
            id: getValueOfElement('numberRecordTF'),
            name: getValueOfElement('nameTF'),
            unit: getValueOfElement('unitTF')
        },
        remaining: getValueOfElement('numberReleasedTF'),
        waybills:null
    })

    tr.appendChild(generateCell(numberRecordTF));
    tr.appendChild(generateCell(nameTF));
    tr.appendChild(generateCell(unitTF));
    tr.appendChild(generateCell(statusTF));
    tr.appendChild(generateCell(numberRequireTF));
    tr.appendChild(generateCell(numberReleasedTF));
    tr.appendChild(generateCell(priceTF));
    tr.appendChild(sum);
    document.getElementById('dest').appendChild(tr);
}

addProductToTableButton.onclick = function () {
    if (numberRecordTF.value !== "" &&
        nameTF.value !== "" &&
        unitTF.value !== "" &&
        statusTF.value !== "" &&
        numberRequireTF.value !== "" &&
        numberReleasedTF.value !== "" &&
        priceTF.value !== "") {

        fetch('/user/typeOfProduct', {
            method: 'PATCH',
            body: JSON.stringify({id: Number(numberRecordTF.value), name: nameTF.value, unit: unitTF.value}),
            headers: headers
        });
        addProductToTable();
        message(messageTF, "Товар\nуспешно\nдобавлен");
        backButton.onclick();
    } else {
        if (numberRecordTF.value === "")
            error(numberRecordTF, document.querySelector("#numberRecordError"), "Заполните поле");
        if (nameTF.value === "")
            error(nameTF, document.querySelector("#nameError"), "Заполните поле");
        if (unitTF.value === "")
            error(unitTF, document.querySelector("#unitError"), "Заполните поле");
        if (statusTF.value === "")
            error(statusTF, document.querySelector("#statusError"), "Вы не выбрали");
        if (numberRequireTF.value === "")
            error(numberRequireTF, document.querySelector("#numberRequireError"), "Заполните поле");
        if (numberReleasedTF.value === "")
            error(numberReleasedTF, document.querySelector("#numberReleasedError"), "Заполните поле");
        if (priceTF.value === "")
            error(priceTF, document.querySelector("#priceError"), "Заполните поле");
    }
};
function addProduct() {
    if (productPage.classList.contains("hidden")) {
        productPage.classList.remove("hidden")
    }
    if (!waybillsPage.classList.contains("hidden")) {
        waybillsPage.classList.add("hidden")
    }
};
backButton.onclick = function () {
    if (waybillsPage.classList.contains("hidden")) {
        waybillsPage.classList.remove("hidden")
    }
    if (!productPage.classList.contains("hidden")) {
        productPage.classList.add("hidden")
    }
};
addWaybillsButton.onclick = function () {
    let numberWaybills = document.querySelector("#numberWaybills");
    let person = document.querySelector("#person");
    let department = document.querySelector("#department");
    let date = document.querySelector("#dateWaybillsTF");

    if (numberWaybills.value !== "" && person.value !== "" && department.value !== "" && date.value !== "") {
        for (let i = 0; i < products.length; i++) {
            products[i].waybills={
                id: numberWaybills.value,
                person: person.value,
                department: department.value,
                date: date.value
            };
        }
        sendMessage(products).then();
    } else {
        if (numberWaybills.value === "")
            error(numberWaybills, document.querySelector("#numberWaybillsError"), "Заполните поле");
        if (person.value === "")
            error(person, document.querySelector("#personError"), "Заполните поле");
        if (department.value === "")
            error(department, document.querySelector("#departmentError"), "Заполните поле");
        if (date.value === "")
            error(date, document.querySelector("#dateWaybillsError"), "Заполните поле");
    }
};

async function sendMessage(data) {
    try {
        const headers = new Headers({
            'Content-Type': 'application/json'
        });
        let token = document.querySelector("#_csrf").attributes.getNamedItem('content').value;
        let header = document.querySelector("#_csrf_header").attributes.getNamedItem('content').value;
        headers.append(header,token);
        const response = await fetch('/user/addWaybills', {
            method: 'POST',
            body: JSON.stringify(data),
            headers: headers
        });
        const json = await response.json();
        if (response.status === 200) {
            document.querySelector("#numberWaybills").value="";
            document.querySelector("#person").value="";
            document.querySelector("#department").value="";
            document.querySelector("#dateWaybillsTF").value="";
            document.querySelector("#dest").innerHTML="";
            message(messageTF, json);
        } else if (response.status === 409) {
            error(null, messageTF, json);
        }
    } catch (error) {
    }
}

function getTypeOfProduct() {
    sendRequest(numberRecordTF.value, '/user/typeOfProduct').then();
}

numberRecordTF.onpaste = function (event) {
    sendRequest(numberRecordTF.value, '/user/typeOfProduct').then();
};

async function sendRequest(data, url) {
    try {

        const response = await fetch(url, {
            method: 'POST',
            body: JSON.stringify(data),
            headers: headers
        });
        const json = await response.json();
        if (response.status === 200) {
            nameTF.value = json.name;
            unitTF.value = json.unit;
        } else {

        }
    } catch (error) {
        console.log(error);
    }
}