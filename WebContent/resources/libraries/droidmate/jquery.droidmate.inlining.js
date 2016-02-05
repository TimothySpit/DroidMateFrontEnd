define([ 'jquery'], function(require) {
	//ajax object
	var droidmate = $.droidmate || {};
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
		$.ajax({
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
	$.droidmate = droidmate;
	
});