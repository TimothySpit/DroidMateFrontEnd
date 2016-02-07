define([ 'require',
		'jquery.droidmate.overlays','jquery.droidmate.explore','jquery.droidmate.ajax',
		'../explore/handleUpdate'], function(require) {

	var updateHelper = require('../explore/handleUpdate' );
	
	//handle open reports folder button
	$('#button-open-output-folder').click(function() {
		$.droidmate.ajax.openOutputFolder(true,function(data) {
			if(!data | !data.openOutputFolder) {
				//server response error
				return;
			}
			
			if(!data.openOutputFolder.result) {
				//folder could not be opened
				$.droidmate.overlays.danger("Could not open explorer on host system.", 
						$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			//explorer is opening
			$.droidmate.overlays.success("Explorer is opening...", 
					$.droidmate.overlays.SUCCESS_MESSAGE_TIMEOUT);
			
		});
	});

	//handle show details for apks button
	$('#button-show-apk-details-dynamic').click(function() {
		window.location = "ExplorationCharts";
	});
	
	//handle stop all button
	$('#button-stop-all').click(function() {
		//disable stop button
		$('#button-stop-all').prop("disabled",true);
		//send stop request
		$.droidmate.explore.stopExploration(true,function(data) {
			
		});
	});
	
	//handle return to start button
	$('#button-return-to-start').click(function() {
		//stop droidmate
		$.droidmate.explore.stopExploration(true,function(data) {
			
			//reset state
			$.droidmate.ajax.clearUser(true, function(result) {
				window.location = "Index";
			});
		});
	});
	
});