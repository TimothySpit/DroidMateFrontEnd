<jsp:include page="../../partials/header.jsp" />

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/explore/style.css">

<!-- requirejs -->
<script
	data-main="${pageContext.request.contextPath}/resources/js/explore/explore.js"
	src="${pageContext.request.contextPath}/resources/libraries/requirejs/require.js"></script>

</head>

<body>
	<div class="container">

		<!-- DroidMate heading -->
		<div class="row">
			<div class="col-md-12 main-heading">
				<h1 class="text-center">DroidMate</h1>
			</div>
		</div>

		<!-- top navi controls -->
		<div class="row">
			<div class="col-sm-4">
				<button class="pull-left btn btn-default"
					id="button-return-to-start">Return to start</button>
			</div>
			<div class="col-sm-2 text-center">
				Exploration time: <span class="label label-default" id="timeLabel">Not
					started</span>
			</div>
			<div class="col-sm-2 text-center">
				<button class="btn btn-default" id="button-show-apk-details-dynamic">Show
					details for APKs</button>
			</div>
			<div class="col-sm-4">
				<form class="pull-right">
					<button id="button-stop-all" class="btn btn-default">Stop
						All</button>
				</form>
			</div>
		</div>

		<!-- APK table containing exploration data -->
		<div class="row">
			<table id="table-apk-exploration-info">
			</table>
		</div>

		<!-- Open reports directory button group and label -->
		<div class="row">
			<div class="col-sm-12">
				<h3>
					<button id="button-open-output-folder"
						class="btn btn-default pull-left">Open
						output folder</button>
					<span class="label label-default" id="span-output-folder-path">Loading
						directory...</span>
				</h3>
			</div>
		</div>

		<!-- DroidMate console output -->
		<div class="row">
			<div class="col-sm-12">
				<div class="panel panel-default" id="div-console-output"></div>
			</div>
		</div>
		
	</div>
</body>

<jsp:include page="../../partials/footer.jsp" />