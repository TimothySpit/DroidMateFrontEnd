<%@page import="java.util.function.Consumer"%>
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
	<script
	src="${pageContext.request.contextPath}/resources/js/flot-tickrotor/jquery.flot.tickrotor.js"></script>
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
			<div class="col-sm-12"></div>
		</div>
		<div class="row">
			<div class="col-sm-12 text-center">
				
		</div>
		<div class="row">
			<div class="col-sm-12 text-center">
				<button type="submit" data-toggle="modal" data-target="#myModal"
					onclick="window.history.back()" class="btn btn-default pull-left"
					type="button" id="selectfolder">Back</button>
			</div>
		</div>
	</div>
	</main>
</body>

<!-- file size chart -->
<script type="text/javascript">
	
<%if (request.getAttribute("files") != null) {%>
	

	<%
	List<JSONObject> files = (List) request.getAttribute("files");
	String names = "";
	String size = "";
	int counter = 0;
	for (JSONObject obj : files) {
		size += "[" + counter + "," + obj.getString("size")
		+ "],";
		names += "[" + counter + ",\"" + obj.getString("name")
				+ "\"],";
		counter++;
	}
	names = "[" + names.substring(0, names.length() - 1) + "];";
	size = "[" + size.substring(0, size.length() - 1) + "];";
	out.println("var data = " + size);
	out.println("var ticks = " + names);
	%>
	
	var dataset = [ {
		label : "File size (KB)",
		data : data,
		color : "#5482FF"
	} ];

	var options = {
		series : {
			bars : {
				show : true
			}
		},
		bars : {
			align : "center",
			barWidth : 0.5
		},
		xaxis : {
			axisLabel : "World Cities",
			axisLabelUseCanvas : true,
			axisLabelFontSizePixels : 5,
			axisLabelFontFamily : 'Verdana, Arial',
			axisLabelPadding : 6,
			ticks : ticks,
			rotateTicks: 90

		},
		yaxis : {
			axisLabel : "File Size",
			axisLabelUseCanvas : true,
			axisLabelFontSizePixels : 12,
			axisLabelFontFamily : 'Verdana, Arial',
			axisLabelPadding : 2,
			tickFormatter : function(v, axis) {
				return v + " KB";
			}
		},
		legend : {
			noColumns : 0,
			labelBoxBorderColor : "#000000",
			position : "nw"
		},
		grid : {
			hoverable : true,
			borderWidth : 0,
			backgroundColor : {
				colors : [ "#ffffff", "#EDF5FF" ]
			}
		}
	};

	$(document).ready(function() {
		$.plot($("#flot-placeholder"), dataset, options);
		$("#flot-placeholder").UseTooltip();
	});

	function gd(year, month, day) {
		return new Date(year, month, day).getTime();
	}

	var previousPoint = null, previousLabel = null;

	$.fn.UseTooltip = function() {
		$(this).bind(
				"plothover",
				function(event, pos, item) {
					if (item) {
						if ((previousLabel != item.series.label)
								|| (previousPoint != item.dataIndex)) {
							previousPoint = item.dataIndex;
							previousLabel = item.series.label;
							$("#tooltip").remove();

							var x = item.datapoint[0];
							var y = item.datapoint[1];

							var color = item.series.color;

							//console.log(item.series.xaxis.ticks[x].label);                

							showTooltip(item.pageX, item.pageY, color,
									"<strong>" + item.series.label
											+ "</strong><br>"
											+ item.series.xaxis.ticks[x].label
											+ " : <strong>" + y
											+ "</strong> KB");
						}
					} else {
						$("#tooltip").remove();
						previousPoint = null;
					}
				});
	};

	function showTooltip(x, y, color, contents) {
		$('<div id="tooltip">' + contents + '</div>').css({
			position : 'absolute',
			display : 'none',
			top : y - 40,
			left : x - 120,
			border : '2px solid ' + color,
			padding : '3px',
			'font-size' : '9px',
			'border-radius' : '5px',
			'background-color' : '#fff',
			'font-family' : 'Verdana, Arial, Helvetica, Tahoma, sans-serif',
			opacity : 0.9
		}).appendTo("body").fadeIn(200);
	}
<%}%>
	
</script>

<jsp:include page="../partials/footer.jsp" />

</html>