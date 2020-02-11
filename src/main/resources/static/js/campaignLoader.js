var display = null;
var refreshPeriod = 12 * 1000; // millisec
let imageCache = {
    '-1': null
}


$(document).ready(function () {
    //init javascript context

    display = $("#display").data();

    let rT = display["refreshtime"];
    if (typeof rT !== 'undefined' && rT !== 'null') {
        refreshPeriod = rT * 1000; // millisec
    } else {
        console.info("Invalid refresh time for display " + display["displayid"] + ": " + rT);
    }


    //register key events to handle config button to go back to config page
    document.onkeypress = onCKeyPress;

    //init campaign cycle
    getNextCampaign();
});

function onCKeyPress(e) {
    // 67 - C and 99 - c key are handled as shortcut to the config page.
    if (e.which == 67 || e.which == 99) {
        window.open('/displays/config', '_self');
    } else if (e.which == 68 || e.which == 100) {
        //d or D opens the "detect panel"
        if ($("#display").css("display") === "none") {
            $("#windowSize").text($(window).width() + " x " + $(window).height());
            $("#display").css("display", "block");
        } else {
            $("#display").css("display", "none");
        }
    }
    console.info(e.which);
}

function getNextCampaign() {
    $.getJSON("next.json", function (data) {
        //set title in all cases
        if (typeof data["name"] !== 'undefined') {
            document.title = data["name"];
        }

        if (typeof data["type"] !== 'undefined') {
            if (data["type"] == 'T') {
                setTextCampaign(data);
            } else if (data["type"] == 'P') {
                setPictureCampaign(data);
            }
        }
    }).fail(function () {
        //try again after timeout
        console.error("Failed to load next campaign, try again in " + refreshPeriod + " millisecs.");
        window.setTimeout(getNextCampaign, refreshPeriod);
    }).done(function () {
        //repeat again
        window.setTimeout(getNextCampaign, refreshPeriod);
    });
}

function setTextCampaign(data) {
    //hide other containers
    $('#pic-container').css('display', 'none');

    //init container
    $('#main-text').css('color', data["textColor"]);
    $('#main-text').text(data["text"]);
    $(document.body).css('background', data["bkgColor"]);
    $(document.body).css('margin-left', '10%');
    $(document.body).css('margin-right', '10%');
    $(document.body).css('margin-bottom', '0');
    $(document.body).css('margin-top', '50px');

    $('#flex-text-panel').css('height', $(window).height() - 200);

    //display container
    $('#text-container').css('display', 'block');

    //postprocessing
    $('#flex-text-panel').textfill({
        maxFontPixels: 200,
        allowOverflow: true
    });
}

function setPictureCampaign(data) {
    //preload image
    let image = imageCache[data["id"]];
    if (typeof image === 'undefined') {
        //image is not yet cached
        image = new Image();
        image.id = "main-pic-" + data["id"];
        image.src = "/campaigns/" + data["id"] + "/image";
        image.onload = function () {
            loadImage(this);
            imageCache[data["id"]] = image;
        };
        image.onerror = function () {
            console.error("Failed to load image: " + image.src);
        }
    } else {
        //console.info("Load image from cache: " + data["id"]);
        loadImage(image);
    }

}

function loadImage(image) {
    //hide other container
    $('#text-container').css('display', 'none');
    $(document.body).css('background', 'black');
    $(document.body).css('margin', '0px');

    //init container
    let wh = $(window).height();
    let ww = $(window).width();
    let ih = image.height;
    let iw = image.width;
    let newh = ih;
    let neww = iw;
    let topp = 0;
    let leftp = 0;

    if (wh / ww < ih / iw) {
        // the frame is lower
        newh = wh;
        neww = newh / ih * iw;
        leftp = (ww - neww) / 2;
    } else {
        // frame is higher
        neww = ww;
        newh = neww / iw * ih;
        topp = (wh - newh) / 2;
    }

    image.style.position = 'fixed';
    image.style.top = topp + "px";
    image.style.left = leftp + "px";
    image.height = newh;
    image.width = neww;

    $('#pic-container').empty().append(image);

    //display container
    $('#pic-container').css('display', 'block');

    //postprocessing
}

function clearAllTimeouts() {
    const highestTimeoutId = setTimeout(";");
    for (let i = 0; i < highestTimeoutId; i++) {
        clearTimeout(i);
    }
}



