$(function() {
	// exploration button handler
	$('#startexploration').click(
		 	function(e) {
				$.droidmate.ajax.post.setSelectedAPKS(rows_selected, false,
						function(data) {
							window.location = "/DroidMate/Explore";
						});
			});
});