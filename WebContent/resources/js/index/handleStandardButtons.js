define([ 'require', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays' ], function(require) {

	//handle settings button click
	$('#button-settings').click(function() {
		//redirect to settings page
		window.location = "Settings";
	});
	
	//handle static information button
	$('#button-show-static-information').click(function() {
		//get apks
		var apks = $.droidmate.ajax.get.getAPKSData();
		
		//sort apks alphabetically 
		apks.sort(function(a, b) {
			return a.name.toUpperCase().localeCompare(
					b.name.toUpperCase());
		});
		
		var apkNames = $.map(apks, function(val, i) {
			return val.name;
		});
		var apkSizes = $.map(apks, function(val, i) {
			return val.size / 1000 / 1000; // in mb
		});
		
		//Show dialog
		$.droidmate.dialogs.createFileSizeHistogramDialog(
				'File Sizes in MB', apkNames,
				apkSizes, 500, 400);
	});
});