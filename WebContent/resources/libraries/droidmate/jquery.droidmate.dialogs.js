define([ 'require', 'jquery', 'bootbox', 'jstree',
		'jquery.flot', 'jquery.flot.axislabels',
		'jquery.flot.tooltip', 'jquery.flot.excanvas', 'jquery.flot.navigate',
		'jquery.flot.symbol' ], 
		function(require, jquery, bootbox, jstree, flot, flotAxisLabels, flotTooltip,flotExcanvas, flotNax, flotSymbol) {

	var droidmate = $.droidmate || {};
	var dialogs = {};

	var standardModalSettings = function(title, buttons) {

		this.title = title;
		this.message = '<div class="dialog-container"></div>';
		this.buttons = {
			success : buttons
		};
		this.show = false;
	};

	// file dialog modal
	function createFileDialog(title, callback) {
		var cb = callback || function() {
		};

		var buttons = {
			label : "Select",
			className : "btn-success",
			callback : function() {
			}
		};
		var settings = new standardModalSettings(title, buttons);
		settings.className = 'file-dialog';

		var dialog = bootbox.dialog(settings);
		var dialogContainer = $(dialog).find(".dialog-container");
		dialogContainer.jstree({
			'core' : {
				'data' : {
					url : "FileSystemHandler",
					type : 'POST',
					data : function(node) {
						if (node.text) {
							var path = "";
							dialogContainer.find('#' + node.id).parents("li")
									.each(
											function() {
												path = $(this).children("a")
														.text()
														+ path;
											});

							return {
								path : path + node.text,
								type : "directory"
							};
						} else
							return {
								path : "root",
								type : "directory"
							};
					}
				}
			}
		});

		$(dialog).find(".btn-success").on(
				'click',
				function() {
					// get selected items
					var selectedNodes = $(dialog).find(".dialog-container")
							.jstree(true).get_selected(true);

					// no item has been selected
					if (!selectedNodes.length) {
						return;
					}

					// get first selected item
					var firstItem = selectedNodes[0];

					var path = "";

					// calculate path up to root node
					dialogContainer.find('#' + firstItem.id).parents("li")
							.each(function() {
								path = $(this).children("a").text() + path;
							});

					path = path + firstItem.text;

					cb(path);
				});
		dialog.modal('show');
	}
	dialogs.createFileDialog = createFileDialog;

	// file size histogram
	function createFileSizeHistogramDialog(title, fileNames, sizesMB,
			dialogWidth, dialogHeight, callback) {
		var cb = callback || function() {
		};

		var sizeMapping = $.map(sizesMB, function(val, i) {
			return [ [ i, val ] ];
		});

		var nameMapping = $.map(fileNames, function(val, i) {
			return [ [ i, val ] ];
		});

		var dataset = [ {
			label : "File Sizes (MB)",
			data : sizeMapping,
			color : "#5482FF"
		} ];

		var options = {
			series : {
				bars : {
					show : true
				}
			},
			bars : {
				align : "center",
				barWidth : 0.5
			},
			legend : {
				noColumns : 0,
				labelBoxBorderColor : "#000000",
				position : "ne"
			},
			grid : {
				hoverable : true,

			},
			tooltip : {
				show : true,
				content : function(label, xval, yval, flotItem) {
					return fileNames[xval];
				},
			},
			xaxis : {
				axisLabel : "APK",
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 12,
				axisLabelFontFamily : 'Verdana, Arial',
				axisLabelPadding : 25,
				tickColor : "#5E5E5E",
				color : "black"
			},
			yaxis : {
				axisLabel : "Size",
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 12,
				axisLabelFontFamily : 'Verdana, Arial',
				axisLabelPadding : 3,
				axisLabelPadding : 10,
				tickColor : "#5E5E5E",
				color : "black"
			}
		};

		var buttons = {
			label : "Close",
			className : "btn-success",
			callback : function() {
			}
		};
		var settings = new standardModalSettings(title, buttons);
		settings.className = 'file-size-dialog';

		var dialog = bootbox.dialog(settings);
		var container = $(dialog).find(".dialog-container");
		container.width(dialogWidth);
		container.height(dialogHeight);

		flot(container, dataset, options);

		$(dialog).find(".btn-success").on('click', function() {
			cb();
		});

		dialog.modal('show');
	}
	dialogs.createFileSizeHistogramDialog = createFileSizeHistogramDialog;

	function createOKTextDialog(title, message, callback) {
		var cb = callback || function() {
		};
		var buttons = {
			label : "Close",
			className : "btn-success",
			callback : function() {
			}
		};
		var settings = new standardModalSettings(title, buttons);
		settings.className = 'ok-text-dialog';
		settings.message = message;

		var dialog = bootbox.dialog(settings);
		dialog.modal('show');
	}
	dialogs.createOKTextDialog = createOKTextDialog;

	droidmate.dialogs = dialogs;
	$.droidmate = droidmate;
	
	return {
		createFileDialog : dialogs.createFileDialog,
		createFileSizeHistogramDialog : dialogs.createFileSizeHistogramDialog,
		createOKTextDialog : dialogs.createOKTextDialog,
	};
	
})