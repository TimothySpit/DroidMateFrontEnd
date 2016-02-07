define([ 'require',
         'jquery.flot', 'jquery.flot.axislabels',
			'jquery.flot.canvas', 'jquery.flot.navigate',
			'jquery.flot.symbol', 'jquery.flot.pie', 
			'jquery.droidmate.ajax', 'jquery.droidmate.dialogs',
		'jquery.droidmate.overlays','../explorationCharts/handleUpdate' ], function(require,Spinner) {

	//get ui updater
	var updateHelper = require('../explorationCharts/handleUpdate');
	
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
									+ Math.round(series.percent)
									+ '%</div>';
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
	
	function initCharts() {
		var apkStatusChart = createPieChart('#flot-apks-status');
		var elementsSeenChart = createGraphChart('#flot-gui-elements-seen',
				'time (s),', 'Elements seen');
		var screensExploredChart = createGraphChart(
				"#flot-gui-screens-explored", "time (s)",
				'Screens explored');
		var elementsExploredChart = createGraphChart(
				"#flot-gui-elements-explored", "time (s)",
				'Elements explored');

		return {
			apkStatusChart : apkStatusChart,
			elementsSeenChart : elementsSeenChart,
			screensExploredChart : screensExploredChart,
			elementsExploredChart : elementsExploredChart
		};
	}
	
	function getAPKStatusInformation() {
		var successfulAPKs = 0;
		var failedAPKs = 0;
		var remainingAPKs = 0;
		$.droidmate.ajax.getAPKSData(false,function(data) {
			if(!data || !data.getAPKSData) {
				//server response error
				return;
			}
			
			if(!data.getAPKSData.result) {
				//could not get apk data
				return;
			}
			
			if(!data.getAPKSData.payload || !data.getAPKSData.payload.data) {
				//intern error, apk data could not be send
				return;
			}
			
			var apks = data.getAPKSData.payload.data;
			
			//get selected APks
			selectedAPKS = $.map(apks,function(value,index) {
				if(value.isSelected) {
					return value;
				}
			});
			
			if(!selectedAPKS.length) {
				//no apks selected
				return;
			}
			
			for (var i = 0; i < selectedAPKS.length; i++) {
				apk = selectedAPKS[i];

				if (apk.explorationStatus === "SUCCESS") {
					successfulAPKs++;
				}
				else if(apk.explorationStatus === "ABORTED" || apk.explorationStatus === "ERROR") {
					failedAPKs++;
				} else {
					remainingAPKs++;
				}
			}
		});
		
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
		choiceContainer.find("input:checked").each(function() {
			if ($(this).attr("id") == "cb-total")
				getTotal = true;
		});

		if (getTotal) // check if the "total" checkbox is checked
		{
			$.droidmate.ajax.getGlobalExploration(false, function(result) {
				var data = result.getGlobalExploration.payload.data;
				elementsSeenHistory = data.historyElements;
				widgetsExploredHistory = data.historyWidgets;
				screensSeenHistory = data.historyScreens;
			});
		}
		var apkStatus = getAPKStatusInformation();

		var individualData = getIndividualData(choiceContainer);
		elementsSeenHistory = [ elementsSeenHistory ]
				.concat([ individualData.elementsSeenIndividual ]);
		widgetsExploredHistory = [ widgetsExploredHistory ]
				.concat(individualData.widgetsExploredIndividual);
		screensSeenHistory = [ screensSeenHistory ]
				.concat(individualData.screensSeenIndividual);

		return {
			elementsSeenHistory : elementsSeenHistory,
			widgetsExploredHistory : widgetsExploredHistory,
			screensSeenHistory : screensSeenHistory,
			apkStatus : apkStatus
		};
	}
	
	function getIndividualData(choiceContainer) {
		var elementsSeenIndividual = [];
		var widgetsExploredIndividual = [];
		var screensSeenIndividual = [];
		$.droidmate.ajax.getAPKSData(false,function(data) {
			if(!data || !data.getAPKSData) {
				//server response error
				return;
			}
			
			if(!data.getAPKSData.result) {
				//could not get apk data
				return;
			}
			
			if(!data.getAPKSData.payload || !data.getAPKSData.payload.data) {
				//intern error, apk data could not be send
				return;
			}
			
			var apks = data.getAPKSData.payload.data;
			
			//get selected APks
			selectedAPKS = $.map(apks,function(value,index) {
				if(value.isSelected) {
					return value;
				}
			});
			
			if(!selectedAPKS.length) {
				//no apks selected
				return;
			}
			
			selectedAPKS.sort(function(a, b) {
				return a.name.toUpperCase().localeCompare(
						b.name.toUpperCase())
			});
			
			var checkBoxes = choiceContainer.find("input");
			for (var i = 0; i < selectedAPKS.length; i++) {
				var apk = selectedAPKS[i];
				var explorationInfo = apk.explorationInfo;
				
				var cb = checkBoxes.filter('[id="cb-' + apk.name
						+ '"]');
				if ($(cb).prop('checked')) {
					elementsSeenIndividual.push(explorationInfo.historyElements);
					widgetsExploredIndividual.push(explorationInfo.historyWidgets);
					screensSeenIndividual.push(explorationInfo.historyScreens);
				} else {
					elementsSeenIndividual.push([ [ 0, 0 ] ]);
					widgetsExploredIndividual.push([ [ 0, 0 ] ]);
					screensSeenIndividual.push([ [ 0, 0 ] ]);
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
		updateHelper.updateUI(false, function(data) {
			if(!data || !data.getUserStatus || !data.getUserStatus.result) {
				return;
			}
			
			var status = data.getUserStatus;
			
			//nothing to show
			if(status == "IDLE") {
				return;
			} 
			
			var data = getData(choiceContainer);
			
			// update elements seen
			var chart = charts.elementsSeenChart;

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
			
			if(repeat) {
				setTimeout(function() {updateCharts(charts, choiceContainer, true);
				}, $.droidmate.ajax.UPDATE_EXPLORATION_INFO_INTERVAL);
			}
		});
	}

	
	//get all selected apks
	$.droidmate.ajax.getAPKSData(true,function(data) {
		if(!data || !data.getAPKSData) {
			//server response error
			return;
		}
		
		if(!data.getAPKSData.result) {
			//could not get apk data
			return;
		}
		
		if(!data.getAPKSData.payload || !data.getAPKSData.payload.data) {
			//intern error, apk data could not be send
			return;
		}
		
		var apks = data.getAPKSData.payload.data;
		
		//get selected APks
		selectedAPKS = $.map(apks,function(value,index) {
			if(value.isSelected) {
				return value;
			}
		});
		
		if(!selectedAPKS.length) {
			//no apks selected
			return;
		}
		
		var charts = initCharts();
		
		// insert checkboxes
		var checkboxes;
		var choiceContainer = $("#apks-charts-legend");
		choiceContainer
				.append('<label><input type="checkbox" name="Show all" checked="checked" id="cb-total">Show all</label>');
		
		selectedAPKS.sort(function(a, b) {
			return a.name.toUpperCase().localeCompare(b.name.toUpperCase())
		});

		for (var i = 0; i < selectedAPKS.length; i++) {
			var label = selectedAPKS[i].name;
			choiceContainer.append('<label><input type="checkbox" name="'
					+ label + '" checked="checked" id="cb-' + label + '">'
					+ label + '</label>');
		}
		choiceContainer.find("input").click(function() {
			updateCharts(charts, choiceContainer, false);
		}); // update on click
		
		// update once for empty data
		updateCharts(charts, choiceContainer, false);
		// set checkbox colors
		$.each(charts, function(index, value) {
			var plot = value;
			var series = plot.getData();
			choiceContainer.find('[id="cb-total"]').parent().css(
					'background-color', series[0].color);
			for (var i = 1; i < series.length; ++i) { // no total checkbox
				var apk = apks[i - 1];
				if (apk)
					choiceContainer.find('[id="cb-' + apk.name + '"]').parent()
							.css('background-color', series[i].color);
			}
		});

		// start updating charts
		updateCharts(charts, choiceContainer, true);
		
	});
});