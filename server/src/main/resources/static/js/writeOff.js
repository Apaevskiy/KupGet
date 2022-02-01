let count = document.querySelector("#count");
let comment = document.querySelector("#comment");

let countError = document.querySelector("#countError");
let commentError = document.querySelector("#commentError");

let messageTF = document.querySelector("#message");

let writeOfButton = document.querySelector("#writeOfButton");

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
        // alert($(this).find('td').html());
        $('#dest tr').removeClass("select");
        $(this).addClass("select");
        selectRow.id = $(this).find('.id').html();
        selectRow.remaining = $(this).find('.remaining').html();
    });
});
writeOfButton.onclick = function () {
    if (count.value !== "" && comment.value !== "") {
        if (Number(count.value) <= Number(selectRow.remaining)) {
            sendMessage({
                id: Number(selectRow.id),
                count: Number(count.value),
                comment: comment.value
            }, Number(selectRow.remaining) - Number(count.value))
                .then();
        } else {
            error(count, countError, "Такого количества нет!");
        }
    } else {
        if (comment.value === "") error(comment, commentError, "Заполните поле!");
        if (count.value === "") error(count, countError, "Заполните поле!");
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
        const response = await fetch('/user/writeOff', {
            method: 'PATCH',
            body: JSON.stringify(data),
            headers: headers
        });
        const json = await response.json();
        if (response.status === 200) {
            message(messageTF, json);
            $('#dest .select .remaining').text(count);
            selectRow.remaining = count;
            if (count === 0) {
                $('#dest .select').html('');
            }
            $('#dest .select .sum').text((Number($('#dest .select .price').text()) * Number(selectRow.remaining)).toFixed(2));
        } else if (response.status === 409) {
            error(null, messageTF, json);
        }
    } catch (error) {
    }
}