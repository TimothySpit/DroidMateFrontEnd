define([ 'jquery'], function(require) {
	//ajax object
	var droidmate = $.droidmate || {};
	var explore = {};
	
	//constants
	explore.UPDATE_EXPLORE_INTERVAL = 1000;
	//--------------------------------------
	
	
	function startExploration(async, success, error, complete) {
		return $.ajax({
			async : async,
			url : "/DroidMate/ExploreHandler",
			method : "POST",
			data : {
				startExploration : true
			},
			success : success,
			error : error,
			complete : complete
		});
	}
	explore.startExploration = startExploration;
	// -----------------------------------
	
	function stopExploration(async, success) {
		$.ajax({
			url : "/DroidMate/StopExplorationHandler",
			async : async,
			dataType : "json",
			type : 'POST',
			data : {
				stopExploration : true
			},
			success : success
		});
	}
	explore.stopExploration = stopExploration;
	// ----------------------------------
	
	droidmate.explore = explore;
	$.droidmate = droidmate;
	
});