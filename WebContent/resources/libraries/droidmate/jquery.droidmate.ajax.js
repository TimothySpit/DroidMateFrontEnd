define([ 'jquery' ], function(require) {
	// ajax object
	var droidmate = $.droidmate || {};
	var ajax = {};

	// constants
	ajax.UPDATE_EXPLORATION_INFO_INTERVAL = 1000;
	ajax.UPDATE_CONSOLE_OUTPUT_INTERVAL = 1000;
	// --------------------------------------

	// create ajax get functions
	var get = {};
	function getDroidMateSettings() {
		var result = null;
		$.ajax({
			url : "/DroidMate/Settings",
			async : false,
			type : 'GET',
			data : {
				get : [ "outputPath", "droidmatePath", "aaptPath", "time" ],
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getDroidMateSettings = getDroidMateSettings;
	// ----------------------------------

	function getAPKsRoot() {
		var result = "";
		$.ajax({
			url : "/DroidMate/APKRootFolderHandler",
			async : false,
			type : 'GET',
			data : {
				getAPKsRoot : true
			},
			success : function(data) {
				if(!data) {
					return;
				}
				result = data.getAPKsRoot;
			}
		});
		return result;
	}
	get.getAPKsRoot = getAPKsRoot;
	// ----------------------------------

	function getExplorationStatus() {
		var result = "";
		$.ajax({
			url : "/DroidMate/APKExploreHandler",
			async : false,
			type : 'GET',
			data : {
				status : ""
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getExplorationStatus = getExplorationStatus;
	// ----------------------------------

	function getGlobalStartingTime() {
		var result = [];
		$.ajax({
			url : "/DroidMate/APKExploreHandler",
			async : false,
			type : 'GET',
			data : {
				explore_get_global_starting_time : true
			},
			success : function(data) {
				if (data.status == "ok") {
					result = data.timestamp;
				} else {
					result = null;
				}
			}
		});
		return result;
	}
	get.getGlobalStartingTime = getGlobalStartingTime;

	// ----------------------------------

	function getConsoleOutput(line) {
		var result = "";
		if (line == null)
			line = 0;
		$.ajax({
			url : "/DroidMate/ConsoleOutput",
			async : false,
			dataType : "json",
			type : 'GET',
			data : {
				get : line.toString()
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getConsoleOutput = getConsoleOutput;
	// ----------------------------------

	function getGlobalElementsSeen() {
		var result = [];
		$.ajax({
			url : "/DroidMate/APKExploreHandler",
			async : false,
			type : 'GET',
			data : {
				explore_get_global_elements_seen : true
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getGlobalElementsSeen = getGlobalElementsSeen;
	// ----------------------------------

	function getGlobalElementsSeenHistory() {
		var result = [];
		$.ajax({
			url : "/DroidMate/APKExploreHandler",
			async : false,
			type : 'GET',
			dataType : "json",
			data : {
				explore_get_global_elements_seen_history : true
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getGlobalElementsSeenHistory = getGlobalElementsSeenHistory;
	// ----------------------------------

	function getGlobalWidgetsExplored() {
		var result = [];
		$.ajax({
			url : "/DroidMate/APKExploreHandler",
			async : false,
			type : 'GET',
			data : {
				explore_get_global_widgets_explored : true
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getGlobalWidgetsExplored = getGlobalWidgetsExplored;
	// ----------------------------------

	function getGlobalWidgetsExploredHistory() {
		var result = null;
		$.ajax({
			url : "/DroidMate/APKExploreHandler",
			async : false,
			type : 'GET',
			dataType : "json",
			data : {
				explore_get_global_widgets_explored_history : true
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getGlobalWidgetsExploredHistory = getGlobalWidgetsExploredHistory;
	// ----------------------------------

	function getGlobalScreensSeen() {
		var result = null;
		$.ajax({
			url : "/DroidMate/APKExploreHandler",
			async : false,
			type : 'GET',
			data : {
				explore_get_global_screens_seen : true
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getGlobalScreensSeen = getGlobalScreensSeen;
	// ----------------------------------

	function getGlobalScreensSeenHistory() {
		var result = null;
		$.ajax({
			url : "/DroidMate/APKExploreHandler",
			async : false,
			type : 'GET',
			dataType : "json",
			data : {
				explore_get_global_screens_seen_history : true
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getGlobalScreensSeenHistory = getGlobalScreensSeenHistory;
	// ----------------------------------

	function getExplorationInfo(apkname) {
		var result = null;
		$.ajax({
			url : "/DroidMate/APKExploreHandler",
			async : false,
			type : 'GET',
			dataType : "json",
			data : {
				explore_get_info : true,
				explore_get_info_apkname : apkname
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getExplorationInfo = getExplorationInfo;

	// ----------------------------------

	function getAPKSData() {
		var result = null;
		$.ajax({
			// Wait for the server to finish apk list and request the table data
			// afterwards
			async : false,
			type : 'GET',
			url : "/DroidMate/APKInformationHandler",
			data : {
				getAPKSData : true
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	}
	get.getAPKSData = getAPKSData;
	// ----------------------------------

	// create ajax post functions
	var post = {};

	function saveReport() {
		var reportHtml = new XMLSerializer().serializeToString(document);
		$.ajax({
			url : "/DroidMate/ReportProvider",
			async : false,
			type : 'POST',
			dataType : "json",
			data : {
				save_report_html : reportHtml
			}
		});
	}
	post.saveReport = saveReport;
	// ----------------------------------

	function setSelectedAPKS(ids, async, success, error, complete) {
		$.ajax({
			// Wait for the server to finish apk list and request the table data
			// afterwards
			async : async,
			type : 'POST',
			url : "/DroidMate/APKPathHandler",
			data : {
				selApks : ids
			},
			success : success,
			error : error,
			complete : complete
		});
	}
	post.setSelectedAPKS = setSelectedAPKS;
	// ----------------------------------

	function setAPKsRoot(newRoot, async, success, error, complete) {
		$.ajax({
			// Wait for the server to finish apk list and request the table data
			// afterwards
			async : async,
			type : 'GET',
			url : "/DroidMate/APKRootFolderHandler",
			data : {
				setAPKsRoot : newRoot
			},
			success : success,
			error : error,
			complete : complete
		});
	}
	post.setAPKsRoot = setAPKsRoot;
	// ----------------------------------

	function openReportFolder(async) {
		var status = false;
		$.ajax({
			type : 'POST',
			url : "/DroidMate/APKExploreHandler",
			async : async,
			data : {
				explore_open_report_folder : true
			},
			dataType : "json",
			success : function(data) {
				status = data.status == "success";
			},
			error : function(data) {
				status = false;
			}
		});

		return status;
	}
	post.openReportFolder = openReportFolder;
	// ----------------------------------

	function saveDroidMateSettings(outputPath, dmPath, aaptPath,
			explorationTimeout, async, success, error, complete) {
		$.ajax({
			async : async,
			url : "/DroidMate/SettingsHandler",
			type : 'GET',
			data : {
				setSettings: true,
				outputPath : outputPath,
				droidmatePath : dmPath,
				aaptPath : aaptPath,
				time : explorationTimeout
			},
			success : success,
			error : error,
			complete : complete
		});
	}
	post.saveDroidMateSettings = saveDroidMateSettings;
	// ----------------------------------

	function startDroidMate(success, error, complete) {
		return $.ajax({
			async : false,
			cache : false,
			url : "/DroidMate/APKExploreHandler",
			method : "POST",
			data : {
				explore_start : true
			},
			success : success,
			error : error,
			complete : complete
		});
	}
	post.startDroidMate = startDroidMate;
	// -----------------------------------

	function restartDroidMate() {
		$.ajax({
			async : false,
			url : "/DroidMate/APKExploreHandler",
			method : "POST",
			data : {
				explore_restart : true
			}
		});
	}
	post.restartDroidMate = restartDroidMate;
	// -----------------------------------

	function stopDroidMate(success, error, complete) {
		$.ajax({
			async : false,
			url : "/DroidMate/APKExploreHandler",
			method : "POST",
			data : {
				explore_stop : true
			},
			success : success,
			error : error,
			complete : complete
		});
	}
	post.stopDroidMate = stopDroidMate;
	// -----------------------------------

	function returnToIndex(success, error, complete) {
		$.ajax({
			async : false,
			url : "/DroidMate/APKExploreHandler",
			method : "POST",
			data : {
				return_to_index : true
			},
			success : success,
			error : error,
			complete : complete
		});
	}
	post.returnToIndex = returnToIndex;
	// -----------------------------------

	ajax.get = get;
	ajax.post = post;
	droidmate.ajax = ajax;
	$.droidmate = droidmate;

});