define([ 'require', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays', '../index/handleUpdate' ], function(require) {

	//handle settings button click
	$('#button-settings').click(function() {
		//redirect to settings page
		window.location = "Settings";
	});
	
	//handle static information button
	$('#button-show-static-information').click(function() {
		
		function updateCallback(data) {
			if(!data || !data.getAPKSData || !data.getAPKSData.result) {
				//path could not been set, show error message
				$.droidmate.overlays.danger("Could not retreive file size data from the server.", 
						$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			//sort apks alphabetically 
			data.getAPKSData.payload.data.sort(function(a, b) {
				return a.name.toUpperCase().localeCompare(
						b.name.toUpperCase());
			});
			
			var apkNames = $.map(data.getAPKSData.payload.data, function(val, i) {
				return val.name;
			});
			var apkSizes = $.map(data.getAPKSData.payload.data, function(val, i) {
				return val.sizeByte / 1000 / 1000; // in mb
			});
			
			//Show dialog
			$.droidmate.dialogs.createFileSizeHistogramDialog(
					'File Sizes in MB', apkNames,
					apkSizes, 500, 400);
		}
		
		//get apks
		$.droidmate.ajax.getAPKSData(true,updateCallback);
	});
});