define([ 'require',
		'jquery.droidmate.overlays','jquery.droidmate.explore','jquery.droidmate.ajax',
		'../explore/apkExplorationTable', 'jquery.droidmate.dialogs', '../explore/Console'], function(require) {

	//get current apks exploration info table
	var tableCreator = require('../explore/apkExplorationTable');
	var table = tableCreator.initModul($('#table-apk-exploration-info'));
	//get ui updater
	var updateHelper = require('../explore/handleUpdate' );
	//create console
	var consoleCreator = require('../explore/Console');
	var console = consoleCreator.initModul($('#div-console-output'));
	console.addClassNamesForHeading('panel-heading');
	console.addClassNamesForContent('panel-body');
	
	var stopLoopFlag = false;
	
	function stopLoop() {
		stopLoopFlag = true;
	}
	
	//continues updating filled table
	function updateTableLoop() {
		if(stopLoopFlag) {
			return;
		}
		updateHelper.updateUI(function(userState) {
			if(!userState || !userState.getUserStatus || !userState.getUserStatus.result) {
				//error in retrieving user state
				return;
			}
			
			if(!userState.getUserStatus.payload || !userState.getUserStatus.payload.data ) {
				//error, user state could not be parsed
				return;
			}
			
			//if finished or error, show message
			if(userState.getUserStatus.payload.data === "FINISHED") {
				$.droidmate.dialogs.createOKTextDialog("DroidMate finished exploration", "DroidMate finished exploration.");
			
				return;
			} else if(userState.getUserStatus.payload.data === "ERROR") {
				$.droidmate.dialogs.createOKTextDialog("DroidMate crashed unexpectedly", "DroidMate crashed while exploring.");
				
				return;
			}
		
			updateTableData();
			
			updateConsoleData();
			
			setTimeout(updateTableLoop, $.droidmate.explore.UPDATE_EXPLORE_INTERVAL);
		});
	}
	
	table.on("row:open", function(e) {
		updateTableData();
	});
	
	function updateConsoleData() {
		//get console data
		$.droidmate.ajax.getConsoleOutput(true, 0, function(data) {
			if(!data || !data.getConsoleOutput || !data.getConsoleOutput.result) {
				//could not get console data
				return;
			}
			
			if(!data.getConsoleOutput.payload || !data.getConsoleOutput.payload.data) {
				//could get result data, but no console data
				return;
			}
			
			var consoleData = data.getConsoleOutput.payload.data;
			
			//set console data and parse new request index
			console.setText(consoleData);
		});
	}
	
	function updateTableData(apkData) {
		function updateTable(apkDataResult) {
			if(stopLoopFlag) {
				return;
			}
			$.each(apkDataResult.getAPKSData.payload.data, function(index,value) {
				//get exploration info
				var explorationInfo = value.explorationInfo;
				if(!explorationInfo) {
					//error in receiving exploration info
					return false;
				}
				
				//show only selected apks
				if(!value.isSelected) {
					//apk is not selected
					return;
				}
				
				//collect data
				var name = value.name;
				var timeSeconds = value.explorationInfo.timeSeconds;
				var elementsSeen = explorationInfo.elementsSeen;
				var screensSeen = explorationInfo.screensSeen;
				var widgetsClicked = explorationInfo.widgetsExplored;
				var status = value.explorationStatus;
				
				var row = table.getRowByName(name);
				if(row) {
					//row is already initialized
					row.updateTime(timeSeconds + "s");
					row.updateElementsSeen(elementsSeen);
					row.updateScreensSeen(screensSeen);
					row.updateWidgetsClicked(widgetsClicked);
					row.updateStatus(status);
				} else {
					//row need to be initialized
					row = table.addAPKData(name, timeSeconds + "s", elementsSeen, screensSeen,
							widgetsClicked, status);
					table.redraw();
				}
				
				//set chart data
				row.setElementsSeenChartData(explorationInfo.historyElements);
				row.setScreensSeenChartData(explorationInfo.historyScreens);
				row.setWidgetsExploredChartData(explorationInfo.historyWidgets);
			});
		}
		
		if(!apkData) {
			//init apkdata
			$.droidmate.ajax.getAPKSData(true,function(apkDataResult) {
				//check result
				if(!apkDataResult || !apkDataResult.getAPKSData || !apkDataResult.getAPKSData.result) {
					//path could not been set, show error message
					$.droidmate.overlays.danger("Could not parse server returned value. Is server running?", 
							$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
					return;
				}
				
				if(!apkDataResult.getAPKSData.payload || !apkDataResult.getAPKSData.payload.data) {
					//path could not been set, show error message
					$.droidmate.overlays.danger("APK data could not be parsed.", 
							$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
					return;
				}
					
				//all data correct, fill in table for the first time
				updateTable(apkDataResult);
			});
		} else {
			updateTable(apkData)
		}
	}
	
	//starts the updating of the exploration table and charts. Start this only once.
	function startUpdateLoop() {
		//updates table and reflects user state changes
		function updateLoop() {
			if(stopLoopFlag) {
				return;
			}
			
			//flag is loop should continue
			var continueLoop = false;

			updateHelper.updateUI(function(userState) {
				if(!userState || !userState.getUserStatus || !userState.getUserStatus.result) {
					//error in retrieving user state
					return;
				}
				
				if(!userState.getUserStatus.payload || !userState.getUserStatus.payload.data ) {
					//error, user state could not be parsed
					return;
				}
				
				//init table data
				updateTableData();
				//init console data
				updateConsoleData();
				
				//continue updating
				if(userState.getUserStatus.payload.data === "IDLE" || userState.getUserStatus.payload.data === "STARTING" || userState.getUserStatus.payload.data === "EXPLORING") {
					continueLoop = true;
				}
				
				//if finished or error, show message only when user was not visiting charts page
				var contextPath = document.referrer.split('/');
				if(!contextPath[4] || contextPath[4] !== "ExplorationCharts") {
					if(userState.getUserStatus.payload.data === "FINISHED") {
						$.droidmate.dialogs.createOKTextDialog("DroidMate finished exploration", "DroidMate finished exploration.");
					} else if(userState.getUserStatus.payload.data === "ERROR") {
						$.droidmate.dialogs.createOKTextDialog("DroidMate crashed unexpectedly", "DroidMate crashed while exploring.");
					}
				}
				//start updating table and charts
				if(userState.getUserStatus.payload.data === "EXPLORING") {
					//enable all needed buttons
					$('#button-stop-all').prop("disabled",false);
					$('#button-return-to-start').prop("disabled",false);
					
					updateTableLoop();
					return; //break out of this loop
				}
				
				if(continueLoop) {
					setTimeout(updateLoop, $.droidmate.explore.UPDATE_EXPLORE_INTERVAL);
				}
			});
		}
		
		//start update loop
		setTimeout(updateLoop, $.droidmate.explore.UPDATE_EXPLORE_INTERVAL);
	}

	return {
		startUpdateLoop : startUpdateLoop,
		stopLoop : stopLoop
	};
});