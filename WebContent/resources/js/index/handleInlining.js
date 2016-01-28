define([ 'require', 'jquery', 'jstree', '../index/apkFileInfoTable', 'jquery.droidmate.inlining', 'jquery.droidmate.overlays'], function(require) {
	
	var tableCreator = require('../index/apkFileInfoTable');
	var table = tableCreator.initModul($('#selectiontable'));
	
	// inline button handler
	$('#inline_files')
			.click(
					function(e) {
						// try to inline apks
						var inliningStartingResult = $.droidmate.inlining
								.startInlining();

						if (!inliningStartingResult) {
							// show error dialog
							$.droidmate.overlays
									.alert(
											"<strong>There was an error starting the inliner</strong>",
											$.droidmate.overlays.alertTypes.DANGER,
											2000);
							return;
						}

						
						// watch inliner status and update progress
						setTimeout(function() {
							$.droidmate.overlays
							.alert(
									"<strong>Inliner started. This could take a while.</strong>",
									$.droidmate.overlays.alertTypes.INFO);
							//disable buttons button
							$('#inline_files').prop('disabled',true);
							$('#startexploration').prop('disabled',true);
							watchInliner();
						}, $.droidmate.inlining.WATCH_INLINER_INTERVAL);

					});

	function watchInliner() {
		//Update table
		table.clear();
		var data = $.droidmate.ajax.get.getAllAPKS();
		$.each(data["info[]"].apks.data, function(index, value) {
			var inlinedStatus = table.inlinedStatus.INLINED;
			if (value.inlined) {
				inlinedStatus = table.inlinedStatus.INLINED;
			} else {
				switch (value.inliningStatus) {
				case $.droidmate.inlining.inliningStatus.NOT_STARTED:
				case $.droidmate.inlining.inliningStatus.FINISHED:
					inlinedStatus = table.inlinedStatus.NOT_INLINED;
					break;
				case $.droidmate.inlining.inliningStatus.INLINING:
					inlinedStatus = table.inlinedStatus.INLINING;
					break;
				case $.droidmate.inlining.inliningStatus.ERROR:
					inlinedStatus = table.inlinedStatus.ERROR;
					break;
				}
			}
			table.addAPKData(value.name, value.size, value.package,
					value.version, inlinedStatus, value.activityName);
			
			table.redraw();
		});
		
		var status = $.droidmate.inlining.getInliningStatus();
		if(status == $.droidmate.inlining.inliningStatus.FINISHED) {
			//finished
			$.droidmate.overlays.removeAllAlerts();
			$.droidmate.overlays
			.alert(
					"<strong>All files are inlined!</strong>",
					$.droidmate.overlays.alertTypes.SUCCESS, 4000);
			$('#inline_files').prop('disabled',false);
			$('#startexploration').prop('disabled',false);
		} else if (status == $.droidmate.inlining.inliningStatus.INLINING || status == $.droidmate.inlining.inliningStatus.NOT_STARTED) {
			//inlining
			setTimeout(function() {
				watchInliner();
			}, $.droidmate.inlining.WATCH_INLINER_INTERVAL);
		} else {
			//error
			$.droidmate.overlays.removeAllAlerts();
			$.droidmate.overlays
			.alert(
					"<strong>There was an error while inlining. Please check this and start again.</strong>",
					$.droidmate.overlays.alertTypes.DANGER);
			$('#inline_files').prop('disabled',false);
		}
	}
	
});