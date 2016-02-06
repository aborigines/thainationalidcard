#!/usr/bin/env node
// source http://ludovicrousseau.blogspot.com/2014/09/pcsc-sample-in-javascript-nodejs.html

// require
var pcsc = require('pcsclite');

var pcsc = pcsc();

pcsc.on('reader', function(reader) {

    function exit() {
        reader.close();
        pcsc.close();
    }

    cmd_select = new Buffer([0x00, 0xA4, 0x04, 0x00, 0x08, 0xA0, 0x00, 0x00, 0x00, 0x54, 0x48, 0x00, 0x01]);
    cmd_select2 = new Buffer([0x00, 0xC0, 0x00, 0x00, 0x0A]);
    cmd_idcard1 = new Buffer([0x80, 0xb0, 0x00, 0x04, 0x02, 0x00, 0x0d]);
    cmd_idcard2 = new Buffer([0x80, 0xc0, 0x00, 0x00, 0x0d]);

    console.log('Using:', reader.name);

    reader.connect(function(err, protocol) {
        if (err) {
            console.log(err);
            return exit();
        }
        reader.transmit(cmd_select, 255, protocol, function(err, data) {
            if (err) {
                console.log(err);
                return exit();
            }
            reader.transmit(cmd_select2, 255, protocol, function(err, data) {
	            if (err) {
	                console.log(err);
	                return exit();
	            }
            });
            reader.transmit(cmd_idcard1, 255, protocol, function(err, data) {
	            if (err) {
	                console.log(err);
	                return exit();
	            }
	            reader.transmit(cmd_idcard2, 255, protocol, function(err, data) {
		            if (err) {
		                console.log(err);
		                return exit();
		            } else {
		            	console.log(data);
		            	console.log(String(data));
		            }
		            return exit();
	            });
            });
        });
    });
});

pcsc.on('error', function(err) {
    console.log('PCSC error', err.message);
});