define(
		[ 'require', 'jquery', 'bootbox', 'jquery.droidmate.ajax', 'jquery.droidmate.overlays' ],
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
															.returnToIndex(function(
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
				if($.droidmate.ajax.post.openReportFolder(false)) {
					$.droidmate.overlays.alert("Explorer process has been started.", $.droidmate.overlays.alertTypes.SUCCESS, 4000);
				}else {
					$.droidmate.overlays.alert("Explorer process could not be started!", $.droidmate.overlays.alertTypes.DANGER, 6000);
				}
			});
			// --------------------------------------

			$(document).ready( function() {

				var startingTimestamp = null;
				function updateClock() {
					if (startingTimestamp == null) {
						startingTimestamp = $.droidmate.ajax.get.getGlobalStartingTime();
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
