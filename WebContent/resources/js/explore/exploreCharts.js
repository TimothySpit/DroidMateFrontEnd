$(document).ready()
	{
	var elementsNotExplored = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
	var elementsExplored = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
	var screensExplored = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
	var updateInterval = 1000;
	var lastUpdate = 0;
	var chartGUIElementsExplored, chartGUIElementsToExplore, chartGUIScreensExplored;
	//var apksExplored, apksYetToExplore, apksFailed
	}


function createChartGUIElementsToExplore(divname) {
    var d1 = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
    var options =  {
            yaxis: {
                labelWidth: 30,
                axisLabel: 'Seen, but unexplored, GUI elements',
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
    console.log("GUIElements yet to explore " + divname);
    $.plot(divname, [d1], options);
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
    console.log("GUIScreens explored " + divname);
    $.plot(divname, [elementsExplored], options);
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
    console.log("GUIElements " + divname);
    chartGUIElementsExplored = $.plot(divname, [[0, 0]], options);
    updateElementsExplored(divname);
};

function addRandomValue(data)
{
	data.shift();
	var y = Math.random(400) * 400;
	lastUpdate += updateInterval / 1000;
	var temp = [lastUpdate, y]; 
	data.push(temp);
}

function updateElementsExplored(divname)
{
	addRandomValue(elementsExplored);
	chartGUIElementsExplored.setData([elementsExplored]);
	chartGUIElementsExplored.draw();
	setTimeout(updateElementsExplored, divname, updateInterval);
	
}