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
    var proc = spawn(
        'java', ['-jar', process.cwd() + '/bin/test.jar']
    );
    proc.stdout.on('data', function (data) {
        writeData(res, "stdout", data.toString());
    });

    proc.stderr.on("data", function (data) {
        writeData(res, "stderr", data.toString());
    });
});

module.exports = router;
