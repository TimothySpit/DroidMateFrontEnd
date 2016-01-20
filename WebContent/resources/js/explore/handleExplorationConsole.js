$(function() {

	var currentLine = 0;

	$('#console-output-scroll [type=checkbox]').prop('checked', true);

	function scrollDown() {
		$('#consoleOutput').animate({
			scrollTop : $('#consoleOutput').get(0).scrollHeight
		}, 200);
	}
	;

	$('#consoleOutput').scroll(function(e) {
		if ($(this).is(':animated')) {
			// scroll happen animate scroll
		} else if (e.originalEvent) {
			// scroll happen manual scroll
			$('#console-output-scroll [type=checkbox]').prop('checked', false);
		} else {
			// scroll happen by call
		}
	});
	$('#console-output-scroll [type=checkbox]').change(function() {
		if ($(this).is(":checked")) {
			scrollDown();
		}
	});

	// update console
	function updateConsole() {
		// get console input
		var conoleOutput = $.droidmate.ajax.get.getConsoleOutput(currentLine);
		var consoleDiv = $('#consoleOutput');
		var text = conoleOutput.text;
		text = text.replace(/(?:\r\n|\r|\n)/g, '<br />');
		consoleDiv.append(text);
		currentLine += parseInt(conoleOutput.lines);

		// scroll down if scrollbar is not already down
		if ($('#console-output-scroll [type=checkbox]').prop('checked')) {
			scrollDown();
		}

		setTimeout(updateConsole,
				$.droidmate.ajax.UPDATE_CONSOLE_OUTPUT_INTERVAL);
	}

	setTimeout(updateConsole, $.droidmate.ajax.UPDATE_CONSOLE_OUTPUT_INTERVAL);
});