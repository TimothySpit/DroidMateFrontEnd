var express = require('express');
var router = express.Router();

function writeData(res, event, data) {
    res.write("event: " + event + "\n");
    var str = data.split("\n");
    for(var i = 0; i < str.length; i++) {
        res.write('data: ' + str[i] + "\n\n");
    }
}

/* GET home page. */
router.get('/', function (req, res, next) {
    res.writeHead(200, {"Content-Type": "text/event-stream", "Cache-Control": "no-cache", "Connection": "keep-alive"});
    res.write("retry: 10000\n");

    var spawn = require('child_process').spawn;
    var process = spawn(
        'java', ['-jar', 'C:/Informatik/Web/WebStorm-Projekte/web-front-end-for-android-gui-test-generator/bin/test.jar']
    );
    process.stdout.on('data', function (data) {
        writeData(res, "stdout", data.toString());
    });

    process.stderr.on("data", function (data) {
        writeData(res, "stderr", data.toString());
    });
});

module.exports = router;
