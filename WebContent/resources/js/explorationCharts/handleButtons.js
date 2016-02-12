define([ 'require', 'jquery'], 
		function(require, jquery) {

	//handle back button
	jquery('#button-back').click(function() {
		window.location = "Explore";
	});

});