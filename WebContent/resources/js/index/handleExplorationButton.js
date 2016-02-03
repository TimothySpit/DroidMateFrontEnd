define([ 'require', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays' ], function(require) {

	$('#button-start-exploration').click(function() {
		//get apk table and selected apks
		var tableCreator = require('../index/apkFileInfoTable');
		var table = tableCreator.initModul($('#table-apk-static-information'));

		var selectedRows = table.getSelectedRows();
		var selectedAPKS = $.map(selectedAPKS, function(val,i) {
			return val.getName();
		});
		
		//set selected apks 
		$.droidmate.ajax.post.setSelectedAPKS(selectedAPKS,true, function() {
			//apks have been set, go to exploration page
			window.location = "Explore";
		});
	});
	
});