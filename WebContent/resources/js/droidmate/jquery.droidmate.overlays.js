(function($) {
	//ajax object
	var droidmate = $.droidmate || {};
	var overlays = {};
	
	//alert types
	var alertTypes = {
			SUCCESS : "alert-success",
			INFO	: "alert-info",
			WARNING	: "alert-warning",
			DANGER	: "alert-danger"
	};
	overlays.alertTypes = alertTypes;
	//-------------------------------------------------------------------------
	
	//info window
	function alert(message, alerttype, timeout) {
		var res = $('<div class="alert ' + alerttype
				+ '"><a class="close" data-dismiss="alert">&times;</a><span>'
				+ message + '</span></div>');
		var parent = $('div#droidMate-alert-container');
		if(!parent.length) {
			parent = $('<div id="droidMate-alert-container">');
			$('body').append(parent);
		}
		
		parent.append(res);
		if (timeout || timeout === 0) {
			setTimeout(function() {
				res.fadeTo(500, 0, function() {
					$(this).remove();
				});
			}, timeout);
		}
		return res;
	}
	
	overlays.alert = alert;
	//-------------------------------------------------------------------------
	
	//removes all alerts
	function removeAllAlerts() {
		var parent = $('div#droidMate-alert-container');
		parent.empty();
	}
	overlays.removeAllAlerts = removeAllAlerts;
	//-------------------------------------------------------------------------
	
	droidmate.overlays = overlays;
	$.droidmate = droidmate;
	
})(jQuery);