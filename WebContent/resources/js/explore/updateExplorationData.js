$(function() {

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
	;

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

				var chart = value.chartElementsSeen;
				var data = apkData.history.slice(0).sort(function(a, b) {
					return a[0] - b[0];
				});
				data = data.slice(-10);
				chart.setData([ data ]);
				chart.draw();

				var chart2 = value.chartScreensSeen;
				var data2 = apkData.historyScreens.slice(0).sort(
						function(a, b) {
							return a[0] - b[0];
						});
				chart2.setData([ data2 ]);
				chart2.draw();

				var chart3 = value.chartElementsExplored;
				var data3 = apkData.historyWidgets.slice(0).sort(
						function(a, b) {
							return a[0] - b[0];
						});
				chart3.setData([ data3 ]);
				chart3.draw();
			}
		});
		setTimeout(updateCharts,
				$.droidmate.explore.UPDATE_EXPLORE_CHARTS_INTERVAL);
	}
	updateCharts();

	function createChartElementsSeen(divname) {
		var options = {
			yaxis : {
				labelWidth : 30,
				axisLabel : 'Elements seen',
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 20,
				axisLabelFontFamily : 'Arial'
			},
			xaxis : {
				labelHeight : 30,
				axisLabel : 'time (s)',
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 15,
				axisLabelFontFamily : 'Arial'
			}
		};
		var elementsExplored = [ [ 0, 0 ], [ 0, 0 ], [ 0, 0 ], [ 0, 0 ],
				[ 0, 0 ] ];
		return $.plot(divname, [ elementsExplored ], options);
	}
	;

	function createChartGUIScreensSeen(divname) {
		var options = {
			yaxis : {
				labelWidth : 30,
				axisLabel : 'Screens seen',
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 20,
				axisLabelFontFamily : 'Arial'
			},
			xaxis : {
				labelHeight : 30,
				axisLabel : 'time (s)',
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 15,
				axisLabelFontFamily : 'Arial'
			}
		};
		var elementsExplored = [ [ 0, 0 ], [ 0, 0 ], [ 0, 0 ], [ 0, 0 ],
				[ 0, 0 ] ];
		return $.plot(divname, [ elementsExplored ], options);
	}
	;

	function createChartElementsExplored(divname) {
		var options = {
			yaxis : {
				labelWidth : 30,
				axisLabel : 'Elements explored',
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 20,
				axisLabelFontFamily : 'Arial'
			},
			xaxis : {
				labelHeight : 30,
				axisLabel : 'time (s)',
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 15,
				axisLabelFontFamily : 'Arial'
			}
		};
		var elementsExplored = [ [ 0, 0 ], [ 0, 0 ], [ 0, 0 ], [ 0, 0 ],
				[ 0, 0 ] ];
		return $.plot(divname, [ elementsExplored ], options);
	}
	;
	
	// create each chart when element gets opened
	function watchDropDownClicked(row) {
		var chartDivElementsSeen = $('[id="apk-chart-min-elements-seen-'
				+ row[1] + '"]');
		var chartElementsSeen = createChartElementsSeen(chartDivElementsSeen);

		var chartDivScreensSeen = $('[id="apk-chart-min-screens-seen-' + row[1]
				+ '"]');
		var chartScreensSeen = createChartGUIScreensSeen(chartDivScreensSeen);

		var chartDivElementsExplored = $('[id="apk-chart-min-elements-explored-'
				+ row[1] + '"]');
		var chartElementsExplored = createChartElementsExplored(chartDivElementsExplored);

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
