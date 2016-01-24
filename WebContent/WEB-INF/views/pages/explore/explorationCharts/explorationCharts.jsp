<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>

<jsp:include page="../../../partials/header.jsp" />

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/explore/style.css">

<!-- requirejs -->
<script
	data-main="${pageContext.request.contextPath}/resources/js/explore/explorationCharts/explorationCharts.js"
	src="${pageContext.request.contextPath}/resources/libraries/requirejs/require.js"></script>

</head>
<body class="container">
	<div>
		<div class="row">
			<div class="col-md-12 main-heading">
				<h1 class="text-center">DroidMate</h1>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-6">
				<div id="flot-gui-elements-seen"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
			<div class="col-sm-6">
				<div id="flot-gui-screens-explored"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-6">
				<div id="flot-apks-status"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
			<div class="col-sm-6">
				<div id="flot-gui-elements-explored"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<fieldset id="legend">
					<legend>APK chart selection</legend>
					<div id="apks-charts-legend"></div>
				</fieldset>
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
</body>

<jsp:include page="../../../partials/footer.jsp" />

</html>