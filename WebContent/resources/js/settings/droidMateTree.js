$(function() {
	$('#fileTreeDroidMate').jstree({
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
	$('#droidmateDialog .modal-footer button').on('click', function(e) {
		var selectedItems = $('#fileTreeDroidMate').jstree(true).get_selected(true);
		if (selectedItems.length > 0) {
			$('#dm-output-folder-name').val(selectedItems[0].text);
		}
	});
});