$(function() {

	// start updating
	setTimeout(function() {
		updateExplorationInfo();
	}, $.droidmate.ajax.UPDATE_EXPLORATION_INFO_INTERVAL);

	// update exploration info
	function updateExplorationInfo() {
		var status = $.droidmate.ajax.get.getExplorationInfo();

		$.each(status, function(index, value) {
			var name = value.name;
			var elementsSeen = value.elementsSeen;
			var success = value.success;

			var apkRows = $('#exploreFiles_wrapper .apk-name');
			apkRows.each(function(index) {
				// check for name
				if ($(this).text() == name) {
					// found apk
					var elementsSeenDiv = $(this.parentElement.parentElement)
							.find('.elements-seen');
					elementsSeenDiv.text(elementsSeen);
					var statusDiv = $(this.parentElement.parentElement)
							.find('.state');
					statusDiv.text(success);
				}
			});
		});
		
		setTimeout(function() {
			updateExplorationInfo();
		}, $.droidmate.ajax.UPDATE_EXPLORATION_INFO_INTERVAL);
	}

	// retrieve files count
	$.ajaxSetup({
		cache : false
	});

	function getData() {
		$.ajaxSetup({
			cache : false
		});

		var fileName = $('#exploreFiles tbody tr:nth-child(' + (counter + 1)
				+ ') td:first-child .apk-name');
	}

	function showReportButton(row) {
		row.find(".apk-name button").disabled = false;
	}

	function updateExplorationStatus(_data) {
		var row = $('#exploreFiles tbody tr:nth-child(' + (counter + 1) + ')');
		var progressBar = row.find('.progress-bar');
		row.find('td .state').text(_data.state);

		if (_data.state == 'FINISHED') {
			showReportButton(row);
		}

		setTimeout(getData, updateInterval);
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
														return json.data;
													}
												},
												'searching' : false,
												'paging' : false,
												"columnDefs" : [
														{
															"targets" : 0,
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
															"targets" : 1,
															"searchable" : false,
															"render" : function(
																	data, type,
																	row) {
																return '<div class="elements-seen">0</div>';
															}
														},
														{
															"targets" : 2,
															"searchable" : false,
															"render" : function(
																	data, type,
																	row) {
																return '<div class="state">'
																		+ data
																		+ '</div>'
															}
														} ]
											});

						}
					});

});