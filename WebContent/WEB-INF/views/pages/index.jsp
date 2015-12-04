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
<script src="${pageContext.request.contextPath}/resources/js/jqueryFileTree.js" type="text/javascript"></script>
<link href="${pageContext.request.contextPath}/resources/css/jqueryFileTree.css" rel="stylesheet" type="text/css" media="screen" />
<script>
	$(document).ready(function() {
    	$('#fileTree').hide();
    	$('#startexploration').hide();
	    $("#selectfolder").click(function(){
	    	$('#fileTree').toggle();
	    	$('#fileTree').fileTree({ root: '/', script: '${pageContext.request.contextPath}/JQueryFileTree' }, function(file) {
	    		var foldername = file.substr(0, file.lastIndexOf("/") + 1);
				$("#folder_name").val(foldername);
				$('#fileTree').hide();
		    	$('#startexploration').show();
			});
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
						<input class="form-control" id="folder_name" name="filebrowser" type="text" placeholder="Select folder" />
						<div class="btn btn-default" id="selectfolder">Select folder</div>
					</div>
				</form>
			</div>
		</div>
		
		<div class="row">		
			<div class="col-sm-4"></div>	
			<div class="col-sm-4">		
				<div id="fileTree" class="file_selector">
			</div>
			<div class="col-sm-4"></div>	
		</div>
		
		<div class="row main_start">
			<div class="col-sm-12 text-center">
				<div class="btn btn-default" id="startexploration">Start exploration</div>
			</div>
		</div>
		<%
			if (request.getAttribute("files") != null) {
		%>
		<div class="row">
			<div class="col-sm-12 text-center">
			<a href="${pageContext.request.contextPath}/apkListingStatic"><button type="submit" class="btn btn-default pull-right" type="button" id="selectfolder">Show
						details for selected .apks</button></a>
			</div>
		</div>
		<div class="row apk-data">
			<table id="example" class="display" cellspacing="0" width="100%">
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
			<%
				List<JSONObject> files = (List) request.getAttribute("files");
			%>
			<%
				String res = "";
					for (JSONObject obj : files) {
						res += "[\"" + obj.getString("name") + "\",\"" + obj.getString("size") + "\",\""
								+ obj.getString("package") + "\",\"" + obj.getString("version") + "\",'" + 
								"<div class=\"ratio\">" 
									+ "<label><input type=\"checkbox\" value=\"\"></label>"	
									+ "</div>" + "',\""
								+ "\"],";
					}
					res = res.substring(0, res.length() - 1);
			%>
			<script>
				$('#example')
						.DataTable(
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