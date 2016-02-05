define([ 'jquery' ], function(require) {
	// ajax object
	var droidmate = $.droidmate || {};
	var ajax = {};

	// constants
	ajax.UPDATE_EXPLORATION_INFO_INTERVAL = 1000;
	ajax.UPDATE_CONSOLE_OUTPUT_INTERVAL = 1000;
	// --------------------------------------

	function getDroidMateSettings(async,success) {
		$.ajax({
			url : "/DroidMate/Settings",
			async : async,
			type : 'GET',
			data : {
				get : [ "outputPath", "droidmatePath", "aaptPath", "time" ],
			},
			success : success
		});
	}
	ajax.getDroidMateSettings = getDroidMateSettings;
	// ----------------------------------

	function getAPKsRoot(async,success) {
		var result = "";
		$.ajax({
			url : "/DroidMate/APKRootFolderHandler",
			async : async,
			type : 'GET',
			data : {
				getAPKsRoot : true
			},
			success : success
		});
		return result;
	}
	ajax.getAPKsRoot = getAPKsRoot;
	// ----------------------------------

	function getUserStatus(async, success) {
		$.ajax({
			url : "/DroidMate/UserStatusHandler",
			async : async,
			type : 'GET',
			data : {
				getUserStatus : true
			},
			success :success
		});
	}
	ajax.getUserStatus = getUserStatus;
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
	ajax.getGlobalStartingTime = getGlobalStartingTime;

	// ----------------------------------

	function getConsoleOutput(startLine, async, success) {
		if (startLine == null)
			startLine = 0;
		$.ajax({
			url : "/DroidMate/ConsoleOutput",
			async : false,
			dataType : "json",
			type : 'GET',
			data : {
				get : startLine.toString()
			},
			success : success
		});
	}
	ajax.getConsoleOutput = getConsoleOutput;
	// ----------------------------------

	function getGlobalElementsSeen(async, success) {
		$.ajax({
			url : "/DroidMate/APKExploreHandler",
			async : async,
			type : 'GET',
			data : {
				explore_get_global_elements_seen : true
			},
			success : success
		});
	}
	ajax.getGlobalElementsSeen = getGlobalElementsSeen;
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
	ajax.getGlobalElementsSeenHistory = getGlobalElementsSeenHistory;
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
	ajax.getGlobalWidgetsExplored = getGlobalWidgetsExplored;
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
	ajax.getGlobalWidgetsExploredHistory = getGlobalWidgetsExploredHistory;
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
	ajax.getGlobalScreensSeen = getGlobalScreensSeen;
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
	ajax.getGlobalScreensSeenHistory = getGlobalScreensSeenHistory;
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
	ajax.getExplorationInfo = getExplorationInfo;

	// ----------------------------------

	function getAPKSData(async, success) {
		$.ajax({
			// Wait for the server to finish apk list and request the table data
			// afterwards
			async : async,
			type : 'GET',
			url : "/DroidMate/APKInformationHandler",
			data : {
				getAPKSData : true
			},
			success : success
		});
	}
	ajax.getAPKSData = getAPKSData;
	// ----------------------------------
	

	function setSelectedAPKS(names, async, success, error, complete) {
		if( Object.prototype.toString.call( names ) !== '[object Array]' ) {
			names = [names];
		}
		$.ajax({
			// Wait for the server to finish apk list and request the table data
			// afterwards
			async : async,
			type : 'POST',
			url : "/DroidMate/SaveSelectedAPKSHandler",
			data : {
				setSelectedAPKS : names
			},
			success : success,
			error : error,
			complete : complete
		});
	}
	ajax.setSelectedAPKS = setSelectedAPKS;
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
	ajax.setAPKsRoot = setAPKsRoot;
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
	ajax.openReportFolder = openReportFolder;
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
	ajax.saveDroidMateSettings = saveDroidMateSettings;
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
	ajax.startDroidMate = startDroidMate;
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
	ajax.restartDroidMate = restartDroidMate;
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
	ajax.stopDroidMate = stopDroidMate;
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
	ajax.returnToIndex = returnToIndex;
	// -----------------------------------

	droidmate.ajax = ajax;
	$.droidmate = droidmate;

});