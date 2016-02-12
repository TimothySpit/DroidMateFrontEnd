define([ 'require', 'jquery', 'Spinner',
		'jquery.droidmate.ajax',
		'jquery.droidmate.overlays' ], function(require,jquery, Spinner, DMAjax, DMOverlays) {

	//create loading indicator for STARTING state
	var spinner = new Spinner().spin()
	var spinnerContainerParent = jquery('#div-droidmate-starting-indicator-container');
	var spinnerContainer = jquery('#div-starting-indicator');
	var spinnerText = jquery('#div-starting-indicator-text');
	spinnerText.text("DroidMate is starting...");
	//show console
	jquery('#div-console-output').show();
	
	function showControls() {
		jquery('#div-exploration-top-navigation').show();
		jquery('#table-apk-exploration-info').show();
		jquery('#div-exploration-bottom-navi').show();
		jquery('#div-console-output').show();
		jquery('#div-apk-exploration-table-container').show();
	}
	
	function hideControls() {
		jquery('#div-exploration-top-navigation').hide();
		jquery('#table-apk-exploration-info').hide();
		jquery('#div-exploration-bottom-navi').hide();
		jquery('#div-console-output').hide();
		jquery('#div-apk-exploration-table-container').hide();
	}
	
	function showReturnIndicator() {
		hideControls();
		spinnerContainerParent.show();
		spinnerText.text("Stopping...");
		spinner = new Spinner().spin()
		spinnerContainer.append(spinner.el);
	}
	
	function updateUIControls(callback) {
		
		DMAjax.getUserStatus(true, function(data) {
			// check for error in data receiving
			if (!data || !data.getUserStatus || !data.getUserStatus.result) {
				DMOverlays.danger(
						"Could not parse server returned value.",
						DMOverlays.DANGER_MESSAGE_TIMEOUT);
				
				if(callback) {
					callback(data);
				}
				return;
			}

			var statusData = data.getUserStatus.payload.data;

			if (!statusData) {
				
				if(callback) {
					callback(data);
				}
				return;
			}
			
			//DroidMate is still starting
			if(statusData == "STARTING") {
				spinnerContainerParent.show();
				spinnerContainer.append(spinner.el);
				
				if(callback) {
					callback(data);
				}
				return;
			} else {
				spinnerContainerParent.hide();
				spinner.el.remove();
			}
			
			//enable ui
			if (statusData === "EXPLORING" || statusData === "FINISHED" || statusData === "ERROR") {
				showControls();
			}
			//disable stop button
			if(statusData === "FINISHED" || statusData === "ERROR") {
				jquery('#button-stop-all').prop("disabled", true);
			}
			
			if(callback) {
				callback(data);
			}
		});
	}
	
	return {
		updateUI : updateUIControls,
		showReturnIndicator : showReturnIndicator
	};
});