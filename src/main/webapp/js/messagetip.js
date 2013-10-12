var intTimeStep = 100;
var isIe = (window.ActiveXObject) ? true : false;
var intAlphaStep = (isIe) ? 5 : 0.05;
var curSObj = null;
var curOpacity = null;
var stopTime = 0;
function startObjMessage(objId) {
    curSObj = document.getElementById(objId);
    if (isIe) {
        curSObj.style.cssText = 'DISPLAY: none; Z-INDEX: 1; FILTER: alpha(opacity=0); POSITION: absolute;';
    }
    setMessage();
}
function setMessage() {
    if (isIe) {
        curSObj.filters.alpha.opacity = 0;
    } else {
        curOpacity = 0;
        curSObj.style.opacity = 0
    }
    curSObj.style.display = '';
    setMessageShow();
}
function setMessageShow() {
    if (isIe) {
        curSObj.filters.alpha.opacity += intAlphaStep;
        if (curSObj.filters.alpha.opacity < 100) {
            setTimeout('setMessageShow()', intTimeStep);
        } else {
            stopTime += 10;
            if (stopTime < 500) {
                setTimeout('setMessageShow()', intTimeStep);
            } else {
                stopTime = 0;
                setMessageClose();
            }
        }
    } else {
        curOpacity += intAlphaStep;
        curSObj.style.opacity = curOpacity;
        if (curOpacity < 1) {
            setTimeout('setMessageShow()', intTimeStep);
        } else {
            stopTime += 10;
            if (stopTime < 200) {
                setTimeout('setMessageShow()', intTimeStep);
            } else {
                stopTime = 0;
                setMessageClose();
            }
        }
    }
}
function setMessageClose() {
    if (isIe) {
        curSObj.filters.alpha.opacity -= intAlphaStep;
        if (curSObj.filters.alpha.opacity > 0) {
            setTimeout('setMessageClose()', intTimeStep);
        } else {
            curSObj.style.display = 'none';
        }
    } else {
        curOpacity -= intAlphaStep;
        if (curOpacity > 0) {
            curSObj.style.opacity = curOpacity;
            setTimeout('setMessageClose()', intTimeStep);
        } else {
            curSObj.style.display = 'none';
        }
    }
}