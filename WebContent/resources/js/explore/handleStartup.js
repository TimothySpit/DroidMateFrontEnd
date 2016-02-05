define([ 'require',
		'jquery.droidmate.overlays','jquery.droidmate.explore','jquery.droidmate.ajax',
		'../explore/handleUpdate'], function(require) {

	var updateHelper = require('../explore/handleUpdate' );
	
	//update UI
	updateHelper.updateUI();
	
	//check user state to init exploration or just display results
	$.droidmate.ajax.getUserStatus(true, function(data) {
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
		
		//check user status: if idle, start exploration
		if(data.getUserStatus.payload.data === "IDLE") {
			//user is in idle state, exploration is not started yet, try to start it
			
		} else if(data.getUserStatus.payload.data === "INLINING") {
			//user is inlining files, but visits Explore page, go back to index
			window.location = "Index";
		} else if(data.getUserStatus.payload.data === "EXPLORING") {
			//user is still exploring, but has refreshed the page
			$.droidmate.overlays.info("DroidMate is still exploring...", 
					$.droidmate.overlays.INFO_MESSAGE_TIMEOUT);
		}
	});

});