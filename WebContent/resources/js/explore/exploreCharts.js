$(function() {
	
	var elementsSeen = [[5, 300], [2, 600], [4, 550], [3, 400], [1, 300]];
	var elementsExplored = [[0,0],[0,0],[0,0],[0,0],[0,0]];
	var screensExplored = [[1, 500], [2, 500], [3, 500], [4, 500], [5, 300]];
	var successfulAPKs = 40, failedAPKs = 30, remainingAPKs=10;
	var updateInterval = 500;
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
		var options = {
		        series: {
		            pie: { 
		                show: true,
		                radius: 1,
		                label: {
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
	                axisLabelFontFamily: 'Arial'
	            },
	            xaxis: {
	                labelHeight: 30,
	                axisLabel: 'time (min)',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 15,
	                axisLabelFontFamily: 'Arial'
	            }
	        };
	    getDataElementsSeen();
	    console.log(elementsSeen);
	    elementsSeen.slice(-5);
	    chartGUIElementsSeen = $.plot(divname, [elementsSeen], options);
	    setTimeout(updateElementsSeen, divname, updateInterval);
	};

	function createChartGUIScreensExplored(divname) {
	    var options =  {
	            yaxis: {
	                labelWidth: 30,
	                axisLabel: 'Explored',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 20,
	                axisLabelFontFamily: 'Arial'
	            },
	            xaxis: {
	                labelHeight: 30,
	                axisLabel: 'time (min)',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 15,
	                axisLabelFontFamily: 'Arial'
	            }
	        };
	    chartGUIScreensExplored = $.plot(divname, [elementsExplored], options);
	    setTimeout(updateScreensExplored, divname, updateInterval);
	};


	function createChartGUIElementsAndScreens(divname) {
	    var options =  {
	            yaxis: {
	                labelWidth: 30,
	                axisLabel: 'explored',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 20,
	                axisLabelFontFamily: 'Arial'
	            },
	            xaxis: {
	                labelHeight: 30,
	                axisLabel: 'time (min)',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 15,
	                axisLabelFontFamily: 'Arial'
	            }
	        };
	    chartElementsAndScreens = $.plot(divname, dataArray, options);
	    chartElementsAndScreens = $.plot(divname, 
	    	    [{ data: screensExplored, label: "Screens" },
	    		{ data: elementsSeen, label: "Elements"}],
	    	    options);
	    setTimeout(updateLines, divname, updateInterval);
	};


	function createChartGUIElementsExplored(divname) {
	    var options =  {
	            yaxis: {
	                labelWidth: 30,
	                axisLabel: 'GUI elements explored',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 20,
	                axisLabelFontFamily: 'Arial'
	            },
	            xaxis: {
	                labelHeight: 30,
	                axisLabel: 'time (min)',
	                axisLabelUseCanvas: true,
	                axisLabelFontSizePixels: 15,
	                axisLabelFontFamily: 'Arial'
	            }
	        };
	    chartGUIElementsExplored = $.plot(divname, [elementsExplored], options);
	    setTimeout(updateElementsExplored, divname, updateInterval);
	};

	function updateAPKValues()
	{
		var apkArray = $.droidmate.ajax.get.getExplorationInfo();
		successfulAPKs = 0;
		failedAPKs = 0;
		for(var i = 0; i < apkArray.length; i++)
		{
			apk = apkArray[i];
			console.log(apk);
			if (apk.finished == "True")
			{
				if (apk.success == "True")
					successfulAPKs++;
				else
					failedAPKs++;
			}
		}
	}

	function getDataElementsSeen()
	{
		var newData = $.droidmate.ajax.get.getGlobalElementsSeen();
		elementsSeen = newData.slice[-dataVisible];
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

	function addRandomValue(currentData)
	{
		currentData.shift();
		var y = Math.random(400) * 400;
		lastUpdate += updateInterval / 1000;
		var temp = [lastUpdate, y]; 
		currentData.push(temp);
	}

	function addRandomY(currentData, newX)
	{
		currentData.shift();
		var y = Math.random(400) * 400;
		var temp = [newX, y]; 
		currentData.push(temp);
	}

	function updateLines(divname)
	{
		chart = chartElementsAndScreens;

		lastUpdate += updateInterval / 1000 / 60;
		addRandomY(elementsExplored, lastUpdate);
		addRandomY(screensExplored, lastUpdate);
		
		data1 = elementsExplored;
		data2 = screensExplored;//.slice(Math.min(screensExplored.length(), 5));
		chart.setData([{ data: elementsExplored, label: "Screens" },
		           	{ data: screensExplored, label: "Elements"}]);
		chart.draw();
		setTimeout(updateLines, divname, updateInterval);
		
	}

	function updateElementsExplored(divname)
	{
		chart = chartGUIElementsExplored;
		data = elementsExplored;
		addRandomValue(data); 
		chart.setData([data]);
		chart.draw();
		
		setTimeout(updateElementsExplored, divname, updateInterval);
	}

	function updateElementsSeen(divname)
	{
		elementsSeen = getDataElementsSeen();
	    elementsSeen.slice(-5);
		chart = chartGUIElementsSeen;

		chart.setData([elementsSeen]);
		chart.draw();
		setTimeout(updateElementsSeen, divname, updateInterval);
	}

	function updateScreensExplored(divname)
	{
		chart = chartGUIScreensExplored;
		data = screensExplored;
		addRandomValue(data); 
		
		chart.setData([data]);
		chart.draw();
		setTimeout(updateScreensExplored, divname, updateInterval);
	}
	
	//createChartGUIElementsToExplore("#flot-gui-elements-not-seen");
	createChartAPKStatus('#flot-apks-status');
	createChartGUIElementsSeen('#flot-gui-elements-seen');
	
	//createChartGUIScreensExplored("#flot-gui-screens-explored");
	//createChartGUIElementsAndScreens("#flot-gui-screens-explored");
	
	
	});

