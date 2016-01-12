<%@page import="java.util.function.Consumer"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../../partials/header.jsp" />
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
	src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.pie.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/flot-axislabels/jquery.flot.axislabels.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/explore/exploreCharts.js"></script>
</head>
</head>
<body class="container">
	<main>
	<div>
		<div class="row">
			<div class="col-md-12 main-heading">
				<h1 class="text-center">DroidMate</h1>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-6">
				<div id="flot-gui-elements-not-seen"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
			<div class="col-sm-6">
				<div id="flot-gui-elements-explored"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-6">
				<div id="flot-apks-status"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
			<div class="col-sm-6">
				<div id="flot-gui-screens-explored"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12 text-center">
				<button type="submit" data-toggle="modal" data-target="#myModal"
					onclick="window.history.back()" class="btn btn-default pull-left"
					type="button" id="selectfolder">Back</button>
			</div>
		</div>
	</div>
	</main>
</body>

<!-- gui elements not yet seen chart -->
<script type="text/javascript">	
$(document).ready(function () {
	window.createChartGUIElementsToExplore("#flot-gui-elements-not-seen");
});
</script>

<!-- gui elements explored chart-->
<script type="text/javascript">	
$(document).ready(function () {
	window.createChartGUIElementsExplored("#flot-gui-elements-explored");
});
</script>

<!-- apkstatus chart-->
<script type="text/javascript">	
$(document).ready(function () {
window.createChartAPKStatus('#flot-apks-status');
});
</script>

<!-- gui screens explored chart-->
<script type="text/javascript">	
$(document).ready(function () {
	window.createChartGUIScreensExplored("#flot-gui-screens-explored");

});
</script>

<jsp:include page="../../partials/footer.jsp" />

</html>