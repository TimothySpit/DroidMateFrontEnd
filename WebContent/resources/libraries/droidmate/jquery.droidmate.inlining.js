(function($) {
	//ajax object
	var droidmate = $.droidmate || {};
	var inlining = {};
	
	//constants
	inlining.WATCH_INLINER_INTERVAL = 1000;
	//--------------------------------------
	
	//Inlining status
	var inliningStatus = {
			NOT_STARTED : "NOT_STARTED",
			INLINING	: "INLINING",
			FINISHED	: "FINISHED",
			ERROR		: "ERROR",
	};
	inlining.inliningStatus = inliningStatus;
	//--------------------------------------
	
	function startInlining() {
		var result = null;
		$.ajax({
	        url:  "/DroidMate/APKInliningHandler",
	        async: false,
	        type: 'POST',
	        data: {inline : true},
	        success: function(data) {
	            result = data;
	        } 
	     });
		return result.success;
	}
	inlining.startInlining = startInlining;
	//--------------------------------------
	
	function getInliningStatus() {
		var result = null;
		$.ajax({
	        url:  "/DroidMate/APKInliningHandler",
	        async: false,
	        type: 'POST',
	        data: {status : true},
	        success: function(data) {
	            result = data;
	        } 
	     });
		return result.status;
	}
	inlining.getInliningStatus = getInliningStatus;
	//--------------------------------------
	
	droidmate.inlining = inlining;
	$.droidmate = droidmate;
	
})(jQuery);