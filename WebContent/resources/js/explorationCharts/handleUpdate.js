define([ 'require', 'jquery', 'jquery.droidmate.ajax'
		,'jquery.droidmate.overlays' ], 
		function(require,jquery, DMAjax, DMOverlays) {

	function showControls() {
		jquery('#div-charts-elements-seen-screens-explored').show();
		jquery('#div-charts-apk-status-elements-explored').show();
		jquery('#div-chart-multiselect').show();
		jquery('#div-explorationcharts-bottom-navi').show();
	}
	
	function hideControls() {
		jquery('#div-charts-elements-seen-screens-explored').hide();
		jquery('#div-charts-apk-status-elements-explored').hide();
		jquery('#div-chart-multiselect').hide();
		jquery('#div-explorationcharts-bottom-navi').hide();
	}
	
	function updateUIControls(async, callback) {
		
		DMAjax.getUserStatus(async, function(data) {
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