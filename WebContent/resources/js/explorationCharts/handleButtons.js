define([ 'require',
		'jquery.droidmate.overlays','jquery.droidmate.explore','jquery.droidmate.ajax'], function(require) {

	//handle back button
	$('#button-back').click(function() {
		window.location = "Explore";
	});

});