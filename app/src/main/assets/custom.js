$(document).ready(function () {
    $("#index > fieldset").attr("style", "");
    var timer = setTimeout(function () {
        loadMainColor();

        clearTimeout(timer);
        timer = null;
    }, 500);
});

function loadMainColor() {
    var images = new Array();

    $('.section.main-content img').each(function () {
        var image = $(this);
        var id = image.attr('id');
        if ((typeof id === "undefined") || id === null) {
            image.attr('id', uuid());
        }

        images.push({
            area: image.width() * image.height(),
            id: image.attr('id'),
            src: image.attr('src')
        });
        image = null;
        id = null;
    });

    var biggestImage = images.sort(sortByArea).filter(fitsByMin).pop();
    console.info("biggestImage: " + JSON.stringify(biggestImage));

    var imageFound = (typeof biggestImage !== "undefined") && biggestImage !== null;
    var imageHasSrc = imageFound && (typeof biggestImage.src !== "undefined") && biggestImage.src !== null;
    if (imageFound && imageHasSrc) {
        RutorClient.onFoundBiggestImage(biggestImage.src);
    }
    images = [];
    images = null;
    biggestImage = null;
};

function sortByArea(first, second) {
    return (first.area - second.area);
};

function fitsByMin(image) {
    console.info("biggestImage: ", image.area, ", fits: ", (image.area > 5000));
    return image.area > 5000;
};

function uuid() {
    return 'xxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
};

function hideshow(me) {
    var thisvar = me.next('div');
    if (thisvar.css('display') == 'none') {
        thisvar.html(thisvar.next('textarea').val());
        thisvar.css('display', 'block');
        me.removeClass("minus plus").addClass("minus");
    }
    else {
        thisvar.html('');
        thisvar.css('display', 'none');
        me.removeClass("minus plus").addClass("plus");
    }
};

function loadFiles(me) {
    try {
        if (!$('#filelist').hasClass("loaded")) {
            $('#filelist').load(RutorClient.originAddress() + '/descriptions/' + me.attr("id") + '.files');
            $('#filelist').addClass("loaded")
            $('#files').removeClass("hide");
        }
        toggleVisibility('files');
    } catch (e) {
        console.error(e);
        RutorClient.onWarning("Sorry, but i can't load files");
    }
};

function toggleVisibility(id) {
    var e = document.getElementById(id);
    if (e.style.display == 'block') {
        e.style.display = 'none';
    } else {
        e.style.display = 'block';
    }
};

function confirmation(delete_comment) {
    var answer = confirm("Удалить комментарий?")
    if (answer){
        window.location = "/comment.php?delete_comment=" + delete_comment;
    }
};

function cOptions(userid, cid) {
	var cstr = 'c_' + cid;
	var c_url = '<a href="/comment.php?edit_comment=' + cid + '"><img src="http://s.rutor.info/t/c_edit.png"></a><a onClick=confirmation(' + cid + ')><img  src="http://s.rutor.info/t/c_delete.png"></a>';
	if (userid == getCookie('userid') || getCookie('class') >= 5)
		document.getElementById(cstr).innerHTML = c_url;
};
