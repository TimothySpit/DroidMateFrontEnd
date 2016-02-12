define([ 'require', 'jquery',
		'jquery.droidmate.overlays','jquery.droidmate.explore','jquery.droidmate.ajax',
		'../explore/handleUpdate', '../explore/handleExploreUpdate'], 
		function(require, jquery, DMOverlays, DMExplore, DMAjax, updateHelper, explorationUpdater) {

	//update UI
	updateHelper.updateUI(function(data) {
		//no connection to server or result was not correctly formatted
		if(!data || !data.getUserStatus) {
			DMOverlays.danger("Could not parse server returned value.", 
					DMOverlays.DANGER_MESSAGE_TIMEOUT);
			return;
		}
		
		//could not get user status (should never happen)
		if(!data.getUserStatus.result) {
			DMOverlays.danger(getUserStatus.getUserStatus.message, 
					DMOverlays.DANGER_MESSAGE_TIMEOUT);
			return;
		}
		
		//check user status: if idle, start DroidMate
		if(data.getUserStatus.payload.data === "IDLE") {
			//user is in idle state, exploration is not started yet, try to start it
			DMOverlays.info("Starting DroidMate...", 
					DMOverlays.INFO_MESSAGE_TIMEOUT);
			
			//send request
			DMExplore.startExploration(true,function(data) {
				if(!data || !data.startExploration) {
					//no connection to the server, or wrong answer
					DMOverlays.danger("Could not parse server returned value.", 
							DMOverlays.DANGER_MESSAGE_TIMEOUT);
					return;
				}
				
				//DroidMate could not be started
				if(!data.startExploration.result) {
					DMOverlays.danger(data.startExploration.message, 
							DMOverlays.DANGER_MESSAGE_TIMEOUT);
					return;
				}
			});
		} else if(data.getUserStatus.payload.data === "INLINING") {
			//user is inlining files, but visits Explore page, go back to index
			window.location = "Index";
		} else if(data.getUserStatus.payload.data === "STARTING") {
		} else if(data.getUserStatus.payload.data === "EXPLORING") {
			//user is still exploring, but has refreshed the page
			DMOverlays.info("DroidMate is still exploring...", 
					DMOverlays.INFO_MESSAGE_TIMEOUT);
		} else if(data.getUserStatus.payload.data === "FINISHED") {
			DMOverlays.success("DroidMate finished exploration.", 
					DMOverlays.SUCCESS_MESSAGE_TIMEOUT);
		} else if(data.getUserStatus.payload.data === "ERROR") {
			DMOverlays.danger("DroidMate crashed while exploring.", 
					DMOverlays.DANGER_MESSAGE_TIMEOUT);
		}
		
		//start update loop
		explorationUpdater.startUpdateLoop();
	});

	//handle return to start button
	$('#button-return-to-start').click(function() {
		//disable loop
		explorationUpdater.stopLoop();
		updateHelper.showReturnIndicator();
		
		//stop droidmate
		DMExplore.stopExploration(true,function(data) {
			
			//reset state
			DMAjax.clearUser(true, function(result) {
				window.location = "Index";
			});
		});
	});
	
	//handle stop all button
	$('#button-stop-all').click(function() {
		DMOverlays.info("Stopping DroidMate... This could take a while.", 
				DMOverlays.INFO_MESSAGE_TIMEOUT);
		//disable stop button
		$('#button-stop-all').prop("disabled",true);
		$('#button-return-to-start').prop("disabled",true);
		$('#button-show-apk-details-dynamic').prop("disabled",true);
		//send stop request
		DMExplore.stopExploration(true,function(data) {
			$('#button-return-to-start').prop("disabled",false);
			$('#button-show-apk-details-dynamic').prop("disabled",false);
			DMOverlays.info("DroidMate stopped.", 
					DMOverlays.INFO_MESSAGE_TIMEOUT);
		});
	});
});