$(function() {
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
							//disable inline button
							$('#inline_files').prop('disabled',true);
							watchInliner();
						}, $.droidmate.inlining.WATCH_INLINER_INTERVAL);

					});

	function watchInliner() {
		//Update table
		$('#selectiontable').DataTable().ajax.reload();
		
		var status = $.droidmate.inlining.getInliningStatus();
		if(status == $.droidmate.inlining.inliningStatus.FINISHED) {
			//finished
			$.droidmate.overlays.removeAllAlerts();
			$.droidmate.overlays
			.alert(
					"<strong>All files are inlined!</strong>",
					$.droidmate.overlays.alertTypes.SUCCESS, 4000);
			$('#inline_files').prop('disabled',false);
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