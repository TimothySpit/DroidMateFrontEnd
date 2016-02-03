define([ 'require', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays' ], function(require) {

	// Get current table
	var tableCreator = require('../index/apkFileInfoTable');
	var table = tableCreator.initModul($('#table-apk-static-information'));

	//Set default overlay display time
	var INLINER_OVERLAY_DISPLAY_TIME = 2000;
	
	//update APKS in table
	function updateInlineAPKStatus() {
		//get apks
		var apks = $.droidmate.ajax.get.getAPKSData();
		
		//variable holding the resulting inlined status
		var inlinedStatus = table.inlinedStatus.INLINED;
		
		//clear table first
		table.clear();
		
		$.each(apks, function(index,value) {
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
			
			//add new status
			table.addAPKData(value.name, value.size, value.package,
					value.version, inlinedStatus, value.activityName);
		});
		
		
		table.redraw();
	}
	
	//watches inliner status
	function watchInlinerStatus() {
		//get current inline status
		var inlinerStatus = $.droidmate.inlining.getInliningStatus();
		
		switch (inlinerStatus) {
		case $.droidmate.inlining.status.FINISHED: {
			$.droidmate.overlays.alert(
					"<strong>Inliner finished successfully.</strong>",
					$.droidmate.overlays.alertTypes.SUCCESS,
					INLINER_OVERLAY_DISPLAY_TIME);
			updateInlineAPKStatus();
			break;
		}
		case $.droidmate.inlining.status.ERROR: {
			$.droidmate.overlays.alert(
					"<strong>There was an error while inlining.</strong>",
					$.droidmate.overlays.alertTypes.ERROR,
					INLINER_OVERLAY_DISPLAY_TIME);
			break;
		}
		case $.droidmate.inlining.status.INLINING: {
			setTimeout(function() {watchInlinerStatus()}, $.droidmate.inlining.WATCH_INLINER_INTERVAL);
			break;
		}
		}
	}
	
	// handle inline button click method
	$('#button-inline-files').click(function() {
		//notify user that the inliner has been started
		$.droidmate.overlays.alert(
				"<strong>Inliner has been started.</strong>",
				$.droidmate.overlays.alertTypes.INFO,
				INLINER_OVERLAY_DISPLAY_TIME);
		
		//send inline request and start watching the resulting status
		$.droidmate.inlining.startInlining();
		
		setTimeout(function() {watchInlinerStatus()}, $.droidmate.inlining.WATCH_INLINER_INTERVAL);
	});
});