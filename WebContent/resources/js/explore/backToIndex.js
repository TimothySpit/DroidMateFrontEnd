$(function() {
	// exploration button handler
	$('#back-to-index').click(function(e) {console.log("da");
		$(this).prop("disabled",true);
		$.droidmate.ajax.post.stopDroidMate(function(e){window.location = "/DroidMate/Index";});
	});
});