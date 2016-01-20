define([ 'jquery', 'jstree' ], function(require) {

	$('#fileTree').jstree({
		'core' : {
			'data' : {
				"url" : "FileSystem?type=directory",
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

	$('#fileSavingDialog .modal-footer button').on('click', function(e) {
		var selectedItems = $('#fileTree').jstree(true).get_selected(true);
		if (selectedItems.length > 0) {
			$('#output-folder-name').val(selectedItems[0].text);
		}
	});
});