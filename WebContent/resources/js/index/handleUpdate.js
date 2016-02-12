define([ 'require', 'jquery', '../index/apkFileInfoTable',
		'jquery.droidmate.ajax',
		'jquery.droidmate.overlays' ], function(require,jquery, tableCreator, DMAjax, DMOverlays) {

	var table = tableCreator.initModul(jquery('#table-apk-static-information'));

	// set up event handler
	function disableUI() {
		jquery("#button-start-exploration").prop("disabled", true);
		jquery('#button-inline-files').prop("disabled", true);
	}

	function enableUI() {
		jquery("#button-start-exploration").prop("disabled", false);
		jquery('#button-inline-files').prop("disabled", false);
	}

	function showControls() {
		jquery('#div-apk-static-information-container').show();
		jquery('#div-apk-folder-selection-result').show();
		jquery('#button-show-static-information').show();
		jquery('#buttons-start-inline').show();
	}

	function hideControls() {
		jquery('#div-apk-static-information-container').hide();
		jquery('#div-apk-folder-selection-result').hide();
		jquery('#button-show-static-information').hide();
		jquery('#buttons-start-inline').hide();
	}

	function enableAPKFolderSelectionButton() {
		jquery('#button-apk-folder-selection').prop("disabled", false);
	}
	
	function disableAPKFolderSelectionButton() {
		jquery('#button-apk-folder-selection').prop("disabled", true);
	}
	
	function updateUIControls(callback) {
		// get current user status
		DMAjax.getUserStatus(true, function(data) {
			// check for error in data receiving
			if (!data || !data.getUserStatus || !data.getUserStatus.result) {
				DMOverlays.danger(
						"Could not parse server returned value.",
						DMOverlays.ERROR_MESSAGE_TIMEOUT);
				if(callback) {
					callback(data);
				}
				return;
			}

			var statusData = data.getUserStatus.payload.data;

			if (!statusData) {
				if(callback) {
					callback(data);
				}
				return;
			}

			// if more than zero entries are in the table, show buttons and
			// table and indicator 
			var rowsCount = table.getRows().length;
			var apksLoadingResultDiv = jquery('#div-apk-folder-selection-result');
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
			} else {
				disableUI();
			}

			// if apks are selected, which are not inlined, disable exploration
			// button
			var notInlinedRows = jquery.map(selectedRows, function(val, i) {
				if (val.getInlinedStatus() == table.inlinedStatus.INLINED)
					return null;
				else
					return true;
			})
			
			if (notInlinedRows.length) {
				jquery("#button-start-exploration").prop("disabled", true);
			}

			// If status is inlining, disable exploration button
			if (statusData === "INLINING") {
				disableUI();
				disableAPKFolderSelectionButton();
			} else {
				enableAPKFolderSelectionButton();
				// if all apks are inlined, disable inline button
				if (!notInlinedRows.length) {
					jquery("#button-inline-files").prop("disabled", true);
				} else {
					jquery("#button-inline-files").prop("disabled", false);
				}
			}
			
			if(callback) {
				callback(data);
			}
		});
	}

	return {
		updateUI : updateUIControls
	};
});