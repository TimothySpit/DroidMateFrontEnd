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
<script
	src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.pie.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/flot-axislabels/jquery.flot.axislabels.js"></script>

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
			<div class="col-sm-6">
				<div id="flot-gui-elements-not-seen"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
			<div class="col-sm-6">
				<div id="flot-gui-elements-explored"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-6">
				<div id="flot-apks-status"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
			<div class="col-sm-6">
				<div id="flot-gui-screens-explored"
					style="width: 450px; height: 300px; margin: 0 auto"></div>
			</div>
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

<!-- gui elements not yet seen chart -->
<script type="text/javascript">	
$(document).ready(function () {
	var d1 = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
	var options =  {
		    yaxis: {
		        labelWidth: 30,
		        axisLabel: 'GUI Elements not yet seen',
	            axisLabelUseCanvas: true,
	            axisLabelFontSizePixels: 20,
	            axisLabelFontFamily: 'Arial'
		    },
		    xaxis: {
		        labelHeight: 30,
		        axisLabel: 'time (min)',
	            axisLabelUseCanvas: true,
	            axisLabelFontSizePixels: 15,
	            axisLabelFontFamily: 'Arial'
		    }
		};
    $.plot($("#flot-gui-elements-not-seen"), [d1], options);
});
</script>

<!-- gui elements explored chart-->
<script type="text/javascript">	
$(document).ready(function () {
	var d1 = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
	var options =  {
		    yaxis: {
		        labelWidth: 30,
		        axisLabel: 'GUI elements explored',
	            axisLabelUseCanvas: true,
	            axisLabelFontSizePixels: 20,
	            axisLabelFontFamily: 'Arial'
		    },
		    xaxis: {
		        labelHeight: 30,
		        axisLabel: 'time (min)',
	            axisLabelUseCanvas: true,
	            axisLabelFontSizePixels: 15,
	            axisLabelFontFamily: 'Arial'
		    }
		};
	
    $.plot($("#flot-gui-elements-explored"), [d1], options);
});
</script>

<!-- apkstatus chart-->
<script type="text/javascript">	
$(document).ready(function () {
var dataSet = [
               {label: "Successful", data: 4119630000, color: "#005CDE" },
               { label: "Failed", data: 590950000, color: "#00A36A" },
               { label: "Aborted", data: 1012960000, color: "#7D0096" }    
           ];
var options = {
        series: {
            pie: { 
                show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 1,
                    formatter: function(label, series){
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                    },
                    background: { opacity: 0.8 }
                }
            }
        },
        legend: {
            show: false
        }
};
$.plot($("#flot-apks-status"), dataSet, options);    
});
</script>

<!-- gui screens explored chart-->
<script type="text/javascript">	
$(document).ready(function () {
	var d1 = [[1, 300], [2, 600], [3, 550], [4, 400], [5, 300]];
	var options =  {
		    yaxis: {
		        labelWidth: 30,
		        axisLabel: 'GUI screens explored',
	            axisLabelUseCanvas: true,
	            axisLabelFontSizePixels: 20,
	            axisLabelFontFamily: 'Arial'
		    },
		    xaxis: {
		        labelHeight: 30,
		        axisLabel: 'time (min)',
	            axisLabelUseCanvas: true,
	            axisLabelFontSizePixels: 15,
	            axisLabelFontFamily: 'Arial'
		    }
		};
	
    $.plot($("#flot-gui-screens-explored"), [d1], options);
});
</script>

<jsp:include page="../partials/footer.jsp" />

</html>