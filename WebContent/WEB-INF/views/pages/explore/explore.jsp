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
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/explore/style.css">
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
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/explore/updateExplorationData.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/explore/handleButtons.js"></script>
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
					<button class="btn btn-default" id="back-to-index" type="button"
						id="returnStart">Return to start</button>
				</form>
			</div>
			<div class="col-sm-4 text-center">
				<form>
					<a href="${pageContext.request.contextPath}/ApkListingDynamic"><button
							class="btn btn-default" type="button" id="apkInfoBtn">Show
							details for .apks</button></a>
				</form>
			</div>
			<div class="col-sm-4">
				<form class="pull-right">
					<button id="stopAllBtn" class="btn btn-default" type="button">Stop
						All</button>
				</form>
			</div>
		</div>
		<div class="row apk-data hide">
			<table id="exploreFiles" class="display">
				<thead>
					<tr>
						<th></th>
						<th>Name</th>
						<th>Elements seen/Screens seen</th>
						<th>Status</th>
					</tr>
				</thead>
			</table>

			<div class="row">
				<div class="col-sm-4">
					<h3>
						<button id="openFolderBtn" class="btn btn-default pull-left"
							type="button">Open output folder</button>
						&nbsp; &nbsp; <span class="label label-default"
							id="outputPathLabel">Empty</span>
					</h3>
				</div>
				<div class="col-sm-4"></div>
				<div class="col-sm-4"></div>
			</div>
		</div>
		<div class="row">
			<div class="panel panel-default">
				<div class="panel-heading">Droidmate output</div>
				<div class="panel-body" id="droidmateOutputPanel"></div>
			</div>

		</div>
	</div>
	</main>
</body>

<jsp:include page="../../partials/footer.jsp" />

</html>