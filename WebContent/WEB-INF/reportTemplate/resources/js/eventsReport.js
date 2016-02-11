$(function() {
	// init table
	var table = $.tableInit($('#table-apk-exploration-info'));
	// init console
	var console = $.consoleInit($('#div-console-output'));
	console.addClassNamesForHeading('panel-heading');
	console.addClassNamesForContent('panel-body');

	// fill table
	$.each($.APKData.apks, function(index, value) {
		// collect data
		var explorationInfo = value.explorationInfo

		var name = value.name;
		var timeSeconds = explorationInfo.timeSeconds;
		var elementsSeen = explorationInfo.elementsSeen;
		var screensSeen = explorationInfo.screensSeen;
		var widgetsClicked = explorationInfo.widgetsExplored;
		var status = value.explorationStatus;

		// row need to be initialized
		row = table.addAPKData(name, timeSeconds + "s", elementsSeen,
				screensSeen, widgetsClicked, status);

		// set chart data
		row.setElementsSeenChartData(explorationInfo.historyElements);
		row.setScreensSeenChartData(explorationInfo.historyScreens);
		row.setWidgetsExploredChartData(explorationInfo.historyWidgets);

		table.redraw();
	});

	// handle show details for all apks button
	$('#button-show-apk-details-dynamic').click(function() {
		window.location = "resources/pages/explorationCharts.html";
	});

	// set console output
	console.setText($.APK_CONSOLE_DATA);
});