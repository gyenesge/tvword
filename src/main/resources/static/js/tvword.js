function onCKeyPress(e) {
    // 67 - C and 99 - c key are handled
    if (e.which == 67 || e.which == 99) {
        window.open('/displays/config', '_self');
    }
}


//register key events to handle config button to go back to config page
document.onkeypress = onCKeyPress;

//set timeout to refresh the page regularly
setTimeout(function () {
    window.open('/displays/next', '_self');
}, 20 * 1000);
