define([ 'require', 'jquery'], function(require, jquery) {
	//ajax object
	var droidmate = jquery.droidmate || {};
	var inlining = {};
	
	//constants
	inlining.WATCH_INLINER_INTERVAL = 1000;
	//--------------------------------------
	
	//Inlining status
	var inliningStatus = {
			NOT_INLINED : "NOT_INLINED",
			INLINING	: "INLINING",
			INLINED		: "INLINED"
	};
	inlining.inliningStatus = inliningStatus;
	//--------------------------------------
	
	function startInlining(async, success) {
		jquery.ajax({
	        url:  "/DroidMate/InlinerHandler",
	        async: async,
	        type: 'POST',
	        dataType : "json",
	        data: {startInlining : true},
	        success: success
	     });
	}
	inlining.startInlining = startInlining;
	//--------------------------------------
	
	droidmate.inlining = inlining;
	jquery.droidmate = droidmate;
	
	return {
		WATCH_INLINER_INTERVAL: inlining.WATCH_INLINER_INTERVAL,
		inliningStatus : inlining.inliningStatus,
		startInlining : inlining.startInlining,
	};
	
});