$(function() {
	// exploration button handler
	$('#back-to-index').click(function(e) {
		$.droidmate.ajax.post.stopDroidMate(function(e){window.location = "/DroidMate/Index";});
	});
});