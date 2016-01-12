$(function() {
	// exploration button handler
	$('#startexploration').click(function(e) {
		$.droidmate.ajax.post.startDroidMate(function(e){window.location = "/DroidMate/Explore";});
	});
});
$(function() {
	//
	$('#stopAllBtn').click(function(e) {
		$.droidmate.ajax.post.stopDroidMate();
	});
});