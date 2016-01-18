$(function() {
	
	var currentLine = 0;
	
	//update console
	function updateConsole() {
		//get console input
		
		var conoleOutput = $.droidmate.ajax.get.getConsoleOutput(currentLine);
		var consoleDiv = $('#consoleOutput');
		var text = conoleOutput.text;
		text = text.replace(/(?:\r\n|\r|\n)/g, '<br />');
		consoleDiv.append(text);
		currentLine += parseInt(conoleOutput.lines);
		
		setTimeout(updateConsole, 1000);
	}
	
	setTimeout(updateConsole, 1000);
});