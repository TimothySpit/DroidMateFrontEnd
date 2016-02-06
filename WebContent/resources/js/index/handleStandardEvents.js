define([ 'require', '../index/apkFileInfoTable', '../index/handleUpdate' ], function(require) {

	var tableCreator = require('../index/apkFileInfoTable');
	var table = tableCreator.initModul($('#table-apk-static-information'));
	
	var updateHelper = require('../index/handleUpdate' );
	
	table.on("row:select", function(e) {
		updateHelper.updateUI();
	});

	//redirect to Explore, when state is is not IDLE or INLINING
	$.droidmate.ajax.getUserStatus(true, function(data) {
		//check for error in data receiving
		if(!data || !data.getUserStatus || !data.getUserStatus.result) {
			$.droidmate.overlays.danger("Could not parse server returned value.", 
					$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
			return;
		}
		
		var statusData = data.getUserStatus.payload.data;
		
		if(!statusData) {
			return;
		}
		
		//If status is not idle or inlining, redirect
		if(statusData !== "INLINING" && statusData != "IDLE") {
			window.location = "Explore";
		}
	});
	
});