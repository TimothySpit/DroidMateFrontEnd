<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html lang="en">

<head>
<jsp:include page="../partials/header.jsp" />
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
<body class="container">
	<main>
	<div>
		<div class="row">
			<div class="col-md-12 main-heading">
				<h1 class="text-center">DroidMate</h1>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 main-heading">
				<div id="flot-placeholder" style="width: 600px; height: 300px"></div>
				<script type="text/javascript">
					var cpu = [], cpuCore = [], disk = [];
					var dataset;
					var totalPoints = 100;
					var updateInterval = 1000;
					var now = new Date().getTime();

					var options = {
						series : {
							lines : {
								lineWidth : 1.2
							},
							bars : {
								align : "center",
								fillColor : {
									colors : [ {
										opacity : 1
									}, {
										opacity : 1
									} ]
								},
								barWidth : 500,
								lineWidth : 1
							}
						},
						xaxis : {
							mode : "time",
							tickSize : [ 60, "second" ],
							tickFormatter : function(v, axis) {
								var date = new Date(v);

								if (date.getSeconds() % 20 == 0) {
									var hours = date.getHours() < 10 ? "0"
											+ date.getHours() : date.getHours();
									var minutes = date.getMinutes() < 10 ? "0"
											+ date.getMinutes() : date
											.getMinutes();
									var seconds = date.getSeconds() < 10 ? "0"
											+ date.getSeconds() : date
											.getSeconds();

									return hours + ":" + minutes + ":"
											+ seconds;
								} else {
									return "";
								}
							},
							axisLabel : "Time",
							axisLabelUseCanvas : true,
							axisLabelFontSizePixels : 12,
							axisLabelFontFamily : 'Verdana, Arial',
							axisLabelPadding : 10
						},
						yaxes : [ {
							min : 0,
							max : 100,
							tickSize : 5,
							tickFormatter : function(v, axis) {
								if (v % 10 == 0) {
									return v + "%";
								} else {
									return "";
								}
							},
							axisLabel : "CPU loading",
							axisLabelUseCanvas : true,
							axisLabelFontSizePixels : 12,
							axisLabelFontFamily : 'Verdana, Arial',
							axisLabelPadding : 6
						}, {
							max : 5120,
							position : "right",
							axisLabel : "Disk",
							axisLabelUseCanvas : true,
							axisLabelFontSizePixels : 12,
							axisLabelFontFamily : 'Verdana, Arial',
							axisLabelPadding : 6
						} ],
						legend : {
							noColumns : 0,
							position : "nw"
						},
						grid : {
							backgroundColor : {
								colors : [ "#ffffff", "#EDF5FF" ]
							}
						}
					};

					function initData() {
						for (var i = 0; i < totalPoints; i++) {
							var temp = [ now += updateInterval, 0 ];

							cpu.push(temp);
							cpuCore.push(temp);
							disk.push(temp);
						}
					}

					function GetData() {
						$.ajaxSetup({
							cache : false
						});

						$.ajax({
							url : "${pageContext.request.contextPath}/cpu",
							dataType : 'json',
							success : update,
							error : function() {
								setTimeout(GetData, updateInterval);
							}
						});
					}

					var temp;

					function update(_data) {
						cpu.shift();
						cpuCore.shift();
						disk.shift();

						now += updateInterval

						temp = [ now, _data.cpu ];
						cpu.push(temp);

						temp = [ now, _data.core ];
						cpuCore.push(temp);

						temp = [ now, _data.disk ];
						disk.push(temp);

						dataset = [ {
							label : "CPU:" + _data.cpu + "%",
							data : cpu,
							lines : {
								fill : true,
								lineWidth : 1.2
							},
							color : "#00FF00"
						}, {
							label : "Disk:" + _data.disk + "KB",
							data : disk,
							color : "#0044FF",
							bars : {
								show : true
							},
							yaxis : 2
						}, {
							label : "CPU Core:" + _data.core + "%",
							data : cpuCore,
							lines : {
								lineWidth : 1.2
							},
							color : "#FF0000"
						} ];

						$.plot($("#flot-placeholder"), dataset, options);
						setTimeout(GetData, updateInterval);
					}

					$(document).ready(function() {
						initData();

						dataset = [ {
							label : "CPU",
							data : cpu,
							lines : {
								fill : true,
								lineWidth : 1.2
							},
							color : "#00FF00"
						}, {
							label : "Disk:",
							data : disk,
							color : "#0044FF",
							bars : {
								show : true
							},
							yaxis : 2
						}, {
							label : "CPU Core",
							data : cpuCore,
							lines : {
								lineWidth : 1.2
							},
							color : "#FF0000"
						} ];

						$.plot($("#flot-placeholder"), dataset, options);
						setTimeout(GetData, updateInterval);
					});
				</script>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 main-heading">
				<%
					if (request.getAttribute("files") != null) {
						List<JSONObject> files = (List) request.getAttribute("files");
						int counter = 0;
						for (JSONObject obj : files) {
							out.print("<div class=\"progress\">"
									+ "<div class=\"progress-bar progress-bar-success pb-file pb-file-" + counter + "\" role=\"progressbar\""
									+ "aria-valuenow=\"10\" aria-valuemin=\"0\" aria-valuemax=\"100\""
									+ "style=\"width: 0%\">" + obj.getString("name") + "</div>" + "</div>");
						counter++;
						}
				%>

				<%
					}
				%>
				<script type="text/javascript">
				var updateProgressBarInterval = 100;
				var counter = 0;
				var numItems = $('.pb-file').length;
				var nowProgressBar = new Date().getTime();
				
				
				function GetProgressBarData() {
					$.ajaxSetup({
						cache : false
					});
					
					$.ajax({
						url : "${pageContext.request.contextPath}/fileUpdate?fileNr="+counter,
						dataType : 'json',
						success : updateProgressBar,
						error : function() {
							setTimeout(GetProgressBarData, updateProgressBarInterval);
						}
					});
				}

				var temp;
				
				function updateProgressBar(_data) {
				
					if(numItems  <= 0)
						return;
					nowProgressBar += updateInterval

					temp = [ nowProgressBar, _data.status ];
					console.log(_data.status);
					$('.pb-file-'+counter).css('width', _data.status+'%');
					//$('.pb-file-'+counter).html(_data.status +'%');
				    counter = Math.min(numItems, counter+1);
				    if(counter== numItems)
				    	counter = 0;
					setTimeout(GetProgressBarData, updateProgressBarInterval);
				}

				$(document).ready(function() {
					setTimeout(GetProgressBarData, updateProgressBarInterval);
				});
				</script>
			</div>
		</div>
	</div>
	</main>
</body>

<jsp:include page="../partials/footer.jsp" />

</html>