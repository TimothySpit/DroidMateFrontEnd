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
													label : "Back to index",
													className : "btn-danger pull-left"
												},
												confirm : {
													label : "Cancel",
													className : "btn-default pull-right"
												}
											},
											callback : function(result) {
												// Gray out the button
												$(this).prop("disabled", true);
												$.droidmate.ajax.post
														.stopDroidMate(function(
																e) {
															window.location = "/DroidMate/Index";
														});
											}
										});

							});
			// --------------------------------------

			// handle openFolderButton
			$('#openFolderBtn').click(function(e) {
				$.droidmate.ajax.post.openReportFolder();
			});
			// --------------------------------------

			$(document).ready( function() {

				var startingTimestamp = null;
				function updateClock() {
					console.log("ready!");
					if (startingTimestamp == null) {
						startingTimestamp = $.droidmate.ajax.get.getGlobalStartingTime();
						console.log(startingTimestamp);
					} else {
						var passedSeconds = Math
								.floor((Date.now() - startingTimestamp) / 1000);
						var seconds = 0, minutes = 0;
						if (passedSeconds >= 60) {
							seconds = passedSeconds % 60;
							minutes = (passedSeconds - seconds) / 60;
						} else {
							seconds = passedSeconds;
						}
						$("#timeLabel").text(
								(minutes < 10 ? "0" : "") + minutes + ":"
										+ (seconds < 10 ? "0" : "") + seconds);
					}
				}
				;
				var clockInterval = setInterval(updateClock, 1000);
			});

		});
