<%@page import="java.util.function.Consumer"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../partials/header.jsp" />
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
				<form class="form-inline text-center"
					action="${pageContext.request.contextPath}/index" method="post">
					<div class="form-group folder-select">
						<input class="form-control" id="folder" name="filebrowser"
							value=<%
			if (request.getAttribute("selpath") != null) {
				out.print("\"" + request.getAttribute("selpath") + "\"");
			} else {
				out.print("\"Please select a folder\"");
			}
		%> type="text" placeholder="Select folder" />
						<button type="submit" data-toggle="modal" data-target="#myModal"
							class="btn btn-default" type="button" id="selectfolder">Select
							Folder</button>
					</div>
				</form>
			</div>
		</div>
		<div class="row main_start">
			<div class="col-sm-12 text-center">
				<a href="${pageContext.request.contextPath}/exploration">
					<button data-toggle="modal" class=<%
			if (request.getAttribute("selpath") != null) {
				out.print("\"btn btn-default\"");
			} else {
				out.print("\"btn btn-default disabled\"");
			}%>
						type="button" id="startexploration">Start exploration</button>
				</a>
			</div>
		</div>
		<%
			if (request.getAttribute("files") != null) {
		%>
		<div class="row apk-data">
			<table id="example" class="display" cellspacing="0" width="100%">
				<thead>
					<tr>
						<th>Name</th>
						<th>Size</th>
						<th>Package</th>
						<th>Version</th>
					</tr>
				</thead>
			</table>
			<%
				List<JSONObject> files = (List) request.getAttribute("files");
			%>
			<%
				String res = "";
					for (JSONObject obj : files) {
						res += "[\"" + obj.getString("name") + "\",\"" + obj.getString("size") + "\",\""
								+ obj.getString("package") + "\",\"" + obj.getString("version") + "\"],";
					}
					res = res.substring(0, res.length() - 1);
			%>
			<script>
				$('#example').DataTable(
			<%out.println("{ \"paging\": false,\"searching\": false, \"data\":[" + res + " ]}");%>
				);
			</script>
		</div>
		<%
			}
		%>

	</div>
	</main>
</body>

<jsp:include page="../partials/footer.jsp" />

</html>