define([ 'require', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays' ], function(require) {

	$('#button-start-exploration').click(function() {
		//get apk table and selected apks
		var tableCreator = require('../index/apkFileInfoTable');
		var table = tableCreator.initModul($('#table-apk-static-information'));

		var selectedRows = table.getSelectedRows();
		var selectedAPKS = $.map(selectedRows, function(val,i) {
			return val.getName();
		});
		
		//set selected apks 
		$.droidmate.ajax.setSelectedAPKS(selectedAPKS,true, function(data) {
			//check if server response could be parsed
			if(!data || !data["setSelectedAPKS[]"]) {
				$.droidmate.overlays.danger("Could not parse server returned value.", 
						$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			//check if all apks could be selected
			if(!data["setSelectedAPKS[]"].result) {
				//not all apks could be selected
				$.droidmate.overlays.danger(data["setSelectedAPKS[]"].message, 
						$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			//all apks have been successfully selected, go to explore page
			window.location = "Explore";
		});
	});
	
});