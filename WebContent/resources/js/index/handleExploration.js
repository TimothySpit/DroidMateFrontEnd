$(function() {
	// exploration button handler
	$('#startexploration').click(function(e) {
		$.droidmate.ajax.post.startDroidMate().done(function(e){window.location = "/DroidMate/Explore";});
		
	});
});