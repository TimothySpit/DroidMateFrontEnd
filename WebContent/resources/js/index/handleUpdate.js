define([ 'require', 'jquery', 'jstree', '../index/apkFileInfoTable',
		'jquery.flot', 'jquery.flot.tooltip', 'jquery.droidmate.ajax',
		'jquery.droidmate.inlining', 'DataTables', 'jquery.droidmate.dialogs',
		'jquery.droidmate.overlays' ], function(require) {

	var tableCreator = require('../index/apkFileInfoTable');
	var table = tableCreator.initModul($('#table-apk-static-information'));

	function testRowSelectionProperties() {
		var selectedRows = table.getSelectedRows();
		var notInlinedRows = $.map(selectedRows, function(val, i) {
			if (val.getInlinedStatus() == table.inlinedStatus.INLINED)
				return null;
			else
				return true;
		})

		if (selectedRows.length === 0) {
			$("#button-start-exploration").prop("disabled", true);
			$('#button-inline-files').prop("disabled",true);
		} else if(notInlinedRows.length > 0) {
			$("#button-start-exploration").prop("disabled", true);
			$('#button-inline-files').prop("disabled",false);
		} else {
			$("#button-start-exploration").prop("disabled", false);
			$('#button-inline-files').prop("disabled",false);
		}
	}
	
	// set up event handler
	table.on("row:select", function(e) {
		testRowSelectionProperties();
	});
	
	testRowSelectionProperties();
});