$(function() {
	$('#folderTree').jstree({
		'core' : {
			'data' : {
				"url" : "FileSystem?type=dir",
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
	function createTable(path) {
		var rows_selected = [];

		$('#selectiontable').DataTable().destroy();
		var table = $('#selectiontable').DataTable(
				{
					"ajax" : {
						'url' : 'Index?apkInfo=' + path,
						"dataSrc" : function(json) {
							if (json.data.length <= 0) {
								$(".apk-data").addClass("hide");
								$("#startexploration").addClass("hide");
								$("#show-static").addClass("hide");
							} else {
								$(".apk-data").removeClass("hide");
								$("#startexploration").removeClass("hide");
								$("#show-static").removeClass("hide");
							}
							return json.data;
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
			// Update state of "Select all" control
			$('#selectiontable thead input[name="select_all"]')
					.trigger('click');
			updateDataTableSelectAllCtrl(table);
		});
		// Handle form submission event
		$('#frm-explore').off('submit');
		$('#frm-explore').on(
				'submit',
				function(e) {
					var form = this;

					// Iterate over all selected checkboxes
					$.each(rows_selected, function(index, rowId) {
						// Create a hidden element
						$(form).append(
								$('<input>').attr('type', 'hidden').attr(
										'name', 'id[]').val(rowId));
					});
				});

		return table;
	}

	if (Cookies.get('selectedAPKS')) {
		createTable(Cookies.get('selectedAPKS'));
	}

	$('#folderSelectModal .modal-footer button').on('click', function(e) {

		var selectedItems = $('#folderTree').jstree(true).get_selected(true);
		if (selectedItems.length > 0) {
			$('#folder_name').val(selectedItems[0].text);

			var path = encodeURIComponent(selectedItems[0].text);

			var table = createTable(path);
			Cookies.set('selectedAPKS', path);

		}
	});
});