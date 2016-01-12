$(function() {
	// exploration button handler
	$('#startexploration').click(function(e) {
		$.droidmate.ajax.post.startDroidMate();
		window.location = "/DroidMate/Explore";
	});
});