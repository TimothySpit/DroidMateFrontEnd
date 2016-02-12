define([ 'require', 'jquery',
		'jquery.droidmate.overlays','jquery.droidmate.ajax',
		'../explore/handleUpdate'], function(require, jquery, DMOverlays, DMAjax,updateHelper ) {

	//handle open reports folder button
	jquery('#button-open-output-folder').click(function() {
		DMAjax.openOutputFolder(true,function(data) {
			if(!data | !data.openOutputFolder) {
				//server response error
				return;
			}
			
			if(!data.openOutputFolder.result) {
				//folder could not be opened
				DMOverlays.danger("Could not open explorer on host system.", 
						DMOverlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			//explorer is opening
			DMOverlays.success("Explorer is opening...", 
					DMOverlays.SUCCESS_MESSAGE_TIMEOUT);
			
		});
	});

	//handle show details for apks button
	jquery('#button-show-apk-details-dynamic').click(function() {
		window.location = "ExplorationCharts";
	});
	
});