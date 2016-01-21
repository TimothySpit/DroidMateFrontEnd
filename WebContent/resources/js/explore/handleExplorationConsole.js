define([ 'require', 'jquery', '../explore/consoleOutput', 'jquery.droidmate.ajax'], function(require) {

	var consoleCreator = require('../explore/consoleOutput');
	var console = consoleCreator.initModul($('#console-output'));
	console.addClassNamesForHeading('panel-heading');
	console.addClassNamesForContent('panel-body');
	
	var currentLine = 0;

	// update console
	function updateConsole() {
		// get console input
		var conoleOutput = $.droidmate.ajax.get.getConsoleOutput(currentLine);
		console.addText(conoleOutput.text);
		currentLine += parseInt(conoleOutput.lines);

		setTimeout(updateConsole,
				$.droidmate.ajax.UPDATE_CONSOLE_OUTPUT_INTERVAL);
	}

	setTimeout(updateConsole, $.droidmate.ajax.UPDATE_CONSOLE_OUTPUT_INTERVAL);
});