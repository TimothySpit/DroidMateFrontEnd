define(
		[ 'jquery', 'DataTables', 'jquery.flot.symbol', 'jquery.flot.canvas',
				'jquery.flot.axislabels', 'jquery.flot.navigate',
				'jquery.flot', 'jquery.droidmate.ajax',
				'jquery.droidmate.explore' ],
		function(require) {

			//private members
			var activeChartDivs = {};
			var tableID = null;
			
			var tableUpdateID = 0;
			//-----------------------
			
			// chart options
			var graphChartOptions = {
				yaxis : {
					labelWidth : 30,
					axisLabel : axisLabelY,
					axisLabelUseCanvas : true,
					axisLabelFontSizePixels : 20,
					axisLabelFontFamily : 'Arial',
					minTickSize : 1,
					min : 0,
				},
				xaxis : {
					labelHeight : 30,
					axisLabel : axisLabelx,
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
					title : '""'
				}, {
					title : "Name"
				}, {
					title : "Elements seen / Screens seen / Widgets clicked"
				} ],
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
							"render" : function(data, type, row) {
								return '<span class="apk-name">'
										+ data
										+ '</span>';
							}
						},
						{
							"targets" : 2,
							"searchable" : false,
							"render" : function(data, type, row) {
								return '<div class="elements-screens-seen">0/0/0</div>';
							}
						},
						{
							"targets" : 3,
							"searchable" : false,
							"render" : function(data, type, row) {
								return '<div class="state">'
										+ '<span class="glyphicon glyphicon glyphicon-asterisk" aria-hidden="true"></span>'
										+ '</div>'
							}
						} ]
			};

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
			
			
			
			
			//---------------
			function removeStadardEvents(table) {
				tableID.find('tbody').off('click');
			}

			function removeFromWatchDropDownClicked(row) {
				activeChartDivs[row[1]] = null;
			}
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

				var chart = {
					chartDivElementsSeen : chartDivElementsSeen,
					chartDivScreensSeen : chartDivScreensSeen,
					chartDivElementsExplored : chartDivElementsExplored,
					chartElementsSeen : chartElementsSeen,
					chartScreensSeen : chartScreensSeen,
					chartElementsExplored : chartElementsExplored
				};
				activeChartDivs[row[1]] = chart;
				
				return chart;
			}
			
			function createChart(divname, axisLabelx, axisLabelY) {
				
				var elementsExplored = [ [ 0, 0 ]];
				return $.plot(divname, [ elementsExplored ], options);
			}
			
			function addStandardEvents(table) {
				tableID.find('tbody').on('click', 'td.details-control', function() {
					var table = tableID.DataTable();
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

			function removeEventListeners(table) {
				
			}
			
			function addEventListeners(modul, table) {
			
			}

			function addStandardMethods(modul, table) {
			
				
			}
			//-----------------
			
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
				
				//start updating table
				updateCharts();
				
				return modul;
			}
			
			var init = function(tableIDentifier) {
				// create new table
				tableID = tableIDentifier;
				var table = null;
				if ($.fn.DataTable.isDataTable(tableID)) {
					table = $(tableID).DataTable();
				} else {
					table = $(tableID).DataTable(options);
				}

				return modul(table);
			}

			return {
				initModul : init
			};
		})