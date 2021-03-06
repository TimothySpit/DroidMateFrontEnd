define([ 'require', 'jquery' ], function(require, jquery) {
	// ajax object
	var droidmate = jquery.droidmate || {};
	var ajax = {};

	// constants
	ajax.UPDATE_EXPLORATION_INFO_INTERVAL = 1000;
	ajax.UPDATE_CONSOLE_OUTPUT_INTERVAL = 1000;
	// --------------------------------------

	function getAPKsRoot(async,success) {
		var result = "";
		jquery.ajax({
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
		jquery.ajax({
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
		jquery.ajax({
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

	function getGlobalExploration(async, success) {
		jquery.ajax({
			url : "/DroidMate/GlobalExploreHandler",
			async : async,
			dataType : "json",
			type : 'POST',
			data : {
				getGlobalExploration : true
			},
			success : success
		});
	}
	ajax.getGlobalExploration = getGlobalExploration;
	// ----------------------------------
	
	function getAPKSData(async, success) {
		jquery.ajax({
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
		jquery.ajax({
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
		jquery.ajax({
			// Wait for the server to finish apk list and request the table data
			// afterwards
			async : async,
			dataType : "json",
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

	function openOutputFolder(async, success) {
		jquery.ajax({
			type : 'POST',
			url : "/DroidMate/OutputFolderHandler",
			async : async,
			data : {
				openOutputFolder : true
			},
			dataType : "json",
			success : success
		});
	}
	ajax.openOutputFolder = openOutputFolder;
	// ----------------------------------

	function saveDroidMateSettings(outputPath, dmPath, aaptPath,
			explorationTimeout, async, success, error, complete) {
		jquery.ajax({
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

	function clearUser(async, success) {
		jquery.ajax({
			url : "/DroidMate/ClearUserHandler",
			async : async,
			dataType : "json",
			type : 'POST',
			data : {
				clearUser : true
			},
			success : success
		});
	}
	ajax.clearUser = clearUser;
	// ----------------------------------
	
	droidmate.ajax = ajax;
	jquery.droidmate = droidmate;

	return {
		UPDATE_EXPLORATION_INFO_INTERVAL : ajax.UPDATE_EXPLORATION_INFO_INTERVAL,
		UPDATE_CONSOLE_OUTPUT_INTERVAL : ajax.UPDATE_CONSOLE_OUTPUT_INTERVAL,
		getAPKsRoot : ajax.getAPKsRoot,
		getUserStatus : ajax.getUserStatus,
		getConsoleOutput : ajax.getConsoleOutput,
		getGlobalExploration : ajax.getGlobalExploration,
		getAPKSData : ajax.getAPKSData,
		setSelectedAPKS : ajax.setSelectedAPKS,
		setAPKsRoot: ajax.setAPKsRoot,
		openOutputFolder : ajax.openOutputFolder,
		saveDroidMateSettings : ajax.saveDroidMateSettings,
		clearUser : ajax.clearUser
	};
	
});