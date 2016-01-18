$(document)
		.ready(
				function() {
					// Start reading droidmate output
					var source = new EventSource(
							'/DroidMate/APKExploreHandler?explore_get_droidmate_output=true');
					source.addEventListener('message', function(e) {
						$("#droidmateOutputPanel").html(e.data);
					}, false);

					// Set output path label
					var settings = $.droidmate.ajax.get.getDroidMateSettings();
					console.log($.droidmate.ajax.get.getDroidMateSettings());
					$("#outputPathLabel").html(settings["outputPath"]);
				});

$(function() {
	//
	$('#stopAllBtn').click(function(e) {
		$.droidmate.ajax.post.stopDroidMate();
	});
});
$(function() {
	// exploration button handler
	$('#back-to-index').click(function(e) {
		$(this).prop("disabled", true);
		$.droidmate.ajax.post.stopDroidMate(function(e) {
			window.location = "/DroidMate/Index";
		});
	});
});
$(function() {
	$('#openFolderBtn').click(function(e) {
		$.droidmate.ajax.post.openReportFolder();
	});
});
$(function() {
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
});
