<!DOCTYPE html>

<html>
	<head>
		<jsp:include page="../partials/header.jsp" />
		<script src="${pageContext.request.contextPath}/resources/js/jqueryFileTree.js" type="text/javascript"></script>
		<link href="${pageContext.request.contextPath}/resources/css/jqueryFileTree.css" rel="stylesheet" type="text/css" media="screen" />
		<style type="text/css">
			.example {
				float: left;
				margin: 15px;
			}

			.file_selector {
				width: 100%;
				height: 100%;
				border-top: solid 1px #BBB;
				border-left: solid 1px #BBB;
				border-bottom: solid 1px #FFF;
				border-right: solid 1px #FFF;
				background: #FFF;
				overflow: scroll;
				padding: 5px;
			}
		</style>

		<script type="text/javascript">
			$(document).ready( function() {
				$('#fileTree').fileTree({ root: '/', script: '${pageContext.request.contextPath}/JQueryFileTree' }, function(file) {
					alert(file);
				});
			});
		</script>
	</head>
	<body>
		
			<h2>Default options</h2>
			<div id="fileTree" class="file_selector"></div>
		
	</body>
	<jsp:include page="../partials/footer.jsp" />
</html>