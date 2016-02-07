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
});