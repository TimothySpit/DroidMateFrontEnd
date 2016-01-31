define(
		[ 'require', 'jquery', 'bootbox', 'jstree', 'jquery.droidmate.dialogs' ],
		function(require, jquery, bootbox) {

			//fill in textboxes
			var settings = $.droidmate.ajax.get.getDroidMateSettings();
			$('#output-folder-name').val(settings.outputPath);
			$('#droidmate-folder-name').val(settings.droidmatePath);
			$('#aapt-folder-name').val(settings.aaptPath);
			$('#explorationTime').val(settings.time);
			
			//configure Output folder dialog
			$('#output-folder-btn').on(
					'click',
					function(e) {
						jquery.droidmate.dialogs.createFileDialog(
								'Select Reports otput path', function(
										selectedItems) {
									if (selectedItems.length > 0) {
										$('#output-folder-name').val(
												selectedItems[0].text);
									}
								});
					});

			//configure Droidmate folder dialog
			$('#droidmate-folder-name-btn').on(
					'click',
					function(e) {
						jquery.droidmate.dialogs.createFileDialog(
								'Select DroidMate path', function(
										selectedItems) {
									if (selectedItems.length > 0) {
										$('#droidmate-folder-name').val(
												selectedItems[0].text);
									}
								});
					});
			
			//configure AAPT folder dialog
			$('#aapt-folder-name-btn').on(
					'click',
					function(e) {
						jquery.droidmate.dialogs.createFileDialog(
								'Select AAPT folder path', function(
										selectedItems) {
									if (selectedItems.length > 0) {
										$('#aapt-folder-name').val(
												selectedItems[0].text);
									}
								});
					});
		});