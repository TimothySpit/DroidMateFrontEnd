define([ 'jquery', 'jquery.droidmate.ajax'], function(require) {

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
	$('#back-to-index').click(function(e) {
		$(this).prop("disabled", true);
		$.droidmate.ajax.post.stopDroidMate(function(e) {
			window.location = "/DroidMate/Index";
		});
	});
	// --------------------------------------

	//handle openFolderButton
	$('#openFolderBtn').click(function(e) {
		$.droidmate.ajax.post.openReportFolder();
	});
	// --------------------------------------

});
