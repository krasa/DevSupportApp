function HasClass(obj, cName) {
    return (!obj || !obj.className) ? false : (new RegExp("\\b" + cName + "\\b")).test(obj.className)
}
function AddClass(obj, cName) {
    if (!obj) return;
    if (obj.className == null) obj.className = '';
    return obj.className += (obj.className.length > 0 ? ' ' : '') + cName;
}
function KillClass(obj, cName) {
    if (!obj) return;
    return obj.className = obj.className.replace(RegExp("^" + cName + "\\b\\s*|\\s*\\b" + cName + "\\b", 'g'), '');
}

function over(obj, name) {
    if (obj) {
        if (!HasClass(obj, name)) {
            AddClass(obj, name);
        }
    }
}

function out(obj, name) {
    if (obj) {
        if (HasClass(obj, name)) {
            KillClass(obj, name);
        }
    }
}

function popupwin(srch, width, height) {
    var w = screen.availWidth;
    var h = screen.availHeight;


    var leftPos = (w - width) / 2, topPos = (h - height) / 2;

    if (window && window.open && leftPos && topPos && w && h) {
        win = window.open(srch, 'imagesrc', 'height=' + height + ',width=' + width + ',status=no,toolbar=no,menubar=no,location=no,scrollbars=no,resizable=yes,top=' + topPos + ',left=' + leftPos);
        if (win) {
            win.focus();
            return false;
        }
    }
    return true;
}

function addToBookmark() {
    title = document.title;
    url = location.href;

    if (window.sidebar) { // Mozilla Firefox Bookmark
        window.sidebar.addPanel(title, url, "");
    } else if (window.external) { // IE Favorite
        window.external.AddFavorite(url, title);
    }
}