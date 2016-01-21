define(
		[ 'require', 'jquery', 'bootbox', 'jquery.droidmate.ajax' ],
		function(require, jquery, bootbox) {
			// Set output path label
			var settings = $.droidmate.ajax.get.getDroidMateSettings();
			$("#outputPathLabel").html(settings["outputPath"]);

			$('#outputPathLabel').click(function(e) {
				var text = $(this).text();
				var $this = $(this);
				var $input = $('<input type=text>');
				$input.prop('value', text);
				$input.insertAfter($(this));
				$input.focus();
				$input.select();
				$this.hide();
				$input.focusout(function() {
					$this.show();
					$input.remove();
				});
			});
			// --------------------------------------

			// handle StopAllButton
			$('#stopAllBtn').click(function(e) {
				$.droidmate.ajax.post.stopDroidMate();
			});

			// exploration button handler
			$('#back-to-index')
					.click(
							function(e) {
								bootbox
										.confirm({
											title : "Return to start?",
											message : "If you go back to the index page, droidmate will be stopped and all exploration results are deleted!",
											buttons : {
												cancel : {
													label : "Cancel",
													className : "btn-default pull-left"
												},
												confirm : {
													label : "Back to index",
													className : "btn-danger pull-right"
												}
											},
											callback : function(result) {
												if (result) {
													// Gray out the button
													$('back-to-index').prop(
															"disabled", true);
													$.droidmate.ajax.post
															.stopDroidMate(function(
																	e) {
																window.location = "/DroidMate/Index";
															});
												}
											}
										});

							});
			// --------------------------------------

			// handle openFolderButton
			$('#openFolderBtn').click(function(e) {
				$.droidmate.ajax.post.openReportFolder();
			});
			// --------------------------------------

		});
