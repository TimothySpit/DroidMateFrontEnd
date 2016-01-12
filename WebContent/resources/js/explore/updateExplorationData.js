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
			var finished = value.finished;
			
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
					
					if(finished) {
						if(success) {
							statusDiv.html('<span class="glyphicon glyphicon-ok-sign" aria-hidden="true"></span>');
							showReportButton($(this.parentElement.parentElement));
						} else {
							statusDiv.html('<span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>');
						}
					}
				}
			});
		});
		
		setTimeout(function() {
			updateExplorationInfo();
		}, $.droidmate.ajax.UPDATE_EXPLORATION_INFO_INTERVAL);
	}

	function showReportButton(row) {
		$(row).find("button").prop("disabled",false);
		$(row).find("button").attr("href",$.droidmate.ajax.get.getReport());
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
																		+ '<span class="glyphicon glyphicon glyphicon-asterisk" aria-hidden="true"></span>'
																		+ '</div>'
															}
														} ]
											});

						}
					});

});