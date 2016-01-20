$(function() {
	// fileSizeInformation Modal dialog
	$('#show-static').on('click', function(e) {
		$.ajax({
			type : "GET",
			contentType : 'application/json; charset=utf-8',
			dataType : 'json',
			url : '/DroidMate/ApkListingStatic',
			error : function() {
				console.log("An error occurred.");
			},
			success : function(data) {
				var dataset = [ {
					label : "File Sizes (MB)",
					data : data.data,
					color : "#5482FF"
				} ];

				var options = {
					series : {
						bars : {
							show : true
						}
					},
					bars : {
						align : "center",
						barWidth : 0.5
					},
					legend : {
						noColumns : 0,
						labelBoxBorderColor : "#000000",
						position : "nw"
					},
					grid : {
						hoverable : true,

					},
					tooltip : {
						show : true,
						content : function(label, xval, yval, flotItem) {
							return data.ticks[xval][1];
						},
					}
				};

				$('#staticinfomodal').on('shown.bs.modal', function() {
					$.plot($("#fileSizes"), dataset, options);
				})
			}
		});
	});
	// ------------------------------------------
})