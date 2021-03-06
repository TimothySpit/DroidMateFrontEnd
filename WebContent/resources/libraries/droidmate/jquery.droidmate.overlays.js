define([ 'require', 'jquery'], function(require, jquery) {
	//ajax object
	var droidmate = jquery.droidmate || {};
	var overlays = {};
	
	var DANGER_MESSAGE_TIMEOUT = 8000; //milliseconds
	var INFO_MESSAGE_TIMEOUT = 5000;
	var WARNING_MESSAGE_TIMEOUT = 6000;
	var SUCCESS_MESSAGE_TIMEOUT = 6000;
	
	overlays.DANGER_MESSAGE_TIMEOUT = DANGER_MESSAGE_TIMEOUT;
	overlays.INFO_MESSAGE_TIMEOUT = INFO_MESSAGE_TIMEOUT;
	overlays.WARNING_MESSAGE_TIMEOUT = WARNING_MESSAGE_TIMEOUT;
	overlays.SUCCESS_MESSAGE_TIMEOUT = SUCCESS_MESSAGE_TIMEOUT;
	
	//alert types
	var alertTypes = {
			SUCCESS : "alert-success",
			INFO	: "alert-info",
			WARNING	: "alert-warning",
			DANGER	: "alert-danger"
	};
	//-------------------------------------------------------------------------
	
	function success(message, timeout) {
		alert(message,alertTypes.SUCCESS, timeout);
	}
	overlays.success = success;
	
	function info(message, timeout) {
		alert(message,alertTypes.INFO, timeout);
	}
	overlays.info = info;
	
	function warning(message, timeout) {
		alert(message,alertTypes.WARNING, timeout);
	}
	overlays.warning = warning;
	
	function danger(message, timeout) {
		alert(message,alertTypes.DANGER, timeout);
	}
	overlays.danger = danger;
	
	//info window
	function alert(message, alerttype, timeout) {
		var res = jquery('<div class="alert ' + alerttype
				+ '"><a class="close" data-dismiss="alert">&times;</a><span>'
				+ message + '</span></div>');
		var parent = jquery('div#droidMate-alert-container');
		if(!parent.length) {
			parent = jquery('<div id="droidMate-alert-container">');
			jquery('body').append(parent);
		}
		
		parent.append(res);
		if (timeout || timeout === 0) {
			setTimeout(function() {
				res.fadeTo(500, 0, function() {
					jquery(this).remove();
				});
			}, timeout);
		}
		return res;
	}
	
	//-------------------------------------------------------------------------
	
	//removes all alerts
	function removeAllAlerts() {
		var parent = jquery('div#droidMate-alert-container');
		parent.empty();
	}
	overlays.removeAllAlerts = removeAllAlerts;
	//-------------------------------------------------------------------------
	
	droidmate.overlays = overlays;
	jquery.droidmate = droidmate;
	
	return {
		DANGER_MESSAGE_TIMEOUT: overlays.DANGER_MESSAGE_TIMEOUT, 
		INFO_MESSAGE_TIMEOUT :overlays.INFO_MESSAGE_TIMEOUT,
		WARNING_MESSAGE_TIMEOUT:overlays.WARNING_MESSAGE_TIMEOUT,
		SUCCESS_MESSAGE_TIMEOUT:overlays.SUCCESS_MESSAGE_TIMEOUT,
		warning: overlays.warning,
		success: overlays.success,
		info: overlays.info,
		warning: overlays.warning,
		danger : overlays.danger,
		removeAllAlerts : overlays.removeAllAlerts,
	};
	
});