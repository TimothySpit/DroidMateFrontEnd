$(document).ready()
	{
	var elementsToExplore = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
	var elementsExplored = [[0,0],[0,0],[0,0],[0,0],[0,0]];
	var screensExplored = [[1, 500], [2, 500], [3, 500], [4, 500], [5, 300]];
	var updateInterval = 1000;
	var lastUpdate = 0;
	var chartGUIElementsExplored, chartGUIElementsToExplore, chartGUIScreensExplored;
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
    chartGUIElementsToExplore = $.plot(divname, [elementsToExplore], options);
    setTimeout(updateElementsToExplore, divname, updateInterval);
};

function createChartGUIScreensExplored(divname) {
    var options =  {
            yaxis: {
                labelWidth: 30,
                axisLabel: 'GUI screens explored',
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
    chartGUIScreensExplored = $.plot(divname, [screensExplored], options);
    setTimeout(updateScreensExplored, divname, updateInterval);
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

function addRandomValue(currentData)
{
	currentData.shift();
	var y = Math.random(400) * 400;
	lastUpdate += updateInterval / 1000;
	var temp = [lastUpdate, y]; 
	currentData.push(temp);
}

function updateElementsExplored(divname)
{
	chart = chartGUIElementsExplored;
	data = elementsExplored;
	addRandomValue(data); 
	console.log(data);
	chart.setData([data]);
	chart.draw();
	
	setTimeout(updateElementsExplored, divname, updateInterval);
}

function updateElementsToExplore(divname)
{
	chart = chartGUIElementsToExplore;
	data = elementsToExplore;
	addRandomValue(data); 
	
	chart.setData([data]);
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