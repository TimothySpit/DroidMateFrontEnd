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

<!-- requirejs -->
<script data-main="${pageContext.request.contextPath}/resources/js/index/index.js"
	src="${pageContext.request.contextPath}/resources/libraries/requirejs/require.js"></script>
	
</head>
<body class="container">
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

		<div class="row main_start">
			<div class="col-sm-12 text-center" id="btns-field" style="display: none;">
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
					<button class="btn btn-default pull-right hide" data-toggle="modal"
						data-target="#staticinfomodal" id="show-static" type="button">
						<span class="glyphicon glyphicon-list-alt"></span> Show details
					</button>
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
			<div class="row apk-data" style="display: none;">
				<table id="selectiontable">
				</table>
			</div>
		</div>
	</div>
	<!-- modal dialogs -->
	<div>
		<jsp:include page="modals/apkFolderSelection.jsp" />
		<jsp:include page="modals/fileSizeInformation.jsp" />
	</div>
</body>

<jsp:include page="../../partials/footer.jsp" />

</html>