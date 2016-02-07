define([ 'require',
		'jquery.droidmate.overlays','jquery.droidmate.explore','jquery.droidmate.ajax',
		'../explore/handleUpdate', '../explore/handleExploreUpdate'], function(require) {

	var updateHelper = require('../explore/handleUpdate' );
	var explorationUpdater = require('../explore/handleExploreUpdate');
	
	//update UI
	updateHelper.updateUI(function(data) {
		//no connection to server or result was not correctly formatted
		if(!data || !data.getUserStatus) {
			$.droidmate.overlays.danger("Could not parse server returned value.", 
					$.droidmate.overlays.DANGER_MESSAGE_TIMEOUT);
			return;
		}
		
		//could not get user status (should never happen)
		if(!data.getUserStatus.result) {
			$.droidmate.overlays.danger(getUserStatus.getUserStatus.message, 
					$.droidmate.overlays.DANGER_MESSAGE_TIMEOUT);
			return;
		}
		
		//check user status: if idle, start DroidMate
		if(data.getUserStatus.payload.data === "IDLE") {
			//user is in idle state, exploration is not started yet, try to start it
			$.droidmate.overlays.info("Starting DroidMate...", 
					$.droidmate.overlays.INFO_MESSAGE_TIMEOUT);
			
			//send request
			$.droidmate.explore.startExploration(true,function(data) {
				if(!data || !data.startExploration) {
					//no connection to the server, or wrong answer
					$.droidmate.overlays.danger("Could not parse server returned value.", 
							$.droidmate.overlays.DANGER_MESSAGE_TIMEOUT);
					return;
				}
				
				//DroidMate could not be started
				if(!data.startExploration.result) {
					$.droidmate.overlays.danger(data.startExploration.message, 
							$.droidmate.overlays.DANGER_MESSAGE_TIMEOUT);
					return;
				}
			});
		} else if(data.getUserStatus.payload.data === "INLINING") {
			//user is inlining files, but visits Explore page, go back to index
			window.location = "Index";
		} else if(data.getUserStatus.payload.data === "STARTING") {
		} else if(data.getUserStatus.payload.data === "EXPLORING") {
			//user is still exploring, but has refreshed the page
			$.droidmate.overlays.info("DroidMate is still exploring...", 
					$.droidmate.overlays.INFO_MESSAGE_TIMEOUT);
		} else if(data.getUserStatus.payload.data === "FINISHED") {
			$.droidmate.overlays.success("DroidMate finished exploration.", 
					$.droidmate.overlays.SUCCESS_MESSAGE_TIMEOUT);
		} else if(data.getUserStatus.payload.data === "ERROR") {
			$.droidmate.overlays.danger("DroidMate crashed while exploring.", 
					$.droidmate.overlays.DANGER_MESSAGE_TIMEOUT);
		}
		
		//start update loop
		explorationUpdater.startUpdateLoop();
	});

});