let backButton = document.querySelector('#backButton');

let numberReleasedTF = document.querySelector('#numberReleasedTF');
let remainingTF = document.querySelector('#remainingTF');

let remaining = Number(remainingTF.value);
let released = Number(numberReleasedTF.value);

backButton.onclick = async function () {
    window.history.back();
};

function checkRemaining() {
    let result = Number(numberReleasedTF.value) - (released - remaining)
    remainingTF.value = result;
    if(result<0){
        remainingTF.classList.add("invalid");
    }
}

numberReleasedTF.onpaste = checkRemaining;