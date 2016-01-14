$(document).ready()
	{
	var elementsToExplore = [[5, 300], [2, 600], [4, 550], [3, 400], [1, 300]];
	var elementsExplored = [[0,0],[0,0],[0,0],[0,0],[0,0]];
	var screensExplored = [[1, 500], [2, 500], [3, 500], [4, 500], [5, 300]];
	var successfulAPKs = 40, failedAPKs = 30, remainingAPKs=10;
	var updateInterval = 500;
	var lastUpdate = 0;
	var chartAPKStatus, chartGUIElementsExplored, chartGUIElementsToExplore, chartGUIScreensExplored, chartElementsAndScreens;
	}



function createChartGUIElementsToExplore(divname) {
    var options =  {
            yaxis: {
                labelWidth: 30,
                axisLabel: 'GUI elements to explore',
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
    //elementsToExplore = getDataElementsToExplore();
    //elementsToExplore.slice(-5);
    elementsToExplore.sort();
    chartGUIElementsToExplore = $.plot(divname, [elementsToExplore], options);
    setTimeout(updateElementsToExplore, divname, updateInterval);
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
    		{ data: elementsToExplore, label: "Elements"}],
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

function getDataElementsToExplore()
{
	//information = $.droidmate.ajax.get.getExplorationInfo();
	data = information[history];
	
	
	
	return data;
}

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
	chartAPKStatus = $.plot($("#flot-apks-status"), dataSet, options);
	setTimeout(updateChartAPKStatus, divname, updateInterval);
}

function updateChartAPKStatus(divname)
{
	/*
	if (remainingAPKs != 0)
		{
		if (Math.random() > 0.75)
			{
			successfulAPKs = successfulAPKs + 1;
			remainingAPKs = remainingAPKs - 1;
			}
		else
			{
			failedAPKs = failedAPKs + 1;
			remainingAPKs = remainingAPKs - 1;
			}
		}
	else
		remainingAPKs = 100;*/
	
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

function updateElementsToExplore(divname)
{
	elementsToExplore = getDataElementsToExplore();
    elementsToExplore.slice(-5);
	chart = chartGUIElementsToExplore;

	chart.setData([elementsToExplore]);
	chart.draw();
	setTimeout(updateElementsToExplore, divname, updateInterval);
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