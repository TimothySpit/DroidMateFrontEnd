$(function() {
	
	var elementsSeen = [[5, 300], [2, 600], [4, 550], [3, 400], [1, 300]];
	var screensExplored = [[1, 500], [2, 500], [3, 500], [4, 500], [5, 300]];
	var testData = [[1, 700], [2, 10], [3, 0], [4, 50], [5, 100], [6, 230], [7, 400], [8, 100], [9, 520], [10, 301]];
	var successfulAPKs = 40, failedAPKs = 30, remainingAPKs=10;
	var updateInterval = 1000;
	var dataVisible = 5;
	var lastUpdate = 0;
	var chartAPKStatus, chartGUIElementsExplored, chartGUIElementsSeen, chartGUIScreensExplored, chartElementsAndScreens;
	
	function createChartAPKStatus(divname)
	{
		//successfulAPKs, failedAPKs, remainingAPKs;
		updateAPKValues();
		var dataSet = [
		               {label: "Successful", data: successfulAPKs, color: "#00A36A"},
		               { label: "Failed", data: failedAPKs, color: "#005CDE"},
		               { label: "Remaining", data: remainingAPKs, color: "#7D0096" }    
		           ];
		var options = 
		{
		        series: 
		        {
		            pie: 
		            { 
		                show: true,
		                radius: 1,
		                label: 
		                {
		                    show: true,
		                    radius: 1,
		                    formatter: function(label, series){
		                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
		                    },
		                    background: { opacity: 0.8 }
		                }
		            }
		        },
		        legend: {
		            show: false
		        }
		};
		chartAPKStatus = $.plot($(divname), dataSet, options);
		setTimeout(updateChartAPKStatus, divname, updateInterval);
	}

	function createChartGUIElementsSeen(divname) {
		var options =  {
	            yaxis: {
	                labelWidth: 30,
	                axisLabel: 'GUI elements seen',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 20,
	                axisLabelFontFamily: 'Arial',
	                panRange: false,
	                minTickSize: 1
	            },
	            xaxis: {
	                labelHeight: 30,
	                axisLabel: 'time (sec)',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 15,
	                axisLabelFontFamily: 'Arial',
	                panRange: [0, null],
	                min : 0,
					minTickSize: 1
	            },
	            pan: {
	    			interactive: true
	    		}
	        };
	    elementsSeen = getDataElementsSeen();
	    chartGUIElementsSeen = $.plot(divname, [elementsSeen], options);
	    setTimeout(updateElementsSeen, divname, updateInterval);
	};

	function createChartGUIScreensExplored(divname) {
		var options =  {
	            yaxis: {
	                labelWidth: 30,
	                axisLabel: 'screens explored',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 20,
	                axisLabelFontFamily: 'Arial',
	                panRange: false,
	                minTickSize: 1
	            },
	            xaxis: {
	                labelHeight: 30,
	                axisLabel: 'time (sec)',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 15,
	                axisLabelFontFamily: 'Arial',
	                panRange: [0, null],
	                min : 0,
					minTickSize: 1
	            },
	            pan: {
	    			interactive: true
	    		}
	        };
	    screensExplored = getDataScreensExplored();
	    chartGUIScreensExplored = $.plot(divname, [screensExplored], options);
	    setTimeout(updateScreensExplored, divname, updateInterval);
	};


	function createChartGUIElementsAndScreens(divname) {
	    var options =  {
	            yaxis: {
	                labelWidth: 30,
	                axisLabel: 'explored',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 20,
	                axisLabelFontFamily: 'Arial',
	                panRange: false,
	                minTickSize: 1
	            },
	            xaxis: {
	                labelHeight: 30,
	                axisLabel: 'time (sec)',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 15,
	                axisLabelFontFamily: 'Arial',
	                panRange: [0, null],
	                min : 0,
					minTickSize: 1
	            },
	            pan: {
	    			interactive: true
	    		}
	        };
	    chartElementsAndScreens = $.plot(divname, testData, options);
	    /*chartElementsAndScreens = $.plot(divname, 
	    	    [{ data: screensExplored, label: "Screens" },
	    		{ data: elementsSeen, label: "Elements"}],
	    	    options);*/
	    setTimeout(updateLines, divname, updateInterval);
	};

	function updateAPKValues()
	{
		var apkArray = $.droidmate.ajax.get.getExplorationInfo();
		var selAPKSSize = $.droidmate.ajax.get.getSelectedAPKS()["info[]"].selApks.data.length;

		successfulAPKs = 0;
		failedAPKs = 0;
		for(var i = 0; i < apkArray.length; i++)
		{
			apk = apkArray[i];
			
			if (apk.finished)
			{
				if (apk.success)
					successfulAPKs++;
				else
					failedAPKs++;
			}
		}
		remainingAPKs = selAPKSSize - successfulAPKs - failedAPKs;
	}

	function getDataElementsSeen()
	{
		var newData = $.droidmate.ajax.get.getGlobalElementsSeenHistory();
		//var output = newData.slice(-dataVisible);
		return newData;
	}
	
	function getDataUpdateLines()
	{
		var newData = $.droidmate.ajax.get.getGlobalWidgetsExploredHistory();
		//var output = newData.slice(-dataVisible);
		return newData;
	}
	
	function getDataScreensExplored()
	{
		var newData = $.droidmate.ajax.get.getGlobalScreensSeenHistory();
		//var output = newData.slice(-dataVisible);
		return newData;
	}



	function updateChartAPKStatus(divname)
	{
		updateAPKValues();
		var dataSet = [
		               {label: "Successful", data: successfulAPKs, color: "#00A36A"},
		               { label: "Failed", data: failedAPKs, color: "#005CDE"},
		               { label: "Remaining", data: remainingAPKs, color: "#7D0096" }    
		           ];
		chartAPKStatus.setData(dataSet);
		chartAPKStatus.draw();
		setTimeout(updateChartAPKStatus, divname, updateInterval);
	}

	function updateLines(divname)
	{
		chart = chartElementsAndScreens;

		data = getDataUpdateLines();
		chart.setData([data]);
		
		chart.draw();
		setTimeout(updateLines, divname, updateInterval);
		
	}

	function updateElementsSeen(divname)
	{
		elementsSeen = getDataElementsSeen();
		chart = chartGUIElementsSeen;

		chart.setData([elementsSeen]);
		chart.draw();
		setTimeout(updateElementsSeen, divname, updateInterval);
	}

	function updateScreensExplored(divname)
	{
		chart = chartGUIScreensExplored;
		screensExplored = getDataScreensExplored();
		chart.setData([screensExplored]);
		chart.draw();
		setTimeout(updateScreensExplored, divname, updateInterval);
	}
	
	//createChartGUIElementsToExplore("#flot-gui-elements-not-seen");
	createChartAPKStatus('#flot-apks-status');
	createChartGUIElementsSeen('#flot-gui-elements-seen');
	
	createChartGUIScreensExplored("#flot-gui-screens-explored");
	createChartGUIElementsAndScreens("#flot-gui-elements-explored");
	
	
	});

