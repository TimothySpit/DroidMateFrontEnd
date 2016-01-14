<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<script
	src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.time.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.canvas.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.symbol.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/flot/excanvas.min.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/flot-tickrotor/jquery.flot.tickrotor.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/flot-tooltip/jquery.flot.tooltip.min.js"></script>
<div id="staticinfomodal" class="modal fade" role="dialog">
	<div class="modal-dialog">

		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">Static info</h4>
			</div>
			<div class="modal-body">
				<div id="fileSizes"
					style="width: 450px; height: 400px; margin: 0 auto"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>

	</div>
</div>

<script>
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
					tooltip: {
					    show:           true ,  
					    content:        function(label, xval, yval, flotItem) {return data.ticks[xval][1];},
					}
				};
				
				$('#staticinfomodal').on('shown.bs.modal', function() {
					$.plot($("#fileSizes"), dataset, options);
				})
			}
		});
	});
</script>