define(
		[ 'require', 'jquery', 'bootbox', 'jstree',
				'../explore/apkExplorationTable', 'jquery.flot',
				'jquery.flot.tooltip', 'jquery.droidmate.ajax',
				'jquery.droidmate.inlining', 'DataTables' ],
		function(require, jquery, bootbox) {

			var tableCreator = require('../explore/apkExplorationTable');

			var table = tableCreator.initModul($('#exploreFiles'));

			// fill table
			var result = null;
			$.ajax({
				url : '/DroidMate/ExplorationData?apkTableData',
				async : false,
				type : 'GET',
				dataType : "json",
				success : function(data) {
					result = data;
				}
			});

			// set initial data
			$.each(result.data, function(index, value) {
				var status = null;
				table.addAPKData(value[1], 0, 0, 0,
						status = table.apkStatus.NOT_STARTED);
			});
			table.redraw();

			// register events
			table.on('row:open', function(row) {
				updateCharts();
			});

			// start updating
			function updateCharts(repeat) {
				var status = $.droidmate.ajax.get.getExplorationInfo();
				$.each(status,
						function(index, value) {
							var name = value.name;
							var elementsSeen = value.elementsSeen;
							var screensSeen = value.screensSeen;
							var widgetsExplored = value.widgetsExplored;
							var success = value.success;
							var finished = value.finished;

							// get current row
							var currentRow = table.getRowByName(name);

							// update texts
							currentRow.updateElementsSeen(elementsSeen);
							currentRow.updateScreensSeen(screensSeen);
							currentRow.updateWidgetsClicked(widgetsExplored);
							var status = null;
							if (finished) {
								if (success)
									status = table.apkStatus.SUCCESS;
								else
									status = table.apkStatus.ERROR;
							} else {
								status = table.apkStatus.NOT_STARTED;
							}
							currentRow.updateStatus(status);

							// update charts
							var apkData = $.droidmate.ajax.get
									.getExplorationInfo(name);

							var sortFunction = function(a, b) {
								return a[0] - b[0];
							};
							var data = apkData.history.slice(0).sort(
									sortFunction);
							currentRow.setElementsSeenChartData(data);

							data = apkData.historyScreens.slice(0).sort(
									sortFunction);
							currentRow.setScreensSeenChartData(data);
							data = apkData.historyWidgets.slice(0).sort(
									sortFunction);
							currentRow.setWidgetsExploredChartData(data);

						});

				var status = $.droidmate.ajax.get.getExplorationStatus();

				if (status != "STARTED") {
					handleEndScreen(status);
					return;
				}

				if (repeat) {
					setTimeout(function() {
						updateCharts();
					}, $.droidmate.ajax.UPDATE_EXPLORATION_INFO_INTERVAL);
				}
			}

			updateCharts();

			function handleEndScreen(status) {
				$.droidmate.ajax.post.saveReport();
				var message = "";
				if (status == "FINISHED") {
					message = "DroidMate finished successful. Reports got saved."
				} else {
					message = "DroidMate crashed. Reports got saved."
				}

				bootbox.confirm(message, function() {
				});
			}

		});