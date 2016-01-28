define(
		[ 'require', 'jquery', 'jstree', '../index/apkFileInfoTable',
				'jquery.flot', 'jquery.flot.tooltip', 'jquery.droidmate.ajax',
				'jquery.droidmate.inlining', 'DataTables' ],
		function(require) {

			var tableCreator = require('../index/apkFileInfoTable');
			var table = tableCreator.initModul($('#selectiontable'));

			function updateTable(table) {
				table.clear();
				var data = $.droidmate.ajax.get.getAllAPKS();
				var numAPKS = data["info[]"].apks.data.length;
				if (numAPKS > 0) {
					$(".apk-data").show();
					$('#load-result-indikator').html(
							'<span class="label label-success text-center">'
									+ numAPKS + ' apks loaded.</span>');
					$("#btns-field").show();
					$("#show-static").show();
				} else {
					$(".apk-data").hide();
					$('#load-result-indikator')
							.html(
									'<span class="label label-danger text-center">no apks loaded.</span>');
					$("#btns-field").hide();
					$("#show-static").hide();
				}

				$.each(data["info[]"].apks.data, function(index, value) {
					var inlinedStatus = table.inlinedStatus.INLINED;
					if (value.inlined == false) {
						inlinedStatus = table.inlinedStatus.NOT_INLINED;
					}
					table.addAPKData(value.name, value.size, value.package,
							value.version, inlinedStatus);
				});
				
				table.redraw();
			}

			// set up event handler
			table.on("row:select", function(e) {
				if (e.length === 0) {
					$("#startexploration").prop("disabled", true);
				} else {
					$("#startexploration").prop("disabled", false);
				}

				var selectedAPKS = [];
				$.each(e, function(index, value) {
					selectedAPKS.push(value.getName());
				});
				$.droidmate.ajax.post.setSelectedAPKS(selectedAPKS);
			});

			$('#folderSelectModal .modal-footer button')
					.click(
							function(e) {

								var selectedItems = $('#folderTree').jstree(
										true).get_selected(true);
								if (selectedItems.length > 0) {
									$('#folder_name')
											.val(selectedItems[0].text);

									var path = encodeURIComponent(selectedItems[0].text);

									console.log(selectedItems[0].text);
									
									$.droidmate.ajax.post.setAPKRoot(
											selectedItems[0].text, false);
									updateTable(table);
								}
							});
			// --------------------------------------------------------------

			var path = $.droidmate.ajax.get.getSelectedAPKRoot();
			$('#folder_name').val(path);

			if (path !== "")
				updateTable(table);
		});