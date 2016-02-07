define(
		[ 'require', 'jquery', 'DataTables', 'jquery.flot.symbol',
				'jquery.flot.canvas', 'jquery.flot.axislabels',
				'jquery.flot.navigate', 'jquery.flot', 'jquery.droidmate.ajax',
				'jquery.droidmate.explore' ],
		function(require) {

			// private members
			var activeChartDivs = {};
			var tableID = null;

			var tableUpdateID = 0;
			// -----------------------

			// chart options
			var graphChartOptions = {
				yaxis : {
					labelWidth : 30,
					axisLabelUseCanvas : true,
					axisLabelFontSizePixels : 20,
					axisLabelFontFamily : 'Arial',
					minTickSize : 1,
					min : 0,
				},
				xaxis : {
					labelHeight : 30,
					axisLabelUseCanvas : true,
					axisLabelFontSizePixels : 20,
					axisLabelFontFamily : 'Arial',
					min : 0,
					minTickSize : 1
				}
			};

			// datatables options
			var datatableOptions = {
				columns : [ {
					title : ''
				}, {
					title : "Name"
				},{
					title : "Time"
				},{
					title : "Elements seen / Screens seen / Widgets clicked"
				},
				{
					title : "Status"
				}],
				'searching' : false,
				'paging' : false,
				bAutoWidth:false,
				'order' : [ [ 1, 'asc' ] ],
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
							"width" : "65%",
							"searchable" : false,
							"render" : function(data, type, row) {
								return '<span class="apk-name">' + data
										+ '</span>';
							}
						},
						{
							"targets" : 2,
							"searchable" : false,
							"orderable" : false,
						},
						{
							"targets" : 3,
							"width" : "25%",
							'orderable' : false,
							"searchable" : false,
							"render" : function(data, type, row) {
								return '<span class="elements-seen">'
										+ data.elementsSeen + '</span>/'
										+ '<span class="screens-seen">'
										+ data.screensSeen + '</span>/'
										+ '<span class="widgets-cllicked">'
										+ data.widgetsClicked + '</span>';
							}
						},
						{
							"targets" : 4,
							"width" : "10%",
							'orderable' : false,
							"searchable" : false,
							"render" : function(data, type, row) {
								return '<div class="state">'
										+ '<span class="status-label" aria-hidden="true"></span>'
										+ '</div>'
							}
						} ]
			};

			var row = function(rowNode, modul) {
				var span = rowNode.node();
				var labelContainer = $(span).find('.status-label');

				function dataMinElementIndex(arr) {
					var len = arr.length, min = Infinity, index = -1;
					while (len--) {
						if (arr[len][0] < min) {
							min = arr[len][0];
							index = len;
						}
					}
					return index;
				}

				return {
					getName : function() {
						return rowNode.data()[1];
					},
					getElementsSeen : function() {
						return rowNode.data()[3].elementsSeen;
					},
					getScreensSeen : function() {
						return rowNode.data()[3].screensSeen;
					},
					getWidgetsClicked : function() {
						return rowNode.data()[3].widgetsClicked;
					},
					getStatus : function() {
						if (labelContainer.hasClass('glyphicon-success')) {
							return modul.apkStatus.SUCCESS;
						}
						if (labelContainer.hasClass('glyphicon-time')) {
							return modul.apkStatus.NOT_RUNNING;
						}
						if (labelContainer.hasClass('glyphicon-remove')) {
							return modul.apkStatus.ERROR;
						}
						if (labelContainer.hasClass('glyphicon-play')) {
							return modul.apkStatus.EXPLORING;
						}
					},
					setElementsSeenChartData : function(data) {
						if (!activeChartDivs[rowNode.data()[1]])
							return null;

						var chart = activeChartDivs[rowNode.data()[1]].chartElementsSeen;
						chart.setData([ data ]);
						chart.getAxes().xaxis.options.min = data.length == 0 ? 0
								: dataMinElementIndex(data);
						chart.draw();
					},
					setScreensSeenChartData : function(data) {
						if (!activeChartDivs[rowNode.data()[1]])
							return null;

						var chart = activeChartDivs[rowNode.data()[1]].chartScreensSeen;
						chart.setData([ data ]);
						chart.getAxes().xaxis.options.min = data.length == 0 ? 0
								: dataMinElementIndex(data);
						chart.draw();
					},
					setWidgetsExploredChartData : function(data) {
						if (!activeChartDivs[rowNode.data()[1]])
							return null;

						var chart = activeChartDivs[rowNode.data()[1]].chartElementsExplored;
						chart.setData([ data ]);
						chart.getAxes().xaxis.options.min = data.length == 0 ? 0
								: dataMinElementIndex(data);
						chart.draw();
					},
					updateName : function(name) {
						$(rowNode.node()).find('.apk-name').text(name);
					},
					updateElementsSeen : function(elementsSeen) {
					$(rowNode.node()).find('.elements-seen').text(elementsSeen);
					},
					updateScreensSeen : function(screensSeen) {
						$(rowNode.node()).find('.screens-seen').text(screensSeen);
					},
					updateWidgetsClicked : function(widgetsClicked) {
						$(rowNode.node()).find('.widgets-cllicked').text(widgetsClicked);
					},
					updateStatus : function(status) {
						labelContainer.removeClass(function(index, css) {
							return (css.match(/(^|\s)glyphicon-\S+/g) || [])
									.join(' ');
						});

						if (status === modul.apkStatus.SUCCESS) {
							labelContainer.addClass('glyphicon-ok');
						} else if (status === modul.apkStatus.NOT_RUNNING) {
							labelContainer.addClass('glyphicon-time');
						} else if (status === modul.apkStatus.ERROR) {
							labelContainer.addClass('glyphicon-remove');
						} else if (status === modul.apkStatus.EXPLORING) {
							labelContainer.addClass('glyphicon-play');
						}
					},
				}
			}

			// format function for table charts submenu
			function format(d) {
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

			// ---------------
			function removeStadardEvents(table) {
				tableID.find('tbody').off('click');
			}

			function removeFromWatchDropDownClicked(row) {
				activeChartDivs[row.data()[1]] = null;
			}
			// create each chart when element gets opened
			function watchDropDownClicked(row) {
				var rowData = row.data();
				var chartDivElementsSeen = $('[id="apk-chart-min-elements-seen-'
						+ rowData[1] + '"]');
				var chartElementsSeen = createChart(chartDivElementsSeen,
						'time (s)', 'Elements seen');

				var chartDivScreensSeen = $('[id="apk-chart-min-screens-seen-'
						+ rowData[1] + '"]');
				var chartScreensSeen = createChart(chartDivScreensSeen,
						'time (s)', 'Screens seen');

				var chartDivElementsExplored = $('[id="apk-chart-min-elements-explored-'
						+ rowData[1] + '"]');
				var chartElementsExplored = createChart(
						chartDivElementsExplored, 'time (s)',
						'Elements explored');

				var chart = {
					chartDivElementsSeen : chartDivElementsSeen,
					chartDivScreensSeen : chartDivScreensSeen,
					chartDivElementsExplored : chartDivElementsExplored,
					chartElementsSeen : chartElementsSeen,
					chartScreensSeen : chartScreensSeen,
					chartElementsExplored : chartElementsExplored
				};
				activeChartDivs[rowData[1]] = chart;

				return chart;
			}

			function createChart(divname, axisLabelx, axisLabelY) {

				var elementsExplored = [ [ 0, 0 ] ];
				graphChartOptions.xaxis.axisLabel = axisLabelx;
				graphChartOptions.yaxis.axisLabel = axisLabelY;
				return $.plot(divname, [ elementsExplored ], graphChartOptions);
			}

			function addStandardEvents(table) {
				tableID.find('tbody').on('click', 'td.details-control',
						function() {
							var table = tableID.DataTable();
							var tr = $(this).closest('tr');
							var tableRow = table.row(tr);
							if (tableRow.child.isShown()) {
								// This row is already open - close it
								tableRow.child.hide();
								removeFromWatchDropDownClicked(tableRow);
								tr.removeClass('shown');
								tableID.trigger("row:close",row(tableRow));
							} else {
								// Open this row
								tableRow.child(format(tableRow.data())).show();
								watchDropDownClicked(tableRow);
								tr.addClass('shown');
								tableID.trigger("row:open",row(tableRow));
							}
						});
			}

			function removeEventListeners(table) {

			}

			function addRowOpenEvent(table, modul, callback) {
				table.on("row:open", function(row) {
					callback(row);
				});
			}
			function removeRowOpenEvent(table) {
				table.off("row:open");
			}
			
			function addRowCloseEvent(table, modul, callback) {
				table.on("row:close", function(row) {
					callback(row);
				});
			}
			function removeRowCloseEvent(table) {
				table.off("row:close");
			}
			
			function addEventListeners(modul, table) {

				var on = function(eventName, callback) {
					switch (eventName) {
					case "row:open":
						addRowOpenEvent(tableID, modul, callback);
						break;
					case "row:close":
						addRowCloseEvent(tableID, modul, callback);
						break;
					default:
						break;
					}
				}

				var off = function(eventName) {
					switch (eventName) {
					case "row:open":
						removeRowOpenEvent(tableID);
						break;
					case "row:close":
						removeRowCloseEvent(tableID);
						break;
					default:
						break;
					}
				}

				modul.on = on;
				modul.off = off;
			}

			function addStandardMethods(modul, table) {

				modul.apkStatus = {
					NOT_RUNNING : "NOT_RUNNING",
					EXPLORING : "EXPLORING",
					SUCCESS : "SUCCESS",
					ABORTED : "ABORTED",
					ERROR : "ERROR"
				};

				// add new apk to table
				modul.addAPKData = function(name, time, elementsSeen, screensSeen,
						widgetsClicked, status) {
					var tableRow = table.row.add([ "", name, time, {
						elementsSeen : elementsSeen,
						screensSeen : screensSeen,
						widgetsClicked : widgetsClicked
					} ]);

					var labelContainer = $(tableRow.node()).find(
							'.status-label');
					switch (status) {
					case modul.apkStatus.SUCCESS:
						labelContainer.addClass('glyphicon glyphicon-ok');
						break;
					case modul.apkStatus.NOT_RUNNING:
						labelContainer.addClass('glyphicon glyphicon-time');
						break;
					case modul.apkStatus.ERROR:
						labelContainer.addClass('glyphicon glyphicon-remove');
						break;
					default:
						labelContainer.addClass('glyphicon glyphicon-remove');
						break;
					}

					return row(tableRow, modul);
				}

				// Remove apk row by name
				modul.removeAPKDataByName = function(name) {
					var rowToRemove = null;
					table.rows().every(function(rowIdx, tableLoop, rowLoop) {
						if (this.data()[1] === name) {
							rowToRemove = this;
						}
					});
					rowToRemove.remove();
				}

				// Remove apk row by row
				modul.removeAPKDataByRow = function(row) {
					removeAPKDataByName(row.data[1]);
				}

				// clear table
				modul.clear = function() {
					table.clear();
				}

				// get custom row by name
				modul.getRowByName = function(name) {
					var resultingRow = null;
					table.rows().every(function(rowIdx, tableLoop, rowLoop) {
						if (this.data()[1] === name) {
							resultingRow = this;
						}
					});

					if (resultingRow == null)
						return resultingRow;

					return row(resultingRow, modul);
				}

				modul.getRows = function() {
					var returnRows = [];
					table.rows().every(function(rowIdx, tableLoop, rowLoop) {
						returnRows.push(row(this, modul));
					});
					return returnRows;
				}

				modul.getRowsCount = function() {
					return table.rows().count();
				}
				
				modul.redraw = function() {
					table.draw();
				}
			}
			// -----------------

			function modul(table) {
				// remove existing event handlers
				removeStadardEvents(table);

				// add event listener to table
				addStandardEvents(table);

				// create modul
				var modul = {};
				addStandardMethods(modul, table);

				// remove existing event handlers
				removeEventListeners(table);

				// add custom event listeners
				addEventListeners(modul, table);

				return modul;
			}

			var init = function(tableIDentifier) {
				// create new table
				tableID = tableIDentifier;
				var table = null;
				if ($.fn.DataTable.isDataTable(tableID)) {
					table = $(tableID).DataTable();
				} else {
					table = $(tableID).DataTable(datatableOptions);
				}

				return modul(table);
			}

			return {
				initModul : init
			};
		})