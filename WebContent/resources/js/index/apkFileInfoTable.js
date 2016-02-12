define(
		[ 'require', 'jquery', 'DataTables' ],
		function(require, jquery, DataTables) {

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
						}, 
						{
							title : "Main activity Name"
						},
						{
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
							'targets' : 2,
							'orderable' : false,
						},
						{
							'targets' : 5,
							'orderable' : false,
						},
						{
							'targets' : 6,
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
					if (jquery.inArray(rowId, rows_selected) !== -1) {
						jquery(row).find('input[type="checkbox"]').prop('checked',
								true);
						jquery(row).addClass('selected');
					}
				}
			};

			var row = function(rowNode, modul) {
				var inlineSpan = rowNode.node();
				var labelContainer = jquery(inlineSpan).find(
						'.dt-body-center .inline-label');
				return {
					getName : function() {
						return rowNode.data()[1];
					},
					getSize : function() {
						return rowNode.data()[2];
					},
					getPackage : function() {
						return rowNode.data()[4];
					},
					getVersion : function() {
						return rowNode.data()[5];
					},
					getActivityName : function() {
						return rowNode.data()[3];
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
					},
					updateSize : function(size) {
						rowNode.data()[2] = size;
					},
					updateActivityName : function(activityName) {
						rowNode.data()[3] = activityName;
					},
					updatePackage : function(packageInfo) {
						rowNode.data()[4] = packageInfo;
					},
					updateVersion : function(version) {
						rowNode.data()[5] = version;
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
					},
				}
			}

			function updateDataTableSelectAllCtrl(table) {
				var jquerytable = table.table().node();
				var jquerychkbox_all = jquery('tbody input[type="checkbox"]', jquerytable);
				var jquerychkbox_checked = jquery('tbody input[type="checkbox"]:checked',
						jquerytable);
				var chkbox_select_all = jquery('thead input[name="select_all"]',
						jquerytable).get(0);

				// If none of the checkboxes are checked
				if (jquerychkbox_checked.length === 0) {
					chkbox_select_all.checked = false;
					if ('indeterminate' in chkbox_select_all) {
						chkbox_select_all.indeterminate = false;
					}

					// If all of the checkboxes are checked
				} else if (jquerychkbox_checked.length === jquerychkbox_all.length) {
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
				var jqueryrow = jquery(this).closest('tr');

				// Get row data
				var data = table.row(jqueryrow).data();

				// Get row ID
				var rowId = data[1];

				// Determine whether row ID is in the list of selected row
				// IDs
				var index = jquery.inArray(rowId, rows_selected);

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
					jqueryrow.addClass('selected');
				} else {
					jqueryrow.removeClass('selected');
				}

				// Update state of "Select all" control
				updateDataTableSelectAllCtrl(table);

				// Prevent click event from propagating to parent
				e.stopPropagation();
			}

			function removeStadardEvents(table) {
				tableID.find('tbody').off('click');
				tableID.off('click');
				tableID.find('thead input[name="select_all"]')
						.off('click');

				table.off('draw');
			}

			function addStandardEvents(table) {

				// add event listener to table
				tableID.find('tbody').on('click',
						'input[type="checkbox"]', function(e) {
							var jqueryrow = jquery(this).closest('tr');

							// Get row data
							var data = table.row(jqueryrow).data();

							// Get row ID
							var rowId = data[1];

							// Determine whether row ID is in the list of
							// selected row
							// IDs
							var index = jquery.inArray(rowId, rows_selected);

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
								jqueryrow.addClass('selected');
							} else {
								jqueryrow.removeClass('selected');
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
				tableID.on(
						'click',
						'tbody td, thead tr:first-child',
						function(e) {
							jquery(this).parent().find('input[type="checkbox"]')
									.trigger('click');
						});

				// Handle click on "Select all" control
				tableID.find('thead input[name="select_all"]')
						.on(
								'click',
								function(e) {

									checkbox_all_click = true;

									if (this.checked) {
										tableID.find(
												'tbody input[type="checkbox"]:not(:checked)')
												.trigger('click');
									} else {
										tableID.find(
												'tbody input[type="checkbox"]:checked')
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
									tableID.find(
											'tbody input[type="checkbox"]:not(:checked)')
											.trigger('click');
									// Update state of "Select all" control
									updateDataTableSelectAllCtrl(table);
								});
			}

			function addRowSelectionEvent(table, modul, callback) {
				table.on("row:select", function() {
					var rows = [];
					jquery.each(rows_selected, function(index, value) {
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
						inlinedStatus, activityName) {
					var rowAdded = table.row.add(
							[ "", name, size, activityName, packageInfo, version, "" ])
							;
					var labelContainer = jquery(rowAdded.node()).find(
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
					return row(rowAdded,modul);
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
				
				// get all rows
				modul.getRows = function() {
					var rows = [];
					table.rows().every(function(rowIdx, tableLoop, rowLoop) {
						rows.push(row(this, modul));
					});
				
					return rows;
				}
				
				// get selected rows
				modul.getSelectedRows = function() {
					var rows = [];
					table.rows().every(function(rowIdx, tableLoop, rowLoop) {
						var node = this.node();
						var cbs = jquery('input[type="checkbox"]:checked',
								node);
						if (cbs.length === 0)
							return;
						rows.push(row(this, modul));
					});
				
					return rows;
				}
				
				modul.redraw = function() {
					table.draw();
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
				if (DataTables.isDataTable(tableID)) {
					table = jquery(tableID).DataTable();
				} else {
					table = jquery(tableID).DataTable(options);
				}

				return modul(table);
			}

			return {
				initModul : init
			};
		});