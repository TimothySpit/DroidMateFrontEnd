$(function() {
	//
	$('#stopAllBtn').click(function(e) {
		$.droidmate.ajax.post.stopDroidMate();
	});
});