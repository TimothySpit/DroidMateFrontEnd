<%@page import="com.droidmate.user.GUISettings"%>
<jsp:include page="../../partials/header.jsp" />

<!-- requirejs -->
<script
	data-main="${pageContext.request.contextPath}/resources/js/settings/settings.js"
	src="${pageContext.request.contextPath}/resources/libraries/requirejs/require.js"></script>

</head>

<%
	GUISettings settings = new GUISettings();
%>

<body>
	<div class="container">

		<!-- Settings Heading -->
		<div class="row">
			<div class="col-md-12 main-heading">
				<h1 class="text-center">Settings</h1>
			</div>
		</div>

		<!-- Reports path select controls -->
		<div class="row">
			<div class="col-sm-4">
				<label class="pull-right" for="input-reports-path">Reports
					output path:</label>
			</div>
			<div class="col-sm-4">
				<input class="form-control" id="input-reports-path"
					name="input-reports-path" type="text"
					placeholder="Select reports path"
					value="<%=settings.getOutputFolder()%>" />
			</div>
			<div class="col-sm-4">
				<button id="button-reports-output-path"
					class="btn btn-default btn-default">
					<span class="glyphicon glyphicon-folder-open"></span>
				</button>
			</div>
		</div>

		<!-- DroidMate path select controls -->
		<div class="row">
			<div class="col-sm-4">
				<label class="pull-right" for="input-droidmate-path">DroidMate
					Path:</label>
			</div>
			<div class="col-sm-4">
				<input class="form-control" id="input-droidmate-path"
					name="input-droidmate-path" type="text"
					placeholder="DroidMate path"
					value="<%=settings.getDroidMatePath()%>" />
			</div>
			<div class="col-sm-4">
				<button class="btn btn-default btn-default"
					id="button-droidmate-path">
					<span class="glyphicon glyphicon-folder-open"></span>
				</button>
			</div>
		</div>

		<!-- AAPT path select controls -->
		<div class="row">
			<div class="col-sm-4">
				<label class="pull-right" for="input-aapt-path">AAPT Path:</label>
			</div>
			<div class="col-sm-4">
				<input class="form-control" id="input-aapt-path"
					name="input-aapt-path" type="text" placeholder="AAPT Path"
					value="<%=settings.getAaptToolPath()%>" />
			</div>
			<div class="col-sm-4">
				<button class="btn btn-default btn-default" id="button-aapt-path">
					<span class="glyphicon glyphicon-folder-open"></span>
				</button>
			</div>
		</div>

		<!-- Exploration time select controls -->
		<div class="row">
			<div class="col-sm-4">
				<label class="pull-right" for="input-exploration-timeout">Exploration
					Timeout (s):</label>
			</div>
			<div class="col-sm-4">
				<input class="form-control" id="input-exploration-timeout"
					name="input-exploration-timeout" type="text"
					placeholder="Exploration timeout"
					value="<%=settings.getExplorationTimeout()%>" />
			</div>
			<div class="col-sm-4"></div>
		</div>

		<!-- Back/Save changes controls -->
		<div class="row">
			<div class="col-sm-4">
				<form action="${pageContext.request.contextPath}/Index">
					<button class="btn btn-default btn-default" id="button-back">
						<span class="glyphicon glyphicon-circle-arrow-left"></span> Back
					</button>
				</form>
			</div>
			<div class="col-sm-4"></div>
			<div class="col-sm-4">
				<button name="button-save-changes" id="button-save-changes"
					class="btn btn-default btn-default pull-right">
					<span class="glyphicon glyphicon-floppy-disk"></span> Save changes
				</button>
			</div>
		</div>

	</div>
</body>

<jsp:include page="../../partials/footer.jsp" />
