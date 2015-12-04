function createChartGUIElementsToExplore(divname) {
    var d1 = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
    var options =  {
            yaxis: {
                labelWidth: 30,
                axisLabel: 'GUI Elements not yet seen',
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
    console.log(divname);
    $.plot($(divname), [d1], options);
};

function createChartGUIScreensExplored(divname) {
    var d1 = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
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
    
    $.plot($(divname), [d1], options);
};

function createChartGUIElementsExplored(divname) {
    var d1 = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
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
    
    $.plot($(divname), [d1], options);
};