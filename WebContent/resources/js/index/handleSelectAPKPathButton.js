define([ 'require', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays','jquery.droidmate.dialogs','jquery.droidmate.ajax','../index/handleUpdate' ], function(require) {

	//get current apks table
	var tableCreator = require('../index/apkFileInfoTable');
	var table = tableCreator.initModul($('#table-apk-static-information'));
	
	var updateHelper = require('../index/handleUpdate' );
	
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
			$.each(data.getAPKSData.payload.data, function(index,value) {
				switch (value.inlineStatus) {
				case $.droidmate.inlining.inliningStatus.NOT_INLINED: {
					inlinedStatus = table.inlinedStatus.NOT_INLINED;
					break;
				}
				case $.droidmate.inlining.inliningStatus.INLINING: {
					inlinedStatus = table.inlinedStatus.INLINING;
					break;
				}
				case $.droidmate.inlining.inliningStatus.INLINED: {
					inlinedStatus = table.inlinedStatus.INLINED;
					break;
				}
				case $.droidmate.inlining.inliningStatus.ERROR: {
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
		
		$.droidmate.ajax.getAPKSData(true, updateAPKSCallback);
	}
	
	//handle select apk folder path button click
	$('#button-apk-folder-selection').click(function() {
		//create folderr selection dialog
		$.droidmate.dialogs.createFileDialog('Select APK folder Path',function(path) {
			//at least one path has been selected, take the first one
			if (path) {
				//set input field text
				$('#input-apk-folder-selection').val(path);
				
				//set this path as the new selected apk root to get apks from
				$.droidmate.ajax.setAPKsRoot(path,true,function(data) {
					if(data.setAPKsRoot.result) {
						//path has been successfully set, update table with new apks
						updateAPKSTable(table);
					} else {
						//path could not been set, show error message
						$.droidmate.overlays.danger(data.setAPKsRoot.message, 
								$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
					}
				});
			}
		});
	});
	
	//handle startup
	var path = $.droidmate.ajax.getAPKsRoot(true, function(data) {
		if (data && data.getAPKsRoot && data.getAPKsRoot.result) {
			var path = data.getAPKsRoot.payload.data;
			$('#input-apk-folder-selection').val(path);
			updateAPKSTable();
		}
	});
});