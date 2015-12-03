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
			<div class="col-sm-4">
				<form class="pull-left">
					<button class="btn btn-default" type="button">Return
						to start</button>
				</form>
			</div>
			<div class="col-sm-4 text-center">
				<form>
					<button class="btn btn-default" type="button" id="apkInfoBtn">Show
						details for selected .apks</button>
				</form>
			</div>
			<div class="col-sm-4">
				<form class="pull-right">
					<button class="btn btn-default" type="button">Stop
						All</button>
				</form>
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
		
		<div class="row">
			<div class="col-sm-4">
				<button class="btn btn-default pull-left" type="button">Open
					output folder</button>
			</div>
			<div class="col-sm-4"></div>
			<div class="col-sm-4">
				<button class="btn btn-default pull-right"" type="button">Open
					output folder</button>
			</div>
		</div>
	</div>
	</main>
</body>

<jsp:include page="../partials/footer.jsp" />

</html>