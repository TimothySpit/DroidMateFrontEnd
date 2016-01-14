(function($) {
	//ajax object
	var droidmate = $.droidmate || {};
	var ajax = {};
	
	//constants
	ajax.UPDATE_EXPLORATION_INFO_INTERVAL = 1000;
	//--------------------------------------
	
	//create ajax get functions
	var get = {};
	function getDroidMateSettings() {
		var result = null;
		$.ajax({
	        url:  "/DroidMate/Settings",
	        async: false,
	        type: 'GET',
	        data: {get:["outputPath","droidmatePath","time"],},
	        success: function(data) {
	            result = data;
	        } 
	     });
		return result;
	}
	get.getDroidMateSettings = getDroidMateSettings;
	//----------------------------------
	
	function getExplorationInfo() {
		var result = null;
		$.ajax({
	        url:  "/DroidMate/APKExploreHandler",
	        async: false,
	        type: 'GET',
	        dataType: "json",
	        data: {explore_get_info: true},
	        success: function(data) {
	            result = data;
	        } 
	     });
		return result;
	}
	get.getExplorationInfo = getExplorationInfo;
	//----------------------------------
	
	function getReportPath(apkname) {
		var result = "/DroidMate/ReportProvider?get_report=" + apkname;
		return result;
	}
	get.getReportPath = getReportPath;
	//----------------------------------
	
	function saveReport(apkname) {
		$.ajax({
	        url:  "/DroidMate/ReportProvider",
	        async: false,
	        type: 'GET',
	        dataType: "json",
	        data: {save_report: apkname}
	     });
		return result;
	}
	get.saveReport = saveReport;
	//----------------------------------
	
	//----------------------------------
	
	//create ajax post functions
	var post = {};
	
	function setSelectedAPKS(ids, async, success, error, complete) {
		$.ajax({
			//Wait for the server to finish apk list and request the table data afterwards
		     async: async,
		     type: 'POST',
		     url: "/DroidMate/APKPathHandler",
		     data: { selApks : ids },
		     success: success,
		     error: error,
		     complete: complete
		});
	}
	post.setSelectedAPKS = setSelectedAPKS;
	//----------------------------------
	
	function setAPKRoot(newRoot, async, success, error, complete) {
	$.ajax({
		//Wait for the server to finish apk list and request the table data afterwards
	     async: async,
	     type: 'POST',
	     url: "/DroidMate/APKPathHandler",
	     data: { apkRoot : newRoot },
	     success: success,
	     error: error,
	     complete: complete
	});}
	post.setAPKRoot = setAPKRoot;
	//----------------------------------
	
	function saveDroidMateSettings(outputPath, dmPath, explorationTimeout, async, success, error, complete) {
		$.ajax({
			async: async,
			url: "/DroidMate/Settings",
			type: 'POST',
			data: { 
				save : true,
				outputPath : outputPath,
				droidmatePath : dmPath,
				time : explorationTimeout
			 },
		    success: success,
		    error: error,
		    complete: complete
		});
	}
	post.saveDroidMateSettings = saveDroidMateSettings;
	//----------------------------------
	
	function startDroidMate(success, error, complete) {
		return $.ajax({
			async: false,
			cache: false,
			url: "/DroidMate/APKExploreHandler",
			method: "POST",
			data: {
				explore_start : true
			},
			 success: success,
			 error: error,
			 complete: complete
		});
	}
	post.startDroidMate = startDroidMate;
	//-----------------------------------
	
	function restartDroidMate() {
		$.ajax({
			async: false,
			url: "/DroidMate/APKExploreHandler",
			method: "POST",
			data: {
				explore_restart : true
			}
		});
	}
	post.restartDroidMate = restartDroidMate;
	//-----------------------------------
	
	function stopDroidMate(success, error, complete) {console.log("here");
		$.ajax({
			async: false,
			url: "/DroidMate/APKExploreHandler",
			method: "POST",
			data: {
				explore_stop : true
			},
			 success: success,
			 error: error,
			 complete: complete
		});
	}
	post.stopDroidMate = stopDroidMate;
	//-----------------------------------
	
	ajax.get  = get;
	ajax.post = post;
	droidmate.ajax = ajax;
	$.droidmate = droidmate;
	
})(jQuery);