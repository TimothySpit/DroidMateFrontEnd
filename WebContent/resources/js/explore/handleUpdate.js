define([ 'require', 'Spinner',
		'jquery.flot', 'jquery.flot.tooltip', 'jquery.droidmate.ajax',
		'jquery.droidmate.inlining', 'DataTables', 'jquery.droidmate.dialogs',
		'jquery.droidmate.overlays' ], function(require,Spinner) {

	//create loading indicator for STARTING state
	var spinner = new Spinner().spin()
	var spinnerContainerParent = $('#div-droidmate-starting-indicator-container');
	var spinnerContainer = $('#div-starting-indicator');
	var spinnerText = $('#div-starting-indicator-text');
	spinnerText.val = "DroidMate is starting..."
	
	function showControls() {
		$('#div-exploration-top-navigation').show();
		$('#table-apk-exploration-info').show();
		$('#div-exploration-bottom-navi').show();
		$('#div-console-output').show();
		$('#div-apk-exploration-table-container').show();
	}
	
	function hideControls() {
		$('#div-exploration-top-navigation').hide();
		$('#table-apk-exploration-info').hide();
		$('#div-exploration-bottom-navi').hide();
		$('#div-console-output').hide();
		$('#div-apk-exploration-table-container').hide();
	}
	
	function showReturnIndicator() {
		hideControls();
		spinnerContainerParent.show();
		spinnerText.text("Stopping...");
		spinner = new Spinner().spin()
		spinnerContainer.append(spinner.el);
	}
	
	function updateUIControls(callback) {
		
		$.droidmate.ajax.getUserStatus(true, function(data) {
			// check for error in data receiving
			if (!data || !data.getUserStatus || !data.getUserStatus.result) {
				$.droidmate.overlays.danger(
						"Could not parse server returned value.",
						$.droidmate.overlays.DANGER_MESSAGE_TIMEOUT);
				
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
				$('#button-stop-all').prop("disabled", true);
			} else {
				$('#button-stop-all').prop("disabled", false);
			}
			
			if(callback) {
				callback(data);
			}
		});
	}
	
	function stopExplorationStart() {
		
	}

	function stopExplorationEnd() {
		
	}
	
	return {
		updateUI : updateUIControls,
		showReturnIndicator : showReturnIndicator
	};
});