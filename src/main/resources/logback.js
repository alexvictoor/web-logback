(function() {
    if (typeof (EventSource) !== "undefined") {
        var scripts = document.getElementsByTagName('script');
        var index = scripts.length - 1;
        var myScript = scripts[index];
        var scriptUrl = myScript.src;
        var streamUrl = scriptUrl.substring(0, scriptUrl.length - 10) + "stream"; // 10 being the length of string "logback.js"
        var source = new EventSource(streamUrl);

        var defaultStyle = myScript.getAttribute("style");

        if (defaultStyle) {
            source.onmessage = function(event) {
                console.log("%c " + event.data, defaultStyle);
            };
        } else {
            source.onmessage = function (event) {
                console.log(event.data);
            };
        }

        var debugStyle = myScript.getAttribute("style-debug") || defaultStyle;
        if (debugStyle) {
            source.addEventListener("DEBUG", function(event) {
                console.debug("%c " + event.data, debugStyle);
            });
        } else {
            source.addEventListener("DEBUG", function (event) {
                console.debug(event.data);
            });
        }

        var infoStyle = myScript.getAttribute("style-info") || defaultStyle;
        if (infoStyle) {
            source.addEventListener("INFO", function(event) {
                console.info("%c " + event.data, infoStyle);
            });
        } else {
            source.addEventListener("INFO", function (event) {
                console.info(event.data);
            });
        }

        var warnStyle = myScript.getAttribute("style-warn") || defaultStyle;
        if (warnStyle) {
            source.addEventListener("WARN", function(event) {
                console.warn("%c " + event.data, warnStyle);
            });
        } else {
            source.addEventListener("WARN", function (event) {
                console.warn(event.data);
            });
        }

        var errorStyle = myScript.getAttribute("style-error") || defaultStyle;
        if (errorStyle) {
            source.addEventListener("ERROR", function(event) {
                console.error("%c " + event.data, errorStyle);
            });
        } else {
            source.addEventListener("ERROR", function (event) {
                console.error(event.data);
            });
        }

    } else {
        alert("Your browser does not support SSE, hence web-logback will not work properly");
    }
})();