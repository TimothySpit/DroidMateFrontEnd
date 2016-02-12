define([ 'require', 'jquery', '../index/apkFileInfoTable', '../index/handleUpdate', 'jquery.droidmate.overlays' ], 
		function(require, jquery, tableCreator, updateHelper, DMOverlays) {

	var table = tableCreator.initModul(jquery('#table-apk-static-information'));
	
	table.on("row:select", function(e) {
		updateHelper.updateUI();
	});

	//redirect to Explore, when state is is not IDLE or INLINING
	jquery.droidmate.ajax.getUserStatus(true, function(data) {
		//check for error in data receiving
		if(!data || !data.getUserStatus || !data.getUserStatus.result) {
			DMOverlays.danger("Could not parse server returned value.", 
					DMOverlays.ERROR_MESSAGE_TIMEOUT);
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