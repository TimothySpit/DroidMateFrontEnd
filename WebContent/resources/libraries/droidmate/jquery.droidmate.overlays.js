define([ 'jquery'], function(require) {
	//ajax object
	var droidmate = $.droidmate || {};
	var overlays = {};
	
	var ERROR_MESSAGE_TIMEOUT = 5000; //milliseconds
	
	overlays.ERROR_MESSAGE_TIMEOUT = ERROR_MESSAGE_TIMEOUT;
	
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
	
});