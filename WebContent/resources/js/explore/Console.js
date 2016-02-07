define([ 'require', 'jquery', 'jquery.droidmate.ajax' ], function(require) {

	var divID = null;

	var scrollUpdater = null;
	var scrollUpdaterInterval = 1000; // default

	function createNeededComponents(divID, headingText, cbText) {
		function createDivs(divToAppend) {
			// create needed divs
			var heading = $('<div>');
			heading.append('<span>' + headingText + '</span>');
			divToAppend.append(heading);
			heading.addClass('console-heading');

			var cb = $('<input type="checkbox" />');
			heading.append(cb);
			cb.wrap('<label></label>');
			cb.parent().addClass('console-cb');
			cb.parent().prepend('<span class="scroll-text">'+ cbText +'</span>');
			var content = $('<div>');
			content.css('overflow', 'scroll');
			content.css('height', '500px');

			divToAppend.append(content);
			content.addClass('console-content');

			return {
				heading : {
					head : heading,
					cb : cb
				},
				content : content
			};
		}

		var heading = null;
		var content = null;
		var cb = null;
		if (divID.children().length === 0) {
			var divs = createDivs(divID);
			heading = divs.heading.head;
			cb = divs.heading.cb;
			content = divs.content;
		} else {
			if (divID.find('.console-heading')
					&& divID.find('.console-content')) {
				heading = divID.find('.console-heading');
				cb = divID.find('.console-cb');
				content = divID.find('.console-content');
			} else {
				divID.empty();
				var divs = createDivs(divID);
				heading = divs.heading.head;
				cb = divs.heading.cb;
				content = divs.content;
			}
		}

		return {
			heading : {
				head : heading,
				cb : cb
			},
			content : content
		};
	}

	function removeStadardEvents(divs) {
		divs.heading.cb.off('scroll');
		divs.heading.cb.off('change');
	}

	function scrollDown(content) {
		content.stop(true,true);
		content.animate({
			scrollTop : content.get(0).scrollHeight
		}, 200);
	}

	function updateConsole(divs) {
		if (divs.heading.cb.prop('checked')) {
			scrollDown(divs.content);
		}

		scrollUpdater = setTimeout(function() {
			updateConsole(divs);
		}, scrollUpdaterInterval);
	}

	function addStandardEvents(divs) {
		// initially checked
		divs.heading.cb.prop('checked', true);

		divs.content.bind('wheel mousedown mousewheel keyup', function(evt) {
			divs.heading.cb.prop('checked', false);
			divs.content.stop(true,true);
		});
		divs.heading.cb.change(function() {
			if ($(this).is(":checked")) {
				scrollDown(divs.content);
			}
		});

		scrollUpdater = setTimeout(function() {
			updateConsole(divs);
		}, scrollUpdaterInterval)
	}

	function addStandardMethods(modul, divs) {
		modul.addText = function(newText) {
			if (newText == "")
				return;

			newText = newText.replace(/(?:\r\n|\r|\n)/g, '<br />');
			divs.content.append(newText);
			if (divs.heading.cb.prop('checked')) {
				scrollDown(divs.content);
			}
		}

		modul.setText = function(newText) {
			if (newText == "")
				return;

			newText = newText.replace(/(?:\r\n|\r|\n)/g, '<br />');
			divs.content.html(newText);
			if (divs.heading.cb.prop('checked')) {
				scrollDown(divs.content);
			}
		}
		
		modul.clear = function() {
			divs.content.empty();
		}

		modul.addClassNamesForCheckBox = function(classNames) {
			divs.heading.cb.addClass(classNames);
		}

		modul.addClassNamesForHeading = function(classNames) {
			divs.heading.head.addClass(classNames);
		}

		modul.addClassNamesForContent = function(classNames) {
			divs.content.addClass(classNames);
		}
	}

	function removeEventListeners(divs) {

	}

	function addEventListeners(modul, divs) {

	}

	function modul(divID, divs) {
		// remove existing event handlers
		removeStadardEvents(divs);

		// add event listener to console
		addStandardEvents(divs);

		// create modul
		var modul = {};
		addStandardMethods(modul, divs);

		// remove existing event handlers
		removeEventListeners(divs);

		// add custom event listeners
		addEventListeners(modul, divs);

		return modul;
	}

	var init = function(divIDentifier, headingText, checkboxText) {
		headingText = headingText || "Console Output";
		checkboxText = checkboxText || "Auto scroll";

		// create new console
		divID = divIDentifier;

		var divs = createNeededComponents(divID, headingText, checkboxText);

		return modul(divID, divs);
	}

	return {
		initModul : init
	};

})