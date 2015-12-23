$(function() {

	function saveSettings(outputPath, explorationTime) {
		$
				.get(
						"Settings",
						{
							save : true,
							path : outputPath,
							time : explorationTime
						},
						function(data) {
							var result = JSON.parse(data);
							var infobox = $('#saveinfo-box');
							if (result.success) {
								bootstrap_alert(
										infobox,
										'<span><strong>Success!</strong> All data were saved.</span>',
										'alert-success', 2000);
							} else {
								bootstrap_alert(infobox,
										"<span><strong>Error!</strong> "
												+ result.reason + "</span>",
										'alert-danger', 2000);
							}
						});
	}

	$('#save-button').on('click', function(e) {
		var outputPath = $('#output-folder-name').val();
		var explorationTime = $('#explorationTime').val();
		saveSettings(outputPath, explorationTime);
	});

	function bootstrap_alert(elem, message, alerttype, timeout) {
		var res = $('<div id="alertdiv" class="alert ' + alerttype
				+ '"><a class="close" data-dismiss="alert">×</a><span>'
				+ message + '</span></div>')
		elem.append(res);
		if (timeout || timeout === 0) {
			setTimeout(function() {
				res.fadeTo(500, 0, function() {
					$(this).remove();
				});
			}, timeout);
		}
	}
	;
});

//fill edit boxes
$(function() {
	$.get( "Settings", {get:["path","time"],}, function( data ) {
		var json = JSON.parse(data);
		  $('#output-folder-name').val(json.path);
		  $('#explorationTime').val(json.time);
		});
	
});
