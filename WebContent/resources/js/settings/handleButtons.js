define([ 'require', 'bootbox', 
         'jstree', 'jquery.droidmate.dialogs','jquery.droidmate.ajax', 'jquery.droidmate.overlays' ],function(require, bootbox) {

	//handle report output path button
	$('#button-reports-output-path').click(function() {
		var title = 'Please select a reports output path';
		var callback = function(path) {
			if(path) {
				//set input text to selected path
				$('#input-reports-path').val(path);
			}
		}
		$.droidmate.dialogs.createFileDialog(title,callback);
	});
	
	//handle DroidMate path button
	$('#button-droidmate-path').click(function() {
		var title = 'Please select the path to DroidMate';
		var callback = function(path) {
			if(path) {
				//set input text to selected path
				$('#input-droidmate-path').val(path);
			}
		}
		$.droidmate.dialogs.createFileDialog(title,callback);
	});
	
	//handle AAPT path button
	$('#button-aapt-path').click(function() {
		var title = 'Please select the path to the AAPT tool';
		var callback = function(path) {
			if(path) {
				//set input text to selected path
				$('#input-aapt-path').val(path);
			}
		}
		$.droidmate.dialogs.createFileDialog(title,callback);
	});
	
	//save settings button
	$('#button-save-changes').click(function() {
		//get all currently set settings
		var reportsOutputPath 	= $('#input-reports-path').val();
		var droidmatePath		= $('#input-droidmate-path').val();
		var aaptPath 			= $('#input-aapt-path').val();
		var explorationTimeOut 	= $('#input-exploration-timeout').val();
		
		//check if input seems ok
		var resultMessage = "";
		if(!reportsOutputPath) {
			resultMessage += 'Please specify a reports output path.<br />';
		}
		if(!droidmatePath) {
			resultMessage += 'Please specify a DroidMate path.<br />';
		}
		if(!aaptPath) {
			resultMessage += 'Please specify an AAPT path.<br />';
		}
		if(!explorationTimeOut || !$.isNumeric(explorationTimeOut) || explorationTimeOut <= 0) {
			resultMessage += 'Please specify an positive exploration Timeout.<br />';
		}
		
		//show error, if any
		if(resultMessage) {
			$.droidmate.overlays.alert(resultMessage,
					$.droidmate.overlays.alertTypes.WARNING,
					$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
			return;
		}
		
		//no errors here, try to save settings
		var callback = function(data) {
			//check, if saving was successful, if not, post message
			if(!data || !data.setSettings || !data.setSettings.result) {
				//error in settings saving
				$.droidmate.overlays.alert("Could not parse server returned value.", $.droidmate.overlays.alertTypes.DANGER, 
						$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}
			
			var saveResult = data.setSettings;
			$.droidmate.overlays.alert(saveResult.message,
					$.droidmate.overlays.alertTypes.INFO,
					$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
		}
		$.droidmate.ajax.post.saveDroidMateSettings(reportsOutputPath, droidmatePath, 
				aaptPath, explorationTimeOut, true,callback);
	});
})



function saveSettings(outputPath, dmPath, aaptPath, explorationTime) {
		
	$.droidmate.ajax.post.saveDroidMateSettings(outputPath,dmPath, aaptPath, explorationTime, true, function(result) {
			var infobox = $('#saveinfo-box');
			if (result.success) {
				$.droidmate.overlays.alert(
						'<span><strong>Success!</strong> All data were saved.</span>',
						$.droidmate.overlays.alertTypes.SUCCESS, 2000);
			} else {
				$.droidmate.overlays.alert(
						"<span><strong>Error!</strong> "
								+ result.reason + "</span>",
								$.droidmate.overlays.alertTypes.DANGER, 2000);
			}
		});
	}