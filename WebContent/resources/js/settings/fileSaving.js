$(function() {
	$('#fileTree').jstree({
		'core' : {
			'data' : {
				"url" : "FileSystem?type=all",
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

