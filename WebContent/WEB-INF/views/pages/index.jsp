<%@page import="java.util.function.Consumer"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../partials/header.jsp" />
<script
	src="${pageContext.request.contextPath}/resources/js/jqueryFileTree.js"
	type="text/javascript"></script>
<link
	href="${pageContext.request.contextPath}/resources/css/jqueryFileTree.css"
	rel="stylesheet" type="text/css" media="screen" />
<script>
	$(document).ready(function() {
		$('#fileTree').hide();
		$('#startexploration').hide();
		$("#appstuff").hide();
		$("#selectfolder").click(function() {
			$('#fileTree').toggle();
			$('#fileTree').fileTree({
				root : '/',
				script : '${pageContext.request.contextPath}/JQueryFileTree'
			}, function(file) {
				var foldername = file.substr(0, file.lastIndexOf("/") + 1);
				$("#folder_name").val(foldername);
				$('#fileTree').hide();
				$('#startexploration').show();

				$.post('${pageContext.request.contextPath}/FileSelection', {
					dir : foldername
				}, function(data, status) {
					$('#appstuff').show();
					var values = JSON.parse(data);
					$('#selectiontable').DataTable({
						paging : false,
						searching : false,
						data : values
					});
				});
			});
		});
		$("#startexploration").click(function() {
			var form = $('<form action="${pageContext.request.contextPath}/explore" method="post" id="temp_form">' +
			  '<input type="text" name="files" value="" />' +
			  '</form>');
			$('body').append(form);
			form.submit();
			 $('#temp_form').remove();
		});
	});
</script>
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
			<div class="col-sm-12">
				<form class="form-inline text-center">
					<div class="form-group folder-select">
						<input class="form-control" id="folder_name" name="filebrowser"
							type="text" placeholder="Select folder" />
						<div class="btn btn-default" id="selectfolder">Select folder</div>
					</div>
				</form>
			</div>
		</div>

		<div class="row">
			<div class="col-sm-4"></div>
			<div class="col-sm-4">
				<div id="fileTree" class="file_selector"></div>
				<div class="col-sm-4"></div>
			</div>

			<div class="row main_start">
				<div class="col-sm-12 text-center">
					<div class="btn btn-default" id="startexploration">Start
						exploration</div>
				</div>
			</div>

			<div id="appstuff">
				<div class="row">
					<div class="col-sm-12 text-center">
						<a href="${pageContext.request.contextPath}/apkListingStatic"><button
								type="submit" class="btn btn-default pull-right" type="button"
								id="selectfolder">Show details for selected .apks</button></a>
					</div>
				</div>
				<div class="row apk-data">
					<table id="selectiontable" class="display" cellspacing="0"
						width="100%">
						<thead>
							<tr>
								<th>Name</th>
								<th>Size</th>
								<th>Package</th>
								<th>Version</th>
								<th>Analyse</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
	</main>
</body>

<jsp:include page="../partials/footer.jsp" />

</html>