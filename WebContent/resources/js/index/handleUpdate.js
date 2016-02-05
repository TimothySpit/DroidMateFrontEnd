define([ 'require', 'jquery', 'jstree', '../index/apkFileInfoTable',
		'jquery.flot', 'jquery.flot.tooltip', 'jquery.droidmate.ajax',
		'jquery.droidmate.inlining', 'DataTables', 'jquery.droidmate.dialogs',
		'jquery.droidmate.overlays' ], function(require) {

	var tableCreator = require('../index/apkFileInfoTable');
	var table = tableCreator.initModul($('#table-apk-static-information'));

	// set up event handler
	function disableUI() {
		$("#button-start-exploration").prop("disabled", true);
		$('#button-inline-files').prop("disabled", true);
	}

	function enableUI() {
		$("#button-start-exploration").prop("disabled", false);
		$('#button-inline-files').prop("disabled", false);
	}

	function showControls() {
		$('#div-apk-static-information-container').show();
		$('#div-apk-folder-selection-result').show();
		$('#button-show-static-information').show();
		$('#buttons-start-inline').show();
	}

	function hideControls() {
		$('#div-apk-static-information-container').hide();
		$('#div-apk-folder-selection-result').hide();
		$('#button-show-static-information').hide();
		$('#buttons-start-inline').hide();
	}

	function updateUIControls() {
		// disables all visible controls to disabled
		disableUI();

		// get current user status
		$.droidmate.ajax.getUserStatus(true, function(data) {
			// check for error in data receiving
			if (!data || !data.getUserStatus || !data.getUserStatus.result) {
				$.droidmate.overlays.danger(
						"Could not parse server returned value.",
						$.droidmate.overlays.ERROR_MESSAGE_TIMEOUT);
				return;
			}

			var statusData = data.getUserStatus.payload.data;

			if (!statusData) {
				return;
			}

			// if more than zero entries are in the table, show buttons and
			// table and indicator 
			var rowsCount = table.getRows().length;
			var apksLoadingResultDiv = $('#div-apk-folder-selection-result');
			if (rowsCount) {
				showControls();
				apksLoadingResultDiv.html(
							'<span class="label label-success text-center">'
									+ rowsCount + ' apks loaded.</span>');
			} else {
				apksLoadingResultDiv.html(
						'<span class="label label-danger text-center">No apks loaded.</span>');
			}

			// if more than one apk is selected, enable buttons
			var selectedRows = table.getSelectedRows();
			var selectedRowsCount = selectedRows.length;
			if (selectedRowsCount) {
				enableUI();
			}

			// if apks are selected, which are not inlined, disable exploration
			// button
			var notInlinedRows = $.map(selectedRows, function(val, i) {
				if (val.getInlinedStatus() == table.inlinedStatus.INLINED)
					return null;
				else
					return true;
			})
			if (notInlinedRows.length) {
				$("#button-start-exploration").prop("disabled", true);
			}

			// If status is inlining, disable exploration button
			if (statusData === "INLINING") {
				$("#button-start-exploration").prop("disabled", true);
				$("#button-inline-files").prop("disabled", true);
			} else {
				// if all apks are inlined, disable inline button
				if (!notInlinedRows.length) {
					$("#button-inline-files").prop("disabled", true);
				} else {
					$("#button-inline-files").prop("disabled", false);
				}
			}
		});
	}

	return {
		updateUI : updateUIControls
	};
});