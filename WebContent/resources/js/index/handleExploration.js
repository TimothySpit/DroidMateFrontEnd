define([ 'jquery', 'jstree', 'jquery.droidmate.ajax' ], function(require) {
	// exploration button handler
	$('#startexploration').click(function(e) {
		$.droidmate.ajax.post.startDroidMate(function(e) {
			window.location = "/DroidMate/Explore";
		});
	});
});