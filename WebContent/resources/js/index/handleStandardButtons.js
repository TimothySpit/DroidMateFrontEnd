define([ 'require', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays', '../index/handleUpdate' ], function(require) {

	//handle settings button click
	$('#button-settings').click(function() {
		//redirect to settings page
		window.location = "Settings";
	});
	
	//handle static information button
	$('#button-show-static-information').click(function() {
		//get apks
		var apks = $.droidmate.ajax.get.getAPKSData();
		
		if(!apks || !apks.getAPKSData || !apks.getAPKSData.result) {
			//path could not been set, show error message
			$.droidmate.overlays.alert("Could not retreive file size data from the server.", $.droidmate.overlays.alertTypes.DANGER, 
					$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
			return;
		}
		
		//sort apks alphabetically 
		apks.getAPKSData.payload.data.sort(function(a, b) {
			return a.name.toUpperCase().localeCompare(
					b.name.toUpperCase());
		});
		
		var apkNames = $.map(apks.getAPKSData.payload.data, function(val, i) {
			return val.name;
		});
		var apkSizes = $.map(apks.getAPKSData.payload.data, function(val, i) {
			return val.sizeByte / 1000 / 1000; // in mb
		});
		
		//Show dialog
		$.droidmate.dialogs.createFileSizeHistogramDialog(
				'File Sizes in MB', apkNames,
				apkSizes, 500, 400);
	});
});