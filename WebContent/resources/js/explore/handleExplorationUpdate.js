define([ 'jquery', 'DataTables', 'jquery.flot.symbol', 'jquery.flot.canvas', 'jquery.flot.axislabels', 'jquery.flot.navigate', 'jquery.flot', 'jquery.droidmate.ajax', 'jquery.droidmate.explore'], function(require) {

	var finished_apks = [];

	// start updating
	setTimeout(function() {
		updateExplorationInfo();
	}, $.droidmate.ajax.UPDATE_EXPLORATION_INFO_INTERVAL);

	// update exploration info
	function updateExplorationInfo() {
		var status = $.droidmate.ajax.get.getExplorationInfo();

		$
				.each(
						status,
						function(index, value) {
							var name = value.name;
							var elementsSeen = value.elementsSeen;
							var screensSeen = value.screensSeen;
							var success = value.success;
							var finished = value.finished;

							var apkRows = $('#exploreFiles_wrapper .apk-name');
							apkRows
									.each(function(index) {
										// check for name
										if ($(this).text() == name) {
											// found apk
											var elementsScreensSeenDiv = $(
													this.parentElement.parentElement)
													.find(
															'.elements-screens-seen');
											elementsScreensSeenDiv
													.text(elementsSeen + '/'
															+ screensSeen);
											var statusDiv = $(
													this.parentElement.parentElement)
													.find('.state');

											if (finished
													&& $.inArray(name,
															finished_apks) == -1) {
												finished_apks.push(name);
												if (success) {
													statusDiv
															.html('<span class="glyphicon glyphicon-ok-sign" aria-hidden="true"></span>');
												} else {
													statusDiv
															.html('<span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>');
												}
												showReportButton(
														$(this).text(),
														$(this.parentElement.parentElement));
											}
										}
									});
						});

		setTimeout(function() {
			updateExplorationInfo();
		}, $.droidmate.ajax.UPDATE_EXPLORATION_INFO_INTERVAL);
	}
	;

	function showReportButton(apkName, row) {
		$(row).find("button").prop("disabled", false);
		$(row).find("button").wrap(
				'<a href="' + $.droidmate.ajax.get.getReportPath(apkName)
						+ '" target="_blank"></a>');
		$.droidmate.ajax.get.saveReport(apkName);
	}
	

	/* Formatting function for row details - modify as you need */
	function format(d) {
		// `d` is the original data object for the row
		return '<div class="apk-info-min-container">'
				+ '<div class="apk-chart-min apk-chart-min-elements-seen" id="apk-chart-min-elements-seen-'
				+ d[1]
				+ '"></div>'
				+ '<div class="apk-chart-min apk-chart-min-screens-seen" id="apk-chart-min-screens-seen-'
				+ d[1]
				+ '"></div>'
				+ '<div class="apk-chart-min apk-chart-min-elements-explored" id="apk-chart-min-elements-explored-'
				+ d[1] + '"></div>' + '</div>';
	}
	;

	// Add event listener for opening and closing details
	function addSubTableClick() {
		$('#exploreFiles tbody').on('click', 'td.details-control', function() {
			var table = $('#exploreFiles').DataTable();
			var tr = $(this).closest('tr');
			var row = table.row(tr);
			if (row.child.isShown()) {
				// This row is already open - close it
				row.child.hide();
				removeFromWatchDropDownClicked(row.data());
				tr.removeClass('shown');
			} else {
				// Open this row
				row.child(format(row.data())).show();
				watchDropDownClicked(row.data());
				tr.addClass('shown');
			}
		});
	}

	var activeChartDivs = {};

	function updateCharts() {
		$.each(activeChartDivs, function(index, value) {
			if (value != null) {
				var apkData = $.droidmate.ajax.get.getExplorationInfo(index);
				
				var sortFunction = function(a, b) {
					return a[0] - b[0];
					};
				
				var chartElementsSeen = value.chartElementsSeen;
				var data = apkData.history.sort(sortFunction);
				data = data.slice(-10);
				chartElementsSeen.setData([ data ]);
				chartElementsSeen.getAxes().xaxis.options.min = data.length == 0 ? 0 : data[0][0];
				chartElementsSeen.setupGrid();
				chartElementsSeen.draw();

				var chartScreensSeen = value.chartScreensSeen;
				data = apkData.historyScreens.slice(0).sort(sortFunction);
				chartScreensSeen.setData([ data ]);
				chartScreensSeen.getAxes().xaxis.options.min = data.length == 0 ? 0 : data[0][0];
				chartScreensSeen.draw();

				var chartElementsExplored = value.chartElementsExplored;
				data = apkData.historyWidgets.slice(0).sort(sortFunction);
				chartElementsExplored.setData([ data ]);
				chartElementsExplored.getAxes().xaxis.options.min = data.length == 0 ? 0 : data[0][0];
				chartElementsExplored.draw();
			}
		});
		setTimeout(updateCharts,
				$.droidmate.explore.UPDATE_EXPLORE_CHARTS_INTERVAL);
	}
	updateCharts();

	function createChart(divname, axisLabelx, axisLabelY) {
		var options = {
			yaxis : {
				labelWidth : 30,
				axisLabel : axisLabelY,
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 20,
				axisLabelFontFamily : 'Arial',
				minTickSize: 1,
				min : 0,
			},
			xaxis : {
				labelHeight : 30,
				axisLabel : axisLabelx,
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 20,
				axisLabelFontFamily : 'Arial',
				min : 0,
				minTickSize: 1
			}
		};
		var elementsExplored = [ [ 0, 0 ]];
		return $.plot(divname, [ elementsExplored ], options);
	};

	// create each chart when element gets opened
	function watchDropDownClicked(row) {
		var chartDivElementsSeen = $('[id="apk-chart-min-elements-seen-'
				+ row[1] + '"]');
		var chartElementsSeen = createChart(chartDivElementsSeen, 'time (s)',
				'Elements seen');

		var chartDivScreensSeen = $('[id="apk-chart-min-screens-seen-' + row[1]
				+ '"]');
		var chartScreensSeen = createChart(chartDivScreensSeen, 'time (s)',
				'Screens seen');

		var chartDivElementsExplored = $('[id="apk-chart-min-elements-explored-'
				+ row[1] + '"]');
		var chartElementsExplored = createChart(chartDivElementsExplored,
				'time (s)', 'Elements explored');

		activeChartDivs[row[1]] = {
			chartDivElementsSeen : chartDivElementsSeen,
			chartDivScreensSeen : chartDivScreensSeen,
			chartDivElementsExplored : chartDivElementsExplored,
			chartElementsSeen : chartElementsSeen,
			chartScreensSeen : chartScreensSeen,
			chartElementsExplored : chartElementsExplored
		};
		updateCharts();
	}

	function removeFromWatchDropDownClicked(row) {
		activeChartDivs[row[1]] = null;
	}

	// init update
	$
			.getJSON(
					"ExplorationData?filesCount",
					function(data) {
						if (data.count) {
							// set up table
							$('#exploreFiles')
									.DataTable(
											{
												"ajax" : {
													'url' : '/DroidMate/ExplorationData?apkTableData',
													'dataSrc' : function(json) {
														$(".apk-data")
																.removeClass(
																		"hide");
														addSubTableClick();
														return json.data;
													}
												},
												'searching' : false,
												'paging' : false,
												"columnDefs" : [
														{
															"targets" : 0,
															"className" : 'details-control',
															"orderable" : false,
															"data" : null,
															"defaultContent" : ''
														},
														{
															"targets" : 1,
															"searchable" : false,
															"render" : function(
																	data, type,
																	row) {
																return '<span class="apk-name">'
																		+ data
																		+ '</span>'
																		+ '<button '
																		+ 'class="btn btn-default pull-right" type="button" disabled>'
																		+ 'Show report'
																		+ '</button>'
															}
														},
														{
															"targets" : 2,
															"searchable" : false,
															"render" : function(
																	data, type,
																	row) {
																return '<div class="elements-screens-seen">0</div>';
															}
														},
														{
															"targets" : 3,
															"searchable" : false,
															"render" : function(
																	data, type,
																	row) {
																return '<div class="state">'
																		+ '<span class="glyphicon glyphicon glyphicon-asterisk" aria-hidden="true"></span>'
																		+ '</div>'
															}
														} ]
											});

						}
					});
});
