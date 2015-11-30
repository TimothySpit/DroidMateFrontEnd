<link rel="stylesheet" href="../css/bootstrap.min.css">
<link href="../jsp/cover.css" rel="stylesheet">
<script src="../assets/js/ie-emulation-modes-warning.js"></script>
<script src="../js/bootstrap.min.js"></script>
<link rel="icon" href="../favicon.ico">
<script src="../assets/js/ie-emulation-modes-warning.js"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="../assets/js/ie10-viewport-bug-workaround.js"></script>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<form action="/select" method="post">
	<input type="submit" name="selectFileButton" value="selectFile">
	</input>
</form>
<div class="container">
	<form action="/select" method="post" id="employeeForm" role="form">
		<c:choose>
			<c:when test="${not empty fileList}">
				<table class="table table-striped">
					<thead>
						<tr>
							<td>#</td>
							<td>Name</td>
							<td>Absolute Path</td>
						</tr>
					</thead>
					<c:forEach var="fileContainer" items="${fileList}">
						<td>${fileContainer.id}</td>
						<td>${fileContainer.name}</td>
						<td>${fileContainer.absoluteFilePath}</td>
						</tr>
					</c:forEach>
				</table>
			</c:when>
		</c:choose>
	</form>
</div>
