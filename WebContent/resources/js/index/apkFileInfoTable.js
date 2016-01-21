define(
		[ 'jquery', 'jstree', 'jquery.droidmate.ajax', 'DataTables' ],
		function(require) {

			var rows_selected = [];
			var tableID = null;
			var checkbox_all_click = false;

			var options = {
				columns : [
						{
							title : '<input name="select_all" value="1" type="checkbox">'
						}, {
							title : "Name"
						}, {
							title : "Size"
						}, {
							title : "Package"
						}, {
							title : "Version"
						}, {
							title : "Inlined"
						} ],
				'columnDefs' : [
						{
							'targets' : 0,
							'searchable' : false,
							'orderable' : false,
							'className' : 'dt-body-center',
							'render' : function(data, type, full, meta) {
								return '<input type="checkbox">';
							}
						},
						{
							'targets' : 5,
							'searchable' : false,
							'orderable' : false,
							'className' : 'dt-body-center',
							'render' : function(data, type, full, meta) {
								return '<span class="inline-label label label-danger"></span>';

							}
						} ],
				'searching' : false,
				'paging' : false,
				'order' : [ [ 1, 'asc' ] ],
				'rowCallback' : function(row, data, dataIndex) {
					// Get row ID
					var rowId = data[1];

					// If row ID is in the list of selected
					// row IDs
					if ($.inArray(rowId, rows_selected) !== -1) {
						$(row).find('input[type="checkbox"]').prop('checked',
								true);
						$(row).addClass('selected');
					}
				}
			};

			var row = function(rowNode, modul) {
				var inlineSpan = rowNode.node();
				var labelContainer = $(inlineSpan).find(
						'.dt-body-center .inline-label');
				return {
					getName : function() {
						return rowNode.data()[1];
					},
					getSize : function() {
						return rowNode.data()[2];
					},
					getPackage : function() {
						return rowNode.data()[3];
					},
					getVersion : function() {
						return rowNode.data()[4];
					},
					getInlinedStatus : function() {
						if (labelContainer.hasClass('label-success')) {
							return modul.inlinedStatus.INLINED;
						}
						if (labelContainer.hasClass('label-warning')) {
							return modul.inlinedStatus.NOT_INLINED;
						}
						if (labelContainer.hasClass('label-info')) {
							return modul.inlinedStatus.INLINING;
						}
						if (labelContainer.hasClass('label-danger')) {
							return modul.inlinedStatus.ERROR;
						}
					},
					updateName : function(name) {
						rowNode.data()[1] = name;
						rowNode.draw();
					},
					updateSize : function(size) {
						rowNode.data()[2] = size;
						rowNode.draw();
					},
					updatePackage : function(packageInfo) {
						rowNode.data()[3] = packageInfo;
						rowNode.draw();
					},
					updateVersion : function(version) {
						rowNode.data()[4] = version;
						rowNode.draw();
					},
					updateInlinedStatus : function(status) {
						labelContainer.removeClass(function(index, css) {
							return (css.match(/(^|\s)label-\S+/g) || [])
									.join(' ');
						});

						if (status === modul.inlinedStatus.INLINED) {
							labelContainer.addClass('label-success');
							labelContainer.text("INLINED");
						} else if (status === modul.inlinedStatus.NOT_INLINED) {
							labelContainer.addClass('label-warning');
							labelContainer.text("NOT INLINED");
						} else if (status === modul.inlinedStatus.INLINING) {
							labelContainer.addClass('label-info');
							labelContainer.text("INLINING...");
						} else if (status === modul.inlinedStatus.ERROR) {
							labelContainer.addClass('label-danger');
							labelContainer.text("ERROR");
						}
						rowNode.draw();
					},
				}
			}

			function updateDataTableSelectAllCtrl(table) {
				var $table = table.table().node();
				var $chkbox_all = $('tbody input[type="checkbox"]', $table);
				var $chkbox_checked = $('tbody input[type="checkbox"]:checked',
						$table);
				var chkbox_select_all = $('thead input[name="select_all"]',
						$table).get(0);

				// If none of the checkboxes are checked
				if ($chkbox_checked.length === 0) {
					chkbox_select_all.checked = false;
					if ('indeterminate' in chkbox_select_all) {
						chkbox_select_all.indeterminate = false;
					}

					// If all of the checkboxes are checked
				} else if ($chkbox_checked.length === $chkbox_all.length) {
					chkbox_select_all.checked = true;
					if ('indeterminate' in chkbox_select_all) {
						chkbox_select_all.indeterminate = false;
					}

					// If some of the checkboxes are checked
				} else {
					chkbox_select_all.checked = true;
					if ('indeterminate' in chkbox_select_all) {
						chkbox_select_all.indeterminate = true;
					}
				}
			}

			function selectAll(e) {
				var $row = $(this).closest('tr');

				// Get row data
				var data = table.row($row).data();

				// Get row ID
				var rowId = data[1];

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

			function removeStadardEvents(table) {
				tableID.find('tbody').off('click');
				tableID.off('click');
				$('#selectiontable thead input[name="select_all"]')
						.off('click');

				table.off('draw');
			}

			function addStandardEvents(table) {

				// add event listener to table
				$('#selectiontable tbody').on('click',
						'input[type="checkbox"]', function(e) {
							var $row = $(this).closest('tr');

							// Get row data
							var data = table.row($row).data();

							// Get row ID
							var rowId = data[1];

							// Determine whether row ID is in the list of
							// selected row
							// IDs
							var index = $.inArray(rowId, rows_selected);

							// If checkbox is checked and row ID is not in list
							// of
							// selected row IDs
							if (this.checked && index === -1) {
								rows_selected.push(rowId);

								// Otherwise, if checkbox is not checked and row
								// ID is
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

							if (!checkbox_all_click) {
								tableID.trigger("row:select");
							}

							// Prevent click event from propagating to parent
							e.stopPropagation();
						});

				// Handle click on table cells with checkboxes
				$('#selectiontable').on(
						'click',
						'tbody td, thead tr:first-child',
						function(e) {
							$(this).parent().find('input[type="checkbox"]')
									.trigger('click');
						});

				// Handle click on "Select all" control
				$('#selectiontable thead input[name="select_all"]')
						.on(
								'click',
								function(e) {

									checkbox_all_click = true;

									if (this.checked) {
										$(
												'#selectiontable tbody input[type="checkbox"]:not(:checked)')
												.trigger('click');
									} else {
										$(
												'#selectiontable tbody input[type="checkbox"]:checked')
												.trigger('click');
									}

									checkbox_all_click = false;

									tableID.trigger("row:select");

									// Prevent click event from propagating to
									// parent
									e.stopPropagation();
								});

				// Handle table draw event
				table
						.on(
								'draw',
								function() {
									$(
											'#selectiontable tbody input[type="checkbox"]:not(:checked)')
											.trigger('click');
									// Update state of "Select all" control
									updateDataTableSelectAllCtrl(table);
								});
			}

			function addRowSelectionEvent(table, modul, callback) {
				table.on("row:select", function() {
					var rows = [];
					$.each(rows_selected, function(index, value) {
						rows.push(modul.getRowByName(value));
					});
					callback(rows);
				});
			}

			function removeRowSelectionEvent(table) {
				table.off("row:select");
			}

			function removeEventListeners(table) {
				removeRowSelectionEvent(tableID);
			}
			
			function addEventListeners(modul, table) {

				var on = function(eventName, callback) {
					switch (eventName) {
					case "row:select":
						addRowSelectionEvent(tableID, modul, callback);
						break;

					default:
						break;
					}
				}

				var off = function(eventName) {
					switch (eventName) {
					case "row:select":
						removeRowSelectionEvent(tableID);
						break;

					default:
						break;
					}
				}

				modul.on = on;
				modul.off = off;
			}

			function addStandardMethods(modul, table) {
				modul.inlinedStatus = {
					INLINED : "INLINED",
					NOT_INLINED : "NOT_INLINED",
					INLINING : "INLINING",
					ERROR : "ERROR"
				};

				// add new apk to table
				modul.addAPKData = function(name, size, packageInfo, version,
						inlinedStatus) {
					var row = table.row.add(
							[ "", name, size, packageInfo, version, "" ])
							.draw().node();
					var labelContainer = $(row).find(
							'.dt-body-center .inline-label').parent();

					switch (inlinedStatus) {
					case modul.inlinedStatus.INLINED:
						labelContainer
								.html('<span class="inline-label label label-success">INLINED</span>');
						break;
					case modul.inlinedStatus.NOT_INLINED:
						labelContainer
								.html('<span class="inline-label label label-warning">NOT INLINED</span>');
						break;
					case modul.inlinedStatus.INLINING:
						labelContainer
								.html('<span class="inline-label label label-info">INLINING...</span>');
						break;
					case modul.inlinedStatus.ERROR:
						labelContainer
								.html('<span class="inline-label label label-danger">ERROR</span>');
						break;
					default:
						labelContainer
								.html('<span class="inline-label label label-danger">ERROR</span>');
						break;
					}
				}

				// Remove apk row by name
				modul.removeAPKDataByName = function(name) {
					var rowToRemove = null;
					table.rows().every(function(rowIdx, tableLoop, rowLoop) {
						if (this.data()[1] === name) {
							rows_selected[name] = null;
							rowToRemove = this;
						}
					});
					rowToRemove.remove();
					table.draw();
				}

				// Remove apk row by row
				modul.removeAPKDataByRow = function(row) {
					removeAPKDataByName(row.data[1]);
				}

				// clear table
				modul.clear = function() {
					table.clear();
					table.draw();
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
			}

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
					table = $(tableID).DataTable(options);
				}

				return modul(table);
			}

			return {
				initModul : init
			};
		});