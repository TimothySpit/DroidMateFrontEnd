$(function() {
	$('#fileTreeAndroidSDK').jstree({
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
});

$(function() {
	$('#androidSDKDialog .modal-footer button').on('click', function(e) {
		var selectedItems = $('#fileTreeAndroidSDK').jstree(true).get_selected(true);
		if (selectedItems.length > 0) {
			$('#androidSDK-folder-name').val(selectedItems[0].text);
		}
	});
});