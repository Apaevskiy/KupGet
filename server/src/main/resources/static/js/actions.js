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
    if (input != null) {
        input.classList.add("invalid");
    }
    if (label != null) {
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
