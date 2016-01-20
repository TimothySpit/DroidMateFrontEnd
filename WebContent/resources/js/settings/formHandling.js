define([ 'jquery', 'jquery.droidmate.overlays', 'jquery.droidmate.ajax'], function(require) {

	function saveSettings(outputPath, dmPath, explorationTime) {
		$.droidmate.ajax.post.saveDroidMateSettings(outputPath,dmPath, explorationTime, true, function(result) {
			var infobox = $('#saveinfo-box');
			if (result.success) {
				$.droidmate.overlays.alert(
						'<span><strong>Success!</strong> All data were saved.</span>',
						$.droidmate.overlays.alertTypes.SUCCESS, 2000);
			} else {
				$.droidmate.overlays.alert(
						"<span><strong>Error!</strong> "
								+ result.reason + "</span>",
								$.droidmate.overlays.alertTypes.DANGER, 2000);
			}
		});
	}

	$('#save-button').on('click', function(e) {
		var outputPath = $('#output-folder-name').val();
		var droidmatePath = $('#dm-output-folder-name').val();
		var explorationTime = $('#explorationTime').val();
		saveSettings(outputPath, droidmatePath, explorationTime);
	});

	//fill in textboxes
	var settings = $.droidmate.ajax.get.getDroidMateSettings();
	$('#output-folder-name').val(settings.outputPath);
	$('#dm-output-folder-name').val(settings.droidmatePath);
	$('#explorationTime').val(settings.time);
});
