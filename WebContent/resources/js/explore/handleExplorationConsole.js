$(function() {

	var currentLine = 0;

	function scrollDown() {
		$('#consoleOutput').animate({
			scrollTop : $('#consoleOutput').get(0).scrollHeight
		}, 200);
	};
	
	// update console
	function updateConsole() {
		// get console input
		var conoleOutput = $.droidmate.ajax.get.getConsoleOutput(currentLine);
		var consoleDiv = $('#consoleOutput');
		var text = conoleOutput.text;
		text = text.replace(/(?:\r\n|\r|\n)/g, '<br />');
		consoleDiv.append(text);
		currentLine += parseInt(conoleOutput.lines);

		// scroll down if scrollbar is already down
		if($('#console-output-scroll [type=checkbox]').prop('checked')) {
			scrollDown();
		}
		
		setTimeout(updateConsole, 1000);
	}

	updateConsole();
});