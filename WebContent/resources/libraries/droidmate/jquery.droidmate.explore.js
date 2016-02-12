define([ 'require', 'jquery'], function(require, jquery) {
	//ajax object
	var droidmate = jquery.droidmate || {};
	var explore = {};
	
	//constants
	explore.UPDATE_EXPLORE_INTERVAL = 1000;
	//--------------------------------------
	
	
	function startExploration(async, success, error, complete) {
		return jquery.ajax({
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
		jquery.ajax({
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
	jquery.droidmate = droidmate;
	
	return {
		UPDATE_EXPLORE_INTERVAL: explore.UPDATE_EXPLORE_INTERVAL,
		startExploration : explore.startExploration,
		stopExploration : explore.stopExploration,
	};
	
});