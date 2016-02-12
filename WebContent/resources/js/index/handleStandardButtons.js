define([ 'require', 'jquery',
		'jquery.droidmate.overlays', 'jquery.droidmate.ajax' ], 
		function(require, jquery, DMOverlays, DMAjax) {

	//handle settings button click
	jquery('#button-settings').click(function() {
		//redirect to settings page
		window.location = "Settings";
	});
	
	//handle static information button
	jquery('#button-show-static-information').click(function() {
		
		function updateCallback(data) {
			if(!data || !data.getAPKSData || !data.getAPKSData.result) {
				//path could not been set, show error message
				DMOverlays.danger("Could not retreive file size data from the server.", 
						DMOverlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			//sort apks alphabetically 
			data.getAPKSData.payload.data.sort(function(a, b) {
				return a.name.toUpperCase().localeCompare(
						b.name.toUpperCase());
			});
			
			var apkNames = jquery.map(data.getAPKSData.payload.data, function(val, i) {
				return val.name;
			});
			var apkSizes = jquery.map(data.getAPKSData.payload.data, function(val, i) {
				return val.sizeByte / 1000 / 1000; // in mb
			});
			
			//Show dialog
			jquery.droidmate.dialogs.createFileSizeHistogramDialog(
					'File Sizes in MB', apkNames,
					apkSizes, 500, 400);
		}
		
		//get apks
		DMAjax.getAPKSData(true,updateCallback);
	});
});