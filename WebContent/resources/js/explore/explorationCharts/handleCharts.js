define([ 'jquery', 'jquery.flot', 'jquery.flot.axislabels', 'jquery.flot.canvas', 'jquery.flot.navigate', 'jquery.flot.symbol','jquery.flot.pie', 'jquery.droidmate.ajax'], function(require) {

	function createPieChart(divname) {
		var dataSet = [ {
			label : "Successful",
			data : 0,
			color : "#00A36A"
		}, {
			label : "Failed",
			data : 0,
			color : "#005CDE"
		}, {
			label : "Remaining",
			data : 1,
			color : "#7D0096"
		} ];
		var options = {
			series : {
				pie : {
					show : true,
					radius : 1,
					label : {
						show : true,
						radius : 1,
						formatter : function(label, series) {
							return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
									+ label
									+ '<br/>'
									+ Math.round(series.percent) + '%</div>';
						},
						background : {
							opacity : 0.8
						}
					}
				}
			},
			legend : {
				show : false
			}
		};
		return $.plot($(divname), dataSet, options);
	}

	function createGraphChart(divname, xAxisLabel, yAxisLabel) {
		var options = {
			yaxis : {
				labelWidth : 30,
				axisLabel : yAxisLabel,
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 20,
				axisLabelFontFamily : 'Arial',
				panRange : false,
				minTickSize : 1,
				min : 0,
			},
			xaxis : {
				labelHeight : 30,
				axisLabel : xAxisLabel,
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 15,
				axisLabelFontFamily : 'Arial',
				panRange : [ 0, null ],
				min : 0,
				minTickSize : 1
			},
			pan : {
				interactive : true
			}
		};
		return $.plot(divname, [ 0, 0 ], options);
	}

	function getAPKStatusInformation() {
		var apkArray = $.droidmate.ajax.get.getExplorationInfo();
		var selAPKSSize = $.droidmate.ajax.get.getSelectedAPKS()["info[]"].selApks.data.length;

		var successfulAPKs = 0;
		var failedAPKs = 0;
		for (var i = 0; i < apkArray.length; i++) {
			apk = apkArray[i];

			if (apk.finished) {
				if (apk.success)
					successfulAPKs++;
				else
					failedAPKs++;
			}
		}
		var remainingAPKs = selAPKSSize - successfulAPKs - failedAPKs;
		return {
			successful : successfulAPKs,
			failed : failedAPKs,
			remaining : remainingAPKs
		};
	}

	function getData(choiceContainer) {
		var elementsSeenHistory = [];
		var widgetsExploredHistory = [];
		var screensSeenHistory = [];
		var getTotal = false;
		choiceContainer.find("input:checked").each(function () {
			if ($(this).attr("id") == "id0")
				getTotal = true;
		});
		
		if (getTotal) //check if the "total" checkbox is checked
			{
			elementsSeenHistory = $.droidmate.ajax.get
				.getGlobalElementsSeenHistory();
			widgetsExploredHistory = $.droidmate.ajax.get
				.getGlobalWidgetsExploredHistory();
			screensSeenHistory = $.droidmate.ajax.get
				.getGlobalScreensSeenHistory();
			}
		var apkStatus = getAPKStatusInformation();
		
		var individualData = getIndividualData(choiceContainer);
		elementsSeenHistory = individualData.elementsSeenIndividual.concat([elementsSeenHistory]);
		widgetsExploredHistory = individualData.widgetsExploredIndividual.concat([widgetsExploredHistory]);
		screensSeenHistory = individualData.screensSeenIndividual.concat([screensSeenHistory]);
		
		return {
			elementsSeenHistory : elementsSeenHistory,
			widgetsExploredHistory : widgetsExploredHistory,
			screensSeenHistory : screensSeenHistory,
			apkStatus : apkStatus
		};
	}
	
	function getIndividualData(choiceContainer) {
		var apkArray = $.droidmate.ajax.get.getExplorationInfo();
		var elementsSeenIndividual = [];
		var widgetsExploredIndividual = [];
		var screensSeenIndividual = [];
		
		
		choiceContainer.find("input").each(function () {
			if ($(this).attr("id") != "id0") //individual data does not care about the "total" checkbox
				{
				var key = $(this).attr("name");
				
				for (var i = 0; i < apkArray.length; i++)
					{
					apk = apkArray[i];
					if (apkArray[i].name == key) //this checkbox refers to this apk
						{
						if($(this).prop('checked')) {
							elementsSeenIndividual.push(apk.history);
							widgetsExploredIndividual.push(apk.historyWidgets);
							screensSeenIndividual.push(apk.historyScreens);
						} else {
							elementsSeenIndividual.push([]);
							widgetsExploredIndividual.push([]);
							screensSeenIndividual.push([]);
						}
						
						}
					}
				}
		});

		return {
			elementsSeenIndividual : elementsSeenIndividual,
			widgetsExploredIndividual : widgetsExploredIndividual,
			screensSeenIndividual : screensSeenIndividual,
		};
	}

	function updateCharts(charts, choiceContainer, repeat) {

		var data = getData(choiceContainer);
		
		// update elements seen
		var chart = charts.elementsSeenChart;
		
		//var elementsSeenAll = individualData.elementsSeenIndividual.push(data.elementsSeenHistory);
		//var elementsSeenAll = individualData.elementsSeenIndividual.concat([data.elementsSeenHistory]); //add total line to individual lines
		//console.log(elementsSeenAll);
		//chart.setData(elementsSeenAll);
		
		chart.setData(data.elementsSeenHistory);
		chart.draw();
		

		// update screens explored
		chart = charts.screensExploredChart;
		chart.setData(data.screensSeenHistory);
		chart.draw();

		// Elements explored
		chart = charts.elementsExploredChart;
		chart.setData(data.widgetsExploredHistory);
		chart.draw();

		// apk status
		chart = charts.apkStatusChart;
		var dataSet = [ {
			label : "Successful",
			data : data.apkStatus.successful,
			color : "#00A36A"
		}, {
			label : "Failed",
			data : data.apkStatus.failed,
			color : "#005CDE"
		}, {
			label : "Remaining",
			data : data.apkStatus.remaining,
			color : "#7D0096"
		} ];
		chart.setData(dataSet);
		chart.draw();

		if (repeat)
			{
			setTimeout(function() {updateCharts(charts, choiceContainer, true);},
					$.droidmate.ajax.UPDATE_EXPLORATION_INFO_INTERVAL);
			}
		
	}

	// create all charts
	var apkStatusChart = createPieChart('#flot-apks-status');
	var elementsSeenChart = createGraphChart('#flot-gui-elements-seen',
			'time (s),', 'Elements seen');
	var screensExploredChart = createGraphChart("#flot-gui-screens-explored",
			"time (s)", 'Screens explored');
	var elementsExploredChart = createGraphChart("#flot-gui-elements-explored",
			"time (s)", 'Elements explored');
	
	var charts = {
			apkStatusChart : apkStatusChart,
			elementsSeenChart : elementsSeenChart,
			screensExploredChart : screensExploredChart,
			elementsExploredChart : elementsExploredChart
		};
	// insert checkboxes 
	var checkboxes;
    var choiceContainer = $("#apks-charts-legend");
    var key = 0;
    var label = "total";
    choiceContainer.append('<label><input type="checkbox" name="' + label +
            '" checked="checked" id="id' + key + '">' 
             + label + '</label>'); //draw "total", or not?
    var apks = $.droidmate.ajax.get.getSelectedAPKS()["info[]"].selApks.data;
    for (var i = 0; i < apks.length; i++)
    {
    	var key = i+1;
    	var label = apks[i].name;
        choiceContainer.append('<label><input type="checkbox" name="' + label +
                               '" checked="checked" id="id' + key + '">'
                                + label + '</label>');
    }
    choiceContainer.find("input").click(function() {updateCharts(charts, choiceContainer, false);}); //update on click
    
	// start updating charts
	updateCharts(charts, choiceContainer, true);
	
});
