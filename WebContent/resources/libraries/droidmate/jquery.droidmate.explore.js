define([ 'jquery'], function(require) {
	//ajax object
	var droidmate = $.droidmate || {};
	var explore = {};
	
	//constants
	explore.UPDATE_EXPLORE_CHARTS_INTERVAL = 1000;
	//--------------------------------------
	
	
	droidmate.explore = explore;
	$.droidmate = droidmate;
	
});