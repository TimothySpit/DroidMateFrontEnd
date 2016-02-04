define([ 'require', '../index/apkFileInfoTable', 'jquery.droidmate.inlining',
		'jquery.droidmate.overlays','jquery.droidmate.dialogs','jquery.droidmate.ajax','../index/handleUpdate' ], function(require) {

	//get current apks table
	var tableCreator = require('../index/apkFileInfoTable');
	var table = tableCreator.initModul($('#table-apk-static-information'));
	
	var updateHelper = require('../index/handleUpdate' );
	
	//updates apks table with new apk data
	function updateAPKSTable() {
		table.clear();
		
		var apksData = $.droidmate.ajax.get.getAPKSData();
		
		//If there are no apks, there was an intern error, return
		if(!apksData || !apksData.getAPKSData || !apksData.getAPKSData.result) {
			return;
		}
		
		var apksLoadingResultDiv = $('#div-apk-folder-selection-result');
		var numAPKS = apksData.getAPKSData.payload.data.length;
		
		if (numAPKS > 0) {
			//more than one apk is in the selected root folder, notice user
			apksLoadingResultDiv.html(
					'<span class="label label-success text-center">'
							+ numAPKS + ' apks loaded.</span>');
		} else {
			//hide controls and set indikator
			apksLoadingResultDiv.html(
					'<span class="label label-danger text-center">no apks loaded.</span>');
		}
		
		//Set inlined status
		var inlinedStatus = table.inlinedStatus.INLINED;
		$.each(apksData.getAPKSData.payload.data, function(index,value) {
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
	
	//handle select apk folder path button click
	$('#button-apk-folder-selection').click(function() {
		//create folderr selection dialog
		$.droidmate.dialogs.createFileDialog('Select APK folder Path',function(path) {
			//at least one path has been selected, take the first one
			if (path) {
				//set input field text
				$('#input-apk-folder-selection').val(path);
				
				//set this path as the new selected apk root to get apks from
				$.droidmate.ajax.post.setAPKsRoot(path,true,function(data) {
					if(data.setAPKsRoot.result) {
						//path has been successfully set, update table with new apks
						updateAPKSTable(table);
					} else {
						//path could not been set, show error message
						$.droidmate.overlays.alert(data.setAPKsRoot.message, $.droidmate.overlays.alertTypes.DANGER, 
								$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
					}
				});
			}
		});
	});
	
	var path = $.droidmate.ajax.get.getAPKsRoot(true, function(data) {
		if (data && data.getAPKsRoot && data.getAPKsRoot.result) {
			var path = data.getAPKsRoot.payload.data;
			$('#input-apk-folder-selection').val(path);
			updateAPKSTable();
		}
	});
});