define([ 'require', 'jquery.droidmate.ajax'
		, 'jquery.droidmate.dialogs',
		'jquery.droidmate.overlays' ], function(require,Spinner) {

	function showControls() {
		$('#div-charts-elements-seen-screens-explored').show();
		$('#div-charts-apk-status-elements-explored').show();
		$('#div-chart-multiselect').show();
		$('#div-explorationcharts-bottom-navi').show();
	}
	
	function hideControls() {
		$('#div-charts-elements-seen-screens-explored').hide();
		$('#div-charts-apk-status-elements-explored').hide();
		$('#div-chart-multiselect').hide();
		$('#div-explorationcharts-bottom-navi').hide();
	}
	
	function updateUIControls(async, callback) {
		
		$.droidmate.ajax.getUserStatus(async, function(data) {
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
				if(callback) {
					callback(data);
				}
				return;
			} 
			
			//enable ui
			if (statusData === "EXPLORING" || statusData === "FINISHED" || statusData === "ERROR") {
				showControls();
			}
			
			if(callback) {
				callback(data);
			}
		});
	}

	return {
		updateUI : updateUIControls
	};
});