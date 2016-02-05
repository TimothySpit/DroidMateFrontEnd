<jsp:include page="../../partials/header.jsp" />

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/explore/style.css">

<!-- requirejs -->
<script
	data-main="${pageContext.request.contextPath}/resources/js/explore/explorationCharts/explorationCharts.js"
	src="${pageContext.request.contextPath}/resources/libraries/requirejs/require.js"></script>

</head>

<body>
	<div class="container">

		<!-- DroidMate heading -->
		<div class="row">
			<div class="col-md-12 main-heading">
				<h1 class="text-center">APKs Information</h1>
			</div>
		</div>

		<!-- elements seen and screens explored charts -->
		<div class="row" id="div-charts-elements-seen-screens-explored" style="display: none;">
			<div class="col-sm-6">
				<div id="flot-gui-elements-seen"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
			<div class="col-sm-6">
				<div id="flot-gui-screens-explored"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
		</div>

		<!-- apk status and gui elements explored charts -->
		<div class="row" id="div-charts-apk-status-elements-explored" style="display: none;">
			<div class="col-sm-6">
				<div id="flot-apks-status"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
			<div class="col-sm-6">
				<div id="flot-gui-elements-explored"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
		</div>

		<!-- chart multiselect legend -->
		<div class="row" id="div-chart-multiselect" style="display: none;">
			<div class="col-sm-12">
				<fieldset id="legend">
					<legend>APK chart selection</legend>
					<div id="apks-charts-legend"></div>
				</fieldset>
			</div>
		</div>

		<!-- bottom navi controls -->
		<div class="row" id="div-explorationcharts-bottom-navi" style="display: none;">
			<div class="col-sm-12 text-center">
				<button class="btn btn-default pull-left"
					 id="button-back">Back</button>
			</div>
		</div>

	</div>
</body>

<jsp:include page="../../partials/footer.jsp" />