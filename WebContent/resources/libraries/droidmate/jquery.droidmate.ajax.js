define([ 'jquery' ], function(require) {
	// ajax object
	var droidmate = $.droidmate || {};
	var ajax = {};

	// constants
	ajax.UPDATE_EXPLORATION_INFO_INTERVAL = 1000;
	ajax.UPDATE_CONSOLE_OUTPUT_INTERVAL = 1000;
	// --------------------------------------

	function getAPKsRoot(async,success) {
		var result = "";
		$.ajax({
			url : "/DroidMate/APKRootFolderHandler",
			async : async,
			type : 'POST',
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
			type : 'POST',
			data : {
				getUserStatus : true
			},
			success :success
		});
	}
	ajax.getUserStatus = getUserStatus;
	// ----------------------------------

	function getConsoleOutput(async, startLine, success) {
		if (startLine == null || startLine < 0)
			startLine = 0;
		$.ajax({
			url : "/DroidMate/ConsoleOutputHandler",
			async : async,
			dataType : "json",
			type : 'POST',
			data : {
				getConsoleOutput : startLine.toString()
			},
			success : success
		});
	}
	ajax.getConsoleOutput = getConsoleOutput;
	// ----------------------------------

	function getAPKSData(async, success) {
		$.ajax({
			// Wait for the server to finish apk list and request the table data
			// afterwards
			async : async,
			type : 'POST',
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
			type : 'POST',
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
			type : 'POST',
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

	droidmate.ajax = ajax;
	$.droidmate = droidmate;

});