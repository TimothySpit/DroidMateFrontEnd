define([ 'require', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays','../index/handleUpdate' ], function(require) {

	// Get current table
	var tableCreator = require('../index/apkFileInfoTable');
	var table = tableCreator.initModul($('#table-apk-static-information'));
	
	var updateHelper = require('../index/handleUpdate' );
	
	//Set default overlay display time
	var INLINER_OVERLAY_DISPLAY_TIME = 2000;
	
	//update APKS in table
	function updateInlineAPKStatus() {
		updateHelper.updateUI();
		
		//get user status
		$.droidmate.ajax.getUserStatus(true, function(data) {
			var inlinedFinished = false;
			
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
			
			//If status is inlining, then update, else return
			if(statusData !== "INLINING") {
				inlinedFinished = true;
			}
			
			function updateAPKSCallback(data) {
				if(!data || !data.getAPKSData || !data.getAPKSData.result) {
					$.droidmate.overlays.danger("Could not parse server returned value.", 
							$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
					return;
				}
				
				//variable holding the resulting inlined status
				var inlinedStatus = table.inlinedStatus.INLINED;
				
				$.each(data.getAPKSData.payload.data, function(index,value) {
					switch (value.inlineStatus) {
					case $.droidmate.inlining.inliningStatus.NOT_INLINED: {
						inlinedStatus = table.inlinedStatus.NOT_INLINED;
						break;
					}
					case $.droidmate.inlining.inliningStatus.INLINING: {
						inlinedStatus = table.inlinedStatus.INLINING;
						break;
					}
					case $.droidmate.inlining.inliningStatus.INLINED: {
						inlinedStatus = table.inlinedStatus.INLINED;
						break;
					}
					case $.droidmate.inlining.inliningStatus.ERROR: {
						inlinedStatus = table.inlinedStatus.ERROR;
						break;
					}
					}
					
					//set new status
					var row = table.getRowByName(value.name);
					if(row) {
						row.updateInlinedStatus(inlinedStatus);
					}
				});
				
				//update again if repeatUpdate is true
				if(!inlinedFinished) {
					setTimeout(function() {updateInlineAPKStatus()}, $.droidmate.inlining.WATCH_INLINER_INTERVAL);
				}
			}
			
			//get apks
			$.droidmate.ajax.getAPKSData(true,updateAPKSCallback);
		});
	}
	
	// handle inline button click method
	$('#button-inline-files').click(function() {
		updateHelper.updateUI();
		
		//notify user that inliner has been started
		$.droidmate.overlays.info("Inliner started...",INLINER_OVERLAY_DISPLAY_TIME);
		
		//watch apk inline state change
		setTimeout(function() {updateInlineAPKStatus()}, $.droidmate.inlining.WATCH_INLINER_INTERVAL);
		
		//send inline request and start watching the resulting status
		$.droidmate.inlining.startInlining(true,function(data) {
			if(!data || !data.startInlining) {
				//no connection to the server, or wrong answer
				$.droidmate.overlays.danger("Could not parse server returned value.", 
						$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			var result = data.startInlining;
			//check result
			if(result && result.result) {
				//inliner finished successfully
				$.droidmate.overlays.info(result.message,INLINER_OVERLAY_DISPLAY_TIME);
			} else {
				$.droidmate.overlays.warning(result.message,INLINER_OVERLAY_DISPLAY_TIME);
			}
			
			updateHelper.updateUI();
		});
		
		updateInlineAPKStatus();
	});
});