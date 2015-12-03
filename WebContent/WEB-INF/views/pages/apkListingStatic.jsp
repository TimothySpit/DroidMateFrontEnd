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
<script src="${pageContext.request.contextPath}/resources/js/staticDiagrams.js"></script>
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
</head>
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
				
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12 text-center">
			<div id="flot-placeholder" style="width:450px;height:300px;margin:0 auto"></div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12 text-center">
			<button type="submit" data-toggle="modal" data-target="#myModal"
				onclick="window.history.back()" class="btn btn-default pull-left" type="button" id="selectfolder">Back</button>
			</div>
		</div>
	</div>
	</main>
</body>

<jsp:include page="../partials/footer.jsp" />

</html>