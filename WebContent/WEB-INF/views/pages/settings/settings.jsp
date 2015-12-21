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
<!-- file tree start-->
<link rel="stylesheet"
	href="//cdnjs.cloudflare.com/ajax/libs/jstree/3.0.9/themes/default/style.min.css" />
<script
	src="//cdnjs.cloudflare.com/ajax/libs/jstree/3.0.9/jstree.min.js"></script>
<!-- file tree end -->
</head>
<body class="container">
	<main>
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
					name="filebrowser" type="text" placeholder="Select folder"
					onfocus="this.blur()" readonly />
			</div>
			<div class="col-sm-4">
				<button type="button" class="btn btn-default btn-default">
					<span class="glyphicon glyphicon-folder-open"></span>
				</button>
			</div>
		</div>
		<div class="row input-row">
			<div class="col-sm-4">
				<label class="pull-right" for="filebrowser">Exploration
					Timeout (min):</label>
			</div>
			<div class="col-sm-4">
				<input class="form-control" id="output-folder-name"
					name="filebrowser" type="text" placeholder="Select folder" />
			</div>
			<div class="col-sm-4"></div>
		</div>
		<div class="row bottom-navi">
			<div class="col-sm-4">
				<form action="${pageContext.request.contextPath}/index">
					<button class="btn btn-default btn-default">
						<span class="glyphicon glyphicon-circle-arrow-left"></span> Back
					</button>
				</form>
			</div>
			<div class="col-sm-4"></div>
			<div class="col-sm-4">
				<button type="button" class="btn btn-default btn-default pull-right">
					<span class="glyphicon glyphicon-floppy-disk"></span> Save changes
				</button>
			</div>
		</div>

	</div>
	</main>
</body>

<jsp:include page="../../partials/footer.jsp" />

</html>