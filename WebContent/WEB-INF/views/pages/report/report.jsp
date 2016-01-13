<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/report/style.css">
<title>Droidmate report</title>
</head>
<body>
	<table>
		<tr>
			<th>Name</th>
			<th>Elements seen</th>
			<th>Success</th>
		</tr>
		<%
			String apkName = request.getAttribute("apk_name") == null ? "NO_NAME" : (String)request.getAttribute("apk_name");
			String elementsSeen = request.getAttribute("elements_seen") == null ? "0" : (String)request.getAttribute("elements_seen");
			Boolean successful = request.getAttribute("apk_successful") == null ? false : (Boolean)request.getAttribute("apk_successful");
		%>
		<tr>
			<td>
				<%= apkName %>
			</td>
			<td>
				<%= elementsSeen %>
			</td>
			<td>
			
			<%if (successful) {%>
				<img alt="Success" src="/DroidMate/resources/css/report/images/success.png">
			<%} else {%>
				<img alt="Failure" src="/DroidMate/resources/css/report/images/failure.png">
			<%}%>
			</td>
		</tr>
	</table>
</body>
</html>