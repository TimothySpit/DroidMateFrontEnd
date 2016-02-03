<jsp:include page="../../partials/header.jsp" />

<!-- requirejs -->
<script
	data-main="${pageContext.request.contextPath}/resources/js/index/index.js"
	src="${pageContext.request.contextPath}/resources/libraries/requirejs/require.js"></script>

</head>

<body>
	<div class="container">

		<!-- DroidMate Heading -->
		<div class="row">
			<div class="col-md-12 main-heading">
				<h1 class="text-center">DroidMate</h1>
			</div>
		</div>

		<!-- Settings button -->
		<div class="button-settings-container">
			<button class="btn btn-default" id="button-settings">
				<span class="glyphicon glyphicon-cog"></span>
			</button>
		</div>

		<!-- Select APK Folder controls -->
		<div class="row">
			<div class="col-sm-12 text-center">
				<div class="form-group form-inline">
					<input class="form-control" id="input-apk-folder-selection"
						name="apk-folder-selection" type="text"
						placeholder="Select APK folder" />
					<button class="btn btn-default" id="button-apk-folder-selection">Select
						APK folder</button>
				</div>
				<div style="display: none;" id="div-apk-folder-selection-result"></div>
			</div>
		</div>

		<!-- Start/inline button controls -->
		<div class="row main_start">
			<div id="buttons-start-inline" class="col-sm-12 text-center" style="display: none;">
				<button class="btn btn-default" id="button-start-exploration" disabled>Start
					exploration</button>
				<button class="btn btn-default" id="button-inline-files" disabled>Inline
					APKs</button>
			</div>
		</div>

		<!-- Static information button -->
		<div class="row">
			<div class="col-sm-8"></div>
			<div class="col-sm-4">
				<button class="btn btn-default pull-right" style="display: none;"
					id="button-show-static-information">
					<span class="glyphicon glyphicon-list-alt"></span> Show APK details
				</button>
			</div>
		</div>

		<!-- APK table containing static apk information -->
		<div class="row" id="div-apk-static-information-container" style="display: none;">
			<table id="table-apk-static-information">
			</table>
		</div>

	</div>
</body>

<jsp:include page="../../partials/footer.jsp" />