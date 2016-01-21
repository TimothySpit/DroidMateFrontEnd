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
											}
										}
									});
						});

		setTimeout(function() {
			updateExplorationInfo();
		}, $.droidmate.ajax.UPDATE_EXPLORATION_INFO_INTERVAL);
	}
	

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

	

	// init update
	$
			.getJSON(
					"ExplorationData?filesCount",
					function(data) {
						if (data.count) {
							// set up table
							$('#exploreFiles')
									.DataTable(
											);

						}
					});
});
