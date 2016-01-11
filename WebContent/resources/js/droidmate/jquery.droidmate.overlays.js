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
	function alert(parent, message, alerttype, timeout) {
		var res = $('<div id="alertdiv" class="alert ' + alerttype
				+ '"><a class="close" data-dismiss="alert">&times;</a><span>'
				+ message + '</span></div>')
		parent.append(res);
		if (timeout || timeout === 0) {
			setTimeout(function() {
				res.fadeTo(500, 0, function() {
					$(this).remove();
				});
			}, timeout);
		}
	}
	
	overlays.alert = alert;
	//-------------------------------------------------------------------------
	
	droidmate.overlays = overlays;
	$.droidmate = droidmate;
	
})(jQuery);