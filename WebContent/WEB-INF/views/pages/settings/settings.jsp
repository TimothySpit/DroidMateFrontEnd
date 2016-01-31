<%@page import="java.util.function.Consumer"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.List"%>
<%@page import="com.droidmate.settings.GUISettings"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../../partials/header.jsp" />

<!-- requirejs -->
<script
	data-main="${pageContext.request.contextPath}/resources/js/settings/settings.js"
	src="${pageContext.request.contextPath}/resources/libraries/requirejs/require.js"></script>

</head>
<body class="container">
	<div>
		<div class="row">
			<div class="col-md-12 main-heading">
				<h1 class="text-center">Settings</h1>
			</div>
		</div>
		<div class="row input-row">
			<div class="col-sm-4">
				<label class="pull-right" for="filebrowser">Output folder:</label>
			</div>
			<div class="col-sm-4">
				<input class="form-control" id="output-folder-name"
					name="filebrowser" type="text" placeholder="Select folder" />
			</div>
			<div class="col-sm-4">
				<button type="button" id="output-folder-btn" class="btn btn-default btn-default">
					<span class="glyphicon glyphicon-folder-open"></span>
				</button>
			</div>
		</div>

		<div class="row input-row">
			<div class="col-sm-4">
				<label class="pull-right" for="droidmate-folder-name">DroidMate
					Path:</label>
			</div>
			<div class="col-sm-4">
				<input class="form-control" id="droidmate-folder-name"
					name="droidMatePath" type="text" placeholder="DroidMate Path" />
			</div>
			<div class="col-sm-4">
				<button type="button" class="btn btn-default btn-default" id="droidmate-folder-name-btn">
					<span class="glyphicon glyphicon-folder-open"></span>
				</button>
			</div>
		</div>

		<div class="row input-row">
			<div class="col-sm-4">
				<label class="pull-right" for="aapt-folder-name">AAPT
					Path:</label>
			</div>
			<div class="col-sm-4">
				<input class="form-control" id="aapt-folder-name"
					name="aaptPath" type="text" placeholder="AAPT Path" />
			</div>
			<div class="col-sm-4">
				<button type="button" class="btn btn-default btn-default" id="aapt-folder-name-btn">
					<span class="glyphicon glyphicon-folder-open"></span>
				</button>
			</div>
		</div>

		<div class="row input-row">
			<div class="col-sm-4">
				<label class="pull-right" for="explorationTime">Exploration
					Timeout (sec):</label>
			</div>
			<div class="col-sm-4">
				<input class="form-control" id="explorationTime"
					name="explorationTime" type="text" placeholder="Timeout" />
			</div>
			<div class="col-sm-4"></div>
		</div>


		<div class="row bottom-navi">
			<div class="col-sm-4">
				<form action="${pageContext.request.contextPath}/Index">
					<button class="btn btn-default btn-default">
						<span class="glyphicon glyphicon-circle-arrow-left"></span> Back
					</button>
				</form>
			</div>
			<div class="col-sm-4"></div>
			<div class="col-sm-4">
				<button type="button" name="save-button" id="save-button"
					class="btn btn-default btn-default pull-right">
					<span class="glyphicon glyphicon-floppy-disk"></span> Save changes
				</button>
			</div>
		</div>

	</div>
</body>

<jsp:include page="../../partials/footer.jsp" />

</html>