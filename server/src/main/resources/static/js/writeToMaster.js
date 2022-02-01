let count = document.querySelector("#count");
let messageTF = document.querySelector("#message");

let countError = document.querySelector("#countError");

let writeToMasterButton = document.querySelector("#writeToMasterButton");

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

let selectRow = {id: null, remaining: null}

$(function () {
    $('#dest').on('click', 'tr', function () {
        $('#dest tr').removeClass("select");
        $(this).addClass("select");
        selectRow.id = $(this).find('.id').html();
        selectRow.remaining = $(this).find('.remaining').html();
        console.log('sel: ', selectRow.id, selectRow.remaining);
        console.log('sel num : ', Number(selectRow.id), Number(selectRow.remaining));
    });
});
writeToMasterButton.onclick = function () {
    if (count.value !== "") {
        if (Number(count.value) <= Number(selectRow.remaining)) {
            sendMessage({id: Number(selectRow.id), count: Number(count.value)}, Number(selectRow.remaining) - Number(count.value))
                .then();
        } else {
            error(count, countError, "Такого количества нет на складе!");
        }
    } else {
        error(count, countError, "Введите необходимое количество!");
    }
};

async function sendMessage(data, count) {
    try {
        const headers = new Headers({
            'Content-Type': 'application/json'
        });
        let token = document.querySelector("#_csrf").attributes.getNamedItem('content').value;
        let header = document.querySelector("#_csrf_header").attributes.getNamedItem('content').value;
        headers.append(header,token);
        console.log(header, token);
        const response = await fetch('/user/writeProductToMaster', {
            method: 'PATCH',
            body: JSON.stringify(data),
            headers: headers
        });
        const json = await response.json();
        if (response.status === 200) {
            message(document.querySelector("#message"), json);
            $('#dest .select .remaining').text(count);
            selectRow.remaining=count;
            if(count===0){
                $('#dest .select').html('');
            }
            $('#dest .select .sum').text((Number($('#dest .select .price').text())*Number(selectRow.remaining)).toFixed(2));
        } else if (response.status === 409) {
            messageTF.innerHTML = json;
            messageTF.classList.remove("error");
            setTimeout(function () {
                messageTF.innerHTML = "";
                messageTF.classList.add("error");
            }, 5000);
        }
    } catch (error) {
    }
}