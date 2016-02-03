define([ 'require', '../index/apkFileInfoTable', '../index/handleUpdate' ], function(require) {

	var tableCreator = require('../index/apkFileInfoTable');
	var table = tableCreator.initModul($('#table-apk-static-information'));
	
	var updateHelper = require('../index/handleUpdate' );
	
	table.on("row:select", function(e) {
		updateHelper.updateUI();
	});

});