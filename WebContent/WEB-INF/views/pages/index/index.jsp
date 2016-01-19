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
<script
	src="${pageContext.request.contextPath}/resources/js/index/directoryTree.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/index/handleInlining.js"></script>
	<script
	src="${pageContext.request.contextPath}/resources/js/index/handleExploration.js"></script>
<!-- file tree end -->
</head>
<body class="container">
	<main>
	<div>
		<div class="settings-button">
			<form action="${pageContext.request.contextPath}/Settings">
				<button class="btn btn-default" id="settings">
					<span class="glyphicon glyphicon-cog"></span>
				</button>
			</form>
		</div>
		<div class="row">
			<div class="col-md-12 main-heading">
				<h1 class="text-center">DroidMate</h1>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12 text-center">
				<form class="form-inline">
					<div class="form-group folder-select">
						<input class="form-control" id="folder_name" name="filebrowser"
							type="text" placeholder="Select folder" />
						<div class="btn btn-default" id="selectfolder" data-toggle="modal"
							data-target="#folderSelectModal">Select folder</div>
					</div>
				</form>
				<div id="load-result-indikator"></div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-4"></div>
			<div class="col-sm-4">
				<!-- js tree -->
				<jsp:include page="directoryTreeDialog.jsp" />
				<!-- js tree -->
				<div class="col-sm-4"></div>
			</div>

			<div class="row main_start">
				<div class="col-sm-12 text-center hide" id="btns-field">
					<button type="submit" class="btn btn-default" id="startexploration">Start
						exploration</button>
					<button type="submit" class="btn btn-default" id="inline_files">Inline
						files</button>
				</div>
			</div>

			<div id="appstuff">
				<div class="row">
					<div class="col-sm-4"></div>
					<div class="col-sm-4"></div>
					<div class="col-sm-4">
						<button class="btn btn-default pull-right hide"
							data-toggle="modal" data-target="#staticinfomodal"
							id="show-static" type="button">
							<span class="glyphicon glyphicon-list-alt"></span> Show details
						</button>
						<!-- js tree -->
						<jsp:include page="staticInfoModal.jsp" />
						<!-- js tree -->
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12 text-center">
						<a href="${pageContext.request.contextPath}/apkListingStatic"><button
								type="submit" class="btn btn-default pull-right hide"
								type="button" id="selectfolder">Show details for
								selected .apks</button></a>
					</div>
				</div>
				<div class="row apk-data hide">
					<table id="selectiontable">
						<thead>
							<tr>
								<th><input name="select_all" value="1" type="checkbox"></th>
								<th>Name</th>
								<th>Size</th>
								<th>Package</th>
								<th>Version</th>
								<th>Inlined</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
	</div>
	</main>
</body>

<jsp:include page="../../partials/footer.jsp" />

</html>