define(
		[ 'jquery', 'jquery.flot', 'jquery.flot.axislabels',
				'jquery.flot.canvas', 'jquery.flot.navigate',
				'jquery.flot.symbol', 'jquery.flot.pie',
				'jquery.droidmate.ajax' ],
		function(require) {

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
				choiceContainer.find("input:checked").each(function() {
					if ($(this).attr("id") == "cb-total")
						getTotal = true;
				});

				if (getTotal) // check if the "total" checkbox is checked
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
				var apkArray = $.droidmate.ajax.get.getExplorationInfo();
				apkArray.sort(function(a, b) {
					return a.name.toUpperCase().localeCompare(
							b.name.toUpperCase())
				});

				var elementsSeenIndividual = [];
				var widgetsExploredIndividual = [];
				var screensSeenIndividual = [];

				var checkBoxes = choiceContainer.find("input");
				for (var i = 0; i < apkArray.length; i++) {
					var cb = checkBoxes.filter('[id="cb-' + apkArray[i].name
							+ '"]');
					if ($(cb).prop('checked')) {
						elementsSeenIndividual.push(apkArray[i].history);
						widgetsExploredIndividual
								.push(apkArray[i].historyWidgets);
						screensSeenIndividual.push(apkArray[i].historyScreens);
					} else {
						elementsSeenIndividual.push([ [ 0, 0 ] ]);
						widgetsExploredIndividual.push([ [ 0, 0 ] ]);
						screensSeenIndividual.push([ [ 0, 0 ] ]);
					}
				}

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

				if (repeat) {
					setTimeout(function() {
						updateCharts(charts, choiceContainer, true);
					}, $.droidmate.ajax.UPDATE_EXPLORATION_INFO_INTERVAL);
				}

			}

			// create all charts
			var apkStatusChart = createPieChart('#flot-apks-status');
			var elementsSeenChart = createGraphChart('#flot-gui-elements-seen',
					'time (s),', 'Elements seen');
			var screensExploredChart = createGraphChart(
					"#flot-gui-screens-explored", "time (s)",
					'Screens explored');
			var elementsExploredChart = createGraphChart(
					"#flot-gui-elements-explored", "time (s)",
					'Elements explored');

			var charts = {
				apkStatusChart : apkStatusChart,
				elementsSeenChart : elementsSeenChart,
				screensExploredChart : screensExploredChart,
				elementsExploredChart : elementsExploredChart
			};

			// insert checkboxes
			var checkboxes;
			var choiceContainer = $("#apks-charts-legend");
			choiceContainer
					.append('<label><input type="checkbox" name="Show all" checked="checked" id="cb-total">Show all</label>'); // draw
			// "total",
			// or
			// not?
			var apks = $.droidmate.ajax.get.getSelectedAPKS()["info[]"].selApks.data;
			apks.sort(function(a, b) {
				return a.name.toUpperCase().localeCompare(b.name.toUpperCase())
			});

			for (var i = 0; i < apks.length; i++) {
				var label = apks[i].name;
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
					var name = apks[i - 1].name;
					if (name)
						choiceContainer.find('[id="cb-' + name + '"]').parent()
								.css('background-color', series[i].color);
				}
			});

			// start updating charts
			updateCharts(charts, choiceContainer, true);

		});
