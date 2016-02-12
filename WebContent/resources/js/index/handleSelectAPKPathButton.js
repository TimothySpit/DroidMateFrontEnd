define([ 'require', 'jquery', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays','jquery.droidmate.dialogs','jquery.droidmate.ajax','../index/handleUpdate' ], 
		function(require,jquery, tableCreator, DMInlining, DMOverlays, DMDialogs, DMAjax,updateHelper) {

	//get current apks table
	var table = tableCreator.initModul(jquery('#table-apk-static-information'));
	
	//updates apks table with new apk data
	function updateAPKSTable() {
		table.clear();
		
		function updateAPKSCallback(data) {
			//If there are no apks, there was an intern error, return
			if(!data || !data.getAPKSData || !data.getAPKSData.result) {
				return;
			}
			
			//Set inlined status
			var inlinedStatus = table.inlinedStatus.INLINED;
			jquery.each(data.getAPKSData.payload.data, function(index,value) {
				switch (value.inlineStatus) {
				case DMInlining.inliningStatus.NOT_INLINED: {
					inlinedStatus = table.inlinedStatus.NOT_INLINED;
					break;
				}
				case DMInlining.inliningStatus.INLINING: {
					inlinedStatus = table.inlinedStatus.INLINING;
					break;
				}
				case DMInlining.inliningStatus.INLINED: {
					inlinedStatus = table.inlinedStatus.INLINED;
					break;
				}
				case DMInlining.inliningStatus.ERROR: {
					inlinedStatus = table.inlinedStatus.ERROR;
					break;
				}
				}
				
				//add apk to table
				table.addAPKData(value.name, value.sizeReadable, value.packageName,
						value.packageVersionName + ' (#' + value.packageVersionCode + ')', inlinedStatus, value.activityName);
			});
			
			
			table.redraw();
			
			updateHelper.updateUI();
		}
		
		DMAjax.getAPKSData(true, updateAPKSCallback);
	}
	
	//handle select apk folder path button click
	jquery('#button-apk-folder-selection').click(function() {
		//create folderr selection dialog
		DMDialogs.createFileDialog('Select APK folder Path',function(path) {
			//at least one path has been selected, take the first one
			if (path) {
				//set input field text
				jquery('#input-apk-folder-selection').val(path);
				
				//set this path as the new selected apk root to get apks from
				DMAjax.setAPKsRoot(path,true,function(data) {
					if(data.setAPKsRoot.result) {
						//path has been successfully set, update table with new apks
						updateAPKSTable(table);
					} else {
						//path could not been set, show error message
						DMOverlays.danger(data.setAPKsRoot.message, 
								DMOverlays.ERROR_MESSAGE_TIMEOUT);
					}
				});
			}
		});
	});
	
	//handle startup
	DMAjax.getAPKsRoot(true, function(data) {
		if (data && data.getAPKsRoot && data.getAPKsRoot.result) {
			var path = data.getAPKsRoot.payload.data;
			jquery('#input-apk-folder-selection').val(path);
			updateAPKSTable();
		}
	});
});