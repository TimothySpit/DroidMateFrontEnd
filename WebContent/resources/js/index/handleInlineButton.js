define([ 'require', 'jquery', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays', 'jquery.droidmate.ajax','../index/handleUpdate' ], 
		function(require, jquery, tableCreator, DMInlining, DMOverlays, DMAjax, updateHelper) {

	// Get current table
	var table = tableCreator.initModul(jquery('#table-apk-static-information'));
	
	//Set default overlay display time
	var INLINER_OVERLAY_DISPLAY_TIME = 2000;
	
	//update APKS in table
	function updateInlineAPKStatus() {
		
		//get user status
		updateHelper.updateUI(function(data) {
			
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
			
			function updateAPKSCallback(data) {
				if(!data || !data.getAPKSData || !data.getAPKSData.result) {
					DMOverlays.danger("Could not parse server returned value.", 
							DMOverlays.ERROR_MESSAGE_TIMEOUT);
					return;
				}
				
				//variable holding the resulting inlined status
				var inlinedStatus = table.inlinedStatus.INLINED;
				
				jquery.each(data.getAPKSData.payload.data, function(index,value) {
					switch (value.inlineStatus) {
					case DMInlining.inliningStatus.NOT_INLINED: {
						inlinedStatus = table.inlinedStatus.NOT_INLINED;
						break;
					}
					case DMInlining.inliningStatus.INLINING: {
						inlinedStatus = table.inlinedStatus.INLINING;
						break;
					}
					case DMInlining.inliningStatus.INLINED: {
						inlinedStatus = table.inlinedStatus.INLINED;
						break;
					}
					case DMInlining.inliningStatus.ERROR: {
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
				setTimeout(function() {updateInlineAPKStatus()}, DMInlining.WATCH_INLINER_INTERVAL);
			}
			
			//get apks
			DMAjax.getAPKSData(true,updateAPKSCallback);
		});
	}
	
	// handle inline button click method
	jquery('#button-inline-files').click(function() {
		updateHelper.updateUI();
		
		//notify user that inliner has been started
		DMOverlays.info("Inliner started...",INLINER_OVERLAY_DISPLAY_TIME);
		
		//watch apk inline state change
		setTimeout(function() {updateInlineAPKStatus()}, DMInlining.WATCH_INLINER_INTERVAL);
		
		//send inline request and start watching the resulting status
		DMInlining.startInlining(true,function(data) {
			if(!data || !data.startInlining) {
				//no connection to the server, or wrong answer
				DMOverlays.danger("Could not parse server returned value.", 
						DMOverlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			var result = data.startInlining;
			//check result
			if(result && result.result) {
				//inliner finished successfully
				DMOverlays.info(result.message,INLINER_OVERLAY_DISPLAY_TIME);
			} else {
				DMOverlays.warning(result.message,INLINER_OVERLAY_DISPLAY_TIME);
			}
			
			updateHelper.updateUI();
		});
	});
	
	//start update on page load
	updateInlineAPKStatus();
});