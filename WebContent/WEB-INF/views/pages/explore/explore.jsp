<%@page import="java.util.function.Consumer"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../../partials/header.jsp" />
<!-- chart files -->
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
<script
	src="${pageContext.request.contextPath}/resources/js/explore/exploreCharts.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/explore/updateExplorationData.js"></script>

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
			<div class="col-sm-4">
				<form class="pull-left">
					<a href="${pageContext.request.contextPath}/Index">
						<button data-toggle="modal" class="btn btn-default" type="button"
							id="returnStart">Return to start</button>
					</a>
				</form>
			</div>
			<div class="col-sm-4 text-center">
				<form>
					<a href="${pageContext.request.contextPath}/ApkListingDynamic"><button
							class="btn btn-default" type="button" id="apkInfoBtn">Show
							details for selected .apks</button></a>
				</form>
			</div>
			<div class="col-sm-4">
				<form class="pull-right">
					<button class="btn btn-default" type="button">Stop All</button>
				</form>
			</div>
		</div>
		<div class="row apk-data hide">
			<table id="exploreFiles" class="display">
				<thead>
					<tr>
						<th>Name</th>
						<th>Progress</th>
						<th>Status</th>
					</tr>
				</thead>
			</table>

			<div class="row">
				<div class="col-sm-4">
					<button class="btn btn-default pull-left" type="button">Open
						output folder</button>
				</div>
				<div class="col-sm-4"></div>
				<div class="col-sm-4"></div>
			</div>
		</div>
	</div>
	</main>
</body>

<jsp:include page="../../partials/footer.jsp" />

</html>