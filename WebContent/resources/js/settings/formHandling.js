$(function() {

	function saveSettings(outputPath, explorationTime) {
		$.get("Settings", {save:true,path:outputPath,time:explorationTime},function(data) {
			var result = JSON.parse(data);
			if(result.success) {
				$('#result-indikator').text("Success!");
				$('#result-indikator').addClass("label-success");
				$('#result-indikator').removeClass("label-danger");
			} else {
				$('#result-indikator').text("Error! " + result.reason);
				$('#result-indikator').removeClass("label-success");
				$('#result-indikator').addClass("label-danger");
			}
		});
	}

	$('#save-button').on('click', function(e) {
		var outputPath = $('#output-folder-name').val();
		var explorationTime = $('#explorationTime').val();
		saveSettings(outputPath, explorationTime);
	});

})
