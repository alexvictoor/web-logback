(function() {
    if (typeof (EventSource) !== "undefined") {
        var scripts = document.getElementsByTagName('script');
        var index = scripts.length - 1;
        var myScript = scripts[index];
        var scriptUrl = myScript.src;
        var streamUrl = scriptUrl.substring(0, scriptUrl.length - 10) + "stream"; // 10 being the length of string "logback.js"
        var source = new EventSource(streamUrl);
        source.onmessage = function(event) {
           console.log(event.data);
        };
    }
})();