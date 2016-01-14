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
							var success = value.success;
							var finished = value.finished;

							var apkRows = $('#exploreFiles_wrapper .apk-name');
							apkRows
									.each(function(index) {
										// check for name
										if ($(this).text() == name) {
											// found apk
											var elementsSeenDiv = $(
													this.parentElement.parentElement)
													.find('.elements-seen');
											elementsSeenDiv.text(elementsSeen);
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
	}
	;

	/* Formatting function for row details - modify as you need */
	function format(d) {
		// `d` is the original data object for the row
		return '<div class="apk-info-min-container">'
				+ '<div class="apk-chart-min" id="apk-chart-min-' + d[1] + '"></div>'
				+ '<div class="apk-info-min" id="apk-info-min-' + d[1] + '"></div>'
				+ '</div>';
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
				tr.removeClass('shown');
			} else {
				// Open this row
				row.child(format(row.data())).show();
				watchDropDownClicked(row.data());
				tr.addClass('shown');
			}
		});
	}

	function createChartElementsSeen(divname) {
	    var options =  {
	            yaxis: {
	                labelWidth: 30,
	                axisLabel: 'GUI elements seed',
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
	    var elementsExplored = [[0,0],[0,0],[0,0],[0,0],[0,0]];
	    $.plot(divname, [elementsExplored], options);
	};
	
	//create each chart when element gets opened
	function watchDropDownClicked(row) {
		console.log(row);
		var chartDiv = $('[id="apk-chart-min-' + row[1] + '"]');
		console.log(chartDiv);
		createChartElementsSeen(chartDiv);
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
																return '<div class="elements-seen">0</div>';
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
