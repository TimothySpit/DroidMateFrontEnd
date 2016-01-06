$(function() {
	$('#folderTree').jstree({
		'core' : {
			'data' : {
				"url" : "/DroidMate/FileSystem?type=dir",
				"data" : function(node) {
					if (node.text)
						return {
							"path" : node.text
						};
					else
						return {
							"path" : "root"
						};
				}
			}
		}
	});
});

$(function() {
	var rows_selected = [];
	function createTable() {

		$('#selectiontable').DataTable().destroy();
		var table = $('#selectiontable')
				.DataTable(
						{
							"ajax" : {
								'url' : '/DroidMate/APKPathHandler?info[]=apks',
								"dataSrc" : function(json) {
									if (json["info[]"].apks.data.length <= 0) {
										$(".apk-data").addClass("hide");
										$('#load-result-indikator')
												.html(
														'<span class="label label-danger text-center">no apks loaded.</span>');
										$("#startexploration").addClass("hide");
										$("#show-static").addClass("hide");
									} else {
										$(".apk-data").removeClass("hide");
										$('#load-result-indikator')
												.html(
														'<span class="label label-success text-center">'
																+ json["info[]"].apks.data.length
																+ ' apks loaded.</span>');
										$("#startexploration").removeClass(
												"hide");
										$("#show-static").removeClass("hide");
									}
									return json["info[]"].apks.data;
								}
							},
							'columnDefs' : [ {
								'targets' : 0,
								'searchable' : false,
								'orderable' : false,
								'className' : 'dt-body-center',
								'render' : function(data, type, full, meta) {
									return '<input type="checkbox">';
								}
							} ],
							'searching' : false,
							'paging' : false,
							'order' : [ [ 1, 'asc' ] ],
							'rowCallback' : function(row, data, dataIndex) {
								// Get row ID
								var rowId = data[0];

								// If row ID is in the list of selected row IDs
								if ($.inArray(rowId, rows_selected) !== -1) {
									$(row).find('input[type="checkbox"]').prop(
											'checked', true);
									$(row).addClass('selected');
								}
							}
						});

		function updateDataTableSelectAllCtrl(table) {
			var $table = table.table().node();
			var $chkbox_all = $('tbody input[type="checkbox"]', $table);
			var $chkbox_checked = $('tbody input[type="checkbox"]:checked',
					$table);
			var chkbox_select_all = $('thead input[name="select_all"]', $table)
					.get(0);

			// If none of the checkboxes are checked
			if ($chkbox_checked.length === 0) {
				chkbox_select_all.checked = false;
				if ('indeterminate' in chkbox_select_all) {
					chkbox_select_all.indeterminate = false;
				}
				$("#startexploration").addClass("disabled");

				// If all of the checkboxes are checked
			} else if ($chkbox_checked.length === $chkbox_all.length) {
				chkbox_select_all.checked = true;
				if ('indeterminate' in chkbox_select_all) {
					chkbox_select_all.indeterminate = false;
				}
				$("#startexploration").removeClass("disabled");
				// If some of the checkboxes are checked
			} else {
				chkbox_select_all.checked = true;
				if ('indeterminate' in chkbox_select_all) {
					chkbox_select_all.indeterminate = true;
				}
				$("#startexploration").removeClass("disabled");
			}
		}

		function selectAll(e) {
			var $row = $(this).closest('tr');

			// Get row data
			var data = table.row($row).data();

			// Get row ID
			var rowId = data[0];

			// Determine whether row ID is in the list of selected row
			// IDs
			var index = $.inArray(rowId, rows_selected);

			// If checkbox is checked and row ID is not in list of
			// selected row IDs
			if (this.checked && index === -1) {
				rows_selected.push(rowId);

				// Otherwise, if checkbox is not checked and row ID is
				// in list of selected row IDs
			} else if (!this.checked && index !== -1) {
				rows_selected.splice(index, 1);
			}

			if (this.checked) {
				$row.addClass('selected');
			} else {
				$row.removeClass('selected');
			}

			// Update state of "Select all" control
			updateDataTableSelectAllCtrl(table);

			// Prevent click event from propagating to parent
			e.stopPropagation();
		}
		;

		// set up checkbox handlers
		$('#selectiontable tbody').off('click', 'input[type="checkbox"]');
		$('#selectiontable tbody').on('click', 'input[type="checkbox"]',
				function(e) {
					var $row = $(this).closest('tr');

					// Get row data
					var data = table.row($row).data();

					// Get row ID
					var rowId = data[0];

					// Determine whether row ID is in the list of selected row
					// IDs
					var index = $.inArray(rowId, rows_selected);

					// If checkbox is checked and row ID is not in list of
					// selected row IDs
					if (this.checked && index === -1) {
						rows_selected.push(rowId);

						// Otherwise, if checkbox is not checked and row ID is
						// in list of selected row IDs
					} else if (!this.checked && index !== -1) {
						rows_selected.splice(index, 1);
					}

					if (this.checked) {
						$row.addClass('selected');
					} else {
						$row.removeClass('selected');
					}

					// Update state of "Select all" control
					updateDataTableSelectAllCtrl(table);

					// Prevent click event from propagating to parent
					e.stopPropagation();
				});

		// Handle click on table cells with checkboxes
		$('#selectiontable').off('click', 'tbody td, thead tr:first-child');
		$('#selectiontable').on(
				'click',
				'tbody td, thead tr:first-child',
				function(e) {
					$(this).parent().find('input[type="checkbox"]').trigger(
							'click');
				});

		// Handle click on "Select all" control
		$('#selectiontable thead input[name="select_all"]').off('click');
		$('#selectiontable thead input[name="select_all"]')
				.on(
						'click',
						function(e) {
							if (this.checked) {
								$(
										'#selectiontable tbody input[type="checkbox"]:not(:checked)')
										.trigger('click');
							} else {
								$(
										'#selectiontable tbody input[type="checkbox"]:checked')
										.trigger('click');
							}

							// Prevent click event from propagating to parent
							e.stopPropagation();
						});

		// Handle table draw event
		table.on('draw', function() {
			$('#selectiontable tbody input[type="checkbox"]:not(:checked)')
					.trigger('click');
			// Update state of "Select all" control
			updateDataTableSelectAllCtrl(table);
		});

		return table;
	}

	$.get("/DroidMate/APKPathHandler", {
		info : [ "apkRoot" ]
	}, function(data) {
		var res = JSON.parse(data);
		if (res && res["info[]"] && res["info[]"].apkRoot) {
			$('#folder_name').val(res["info[]"].apkRoot);
			createTable();
		}
	});

	$('#folderSelectModal .modal-footer button').on('click', function(e) {

		var selectedItems = $('#folderTree').jstree(true).get_selected(true);
		if (selectedItems.length > 0) {
			$('#folder_name').val(selectedItems[0].text);

			var path = encodeURIComponent(selectedItems[0].text);

			$.get("/DroidMate/APKPathHandler", {
				apkRoot : selectedItems[0].text
			});

			var table = createTable();
		}
	});
	
	$('#startexploration').click(
			function(e) {
				$.get("/DroidMate/APKPathHandler",{selApks : rows_selected}, function(data) {
					window.location = "/DroidMate/Explore";
				});
				
			});
});