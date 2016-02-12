define([ 'require', 'jquery', '../index/apkFileInfoTable', 'jquery.droidmate.ajax',
		'jquery.droidmate.overlays' ], function(require,jquery,tableCreator, DMAjax, DMOverlays) {

	jquery('#button-start-exploration').click(function() {
		//get apk table and selected apks
		var table = tableCreator.initModul(jquery('#table-apk-static-information'));

		var selectedRows = table.getSelectedRows();
		var selectedAPKS = jquery.map(selectedRows, function(val,i) {
			return val.getName();
		});
		
		//set selected apks 
		DMAjax.setSelectedAPKS(selectedAPKS,true, function(data) {
			//check if server response could be parsed
			if(!data || !data["setSelectedAPKS[]"]) {
				DMOverlays.danger("Could not parse server returned value.", 
						DMOverlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			//check if all apks could be selected
			if(!data["setSelectedAPKS[]"].result) {
				//not all apks could be selected
				DMOverlays.danger(data["setSelectedAPKS[]"].message, 
						DMOverlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			//all apks have been successfully selected, go to explore page
			window.location = "Explore";
		});
	});
	
});