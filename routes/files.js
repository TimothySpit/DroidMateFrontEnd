var express = require('express');


var http = require('http');
var _ = require('lodash');
var express = require('express');
var fs = require('fs');
var path = require('path');
var util = require('util');

var dir =  process.cwd();

var program = require('commander');

var router = express.Router();

router.get('/', function(req, res, next) {
    var currentDir =  dir;
    var query = req.query.path || '';
    console.log(query);
    if (query) currentDir = path.join(dir, query).replace(/\\/g,'/');
    console.log("browsing ", currentDir);
    fs.readdir(currentDir, function (err, files) {
        console.log(files);
        if (err) {
            throw err;
        }
        var data = [];
        files
            .forEach(function (file) {
                try {
                    //console.log("processingile);
                    var isDirectory = fs.statSync(path.join(currentDir ,file)).isDirectory();
                    if (isDirectory) {
                        data.push({ Name : file, IsDirectory: true, Path : path.join(query, file)  });
                    } else {
                        /*var ext = path.extname(file);
                        if(program.exclude && _.contains(program.exclude, ext)) {
                            console.log("excluding file ", file);
                            return;
                        }
                        data.push({ Name : file, Ext : ext, IsDirectory: false, Path : path.join(query, file) });*/
                    }

                } catch(e) {
                    console.log(e);
                }

            });
        data = _.sortBy(data, function(f) { return f.Name });
        res.json(data);
    });
});

module.exports = router;