define([ 'require', 'jquery', 
         'jquery.droidmate.dialogs','jquery.droidmate.ajax', 'jquery.droidmate.overlays' ],
         function(require, jquery, DMDialogs, DMAjax, DMOverlays) {

	//handle report output path button
	jquery('#button-reports-output-path').click(function() {
		var title = 'Please select a reports output path';
		var callback = function(path) {
			if(path) {
				//set input text to selected path
				jquery('#input-reports-path').val(path);
			}
		}
		DMDialogs.createFileDialog(title,callback);
	});
	
	//handle DroidMate path button
	jquery('#button-droidmate-path').click(function() {
		var title = 'Please select the path to DroidMate';
		var callback = function(path) {
			if(path) {
				//set input text to selected path
				jquery('#input-droidmate-path').val(path);
			}
		}
		DMDialogs.createFileDialog(title,callback);
	});
	
	//handle AAPT path button
	jquery('#button-aapt-path').click(function() {
		var title = 'Please select the path to the AAPT tool';
		var callback = function(path) {
			if(path) {
				//set input text to selected path
				jquery('#input-aapt-path').val(path);
			}
		}
		DMDialogs.createFileDialog(title,callback);
	});
	
	//save settings button
	jquery('#button-save-changes').click(function() {
		//get all currently set settings
		var reportsOutputPath 	= jquery('#input-reports-path').val();
		var droidmatePath		= jquery('#input-droidmate-path').val();
		var aaptPath 			= jquery('#input-aapt-path').val();
		var explorationTimeOut 	= jquery('#input-exploration-timeout').val();
		
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
		if(!explorationTimeOut || !jquery.isNumeric(explorationTimeOut) || explorationTimeOut <= 0) {
			resultMessage += 'Please specify an positive exploration Timeout.<br />';
		}
		
		//show error, if any
		if(resultMessage) {
			DMOverlays.warning(resultMessage,
					DMOverlays.DANGER_MESSAGE_TIMEOUT);
			return;
		}
		
		//no errors here, try to save settings
		var callback = function(data) {
			//check, if saving was successful, if not, post message
			if(!data || !data.setSettings || !data.setSettings.result) {
				//error in settings saving
				if(data.setSettings.message) {
				DMOverlays.danger(data.setSettings.message,
						DMOverlays.DANGER_MESSAGE_TIMEOUT);
				} else {
					DMOverlays.danger("Could not parse server returned value.", 
							DMOverlays.DANGER_MESSAGE_TIMEOUT);
				}
				return;
			}
			
			var saveResult = data.setSettings;
			DMOverlays.success(saveResult.message,
					DMOverlays.DANGER_MESSAGE_TIMEOUT);
		}
		DMAjax.saveDroidMateSettings(reportsOutputPath, droidmatePath, 
				aaptPath, explorationTimeOut, true,callback);
	});
})