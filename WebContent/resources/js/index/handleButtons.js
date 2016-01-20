define([ 'jquery', 'jstree'], function(require) {
	
	//folder selection button
	$('#folderSelectModal .modal-footer button').on(
			'click',
			function(e) {

				var selectedItems = $('#folderTree').jstree(true).get_selected(
						true);
				if (selectedItems.length > 0) {
					$('#folder_name').val(selectedItems[0].text);

					var path = encodeURIComponent(selectedItems[0].text);

					$.droidmate.ajax.post.setAPKRoot(selectedItems[0].text,
							false, function(data) {
								if (data.success) {
									createTable();
								}
							});
				}
			});
	//-----------------------------------------------------------
	
	
	
});