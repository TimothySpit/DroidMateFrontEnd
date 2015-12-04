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
<!-- chart files -->
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
<script
    src="${pageContext.request.contextPath}/resources/js/explore/exploreCharts.js"></script>


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
            <div class="col-sm-4">
                <form class="pull-left">
                    <a href="${pageContext.request.contextPath}/index">
                        <button data-toggle="modal"
                            class="btn btn-default" type="button" id="returnStart">Return to start</button>
                    </a>
                </form>
            </div>
            <div class="col-sm-4 text-center">
                <form>
                    <a href="${pageContext.request.contextPath}/apkListingDynamic"><button class="btn btn-default" type="button" id="apkInfoBtn">Show details for selected .apks</button></a>
                </form>
            </div>
            <div class="col-sm-4">
                <form class="pull-right">
                    <button class="btn btn-default" type="button">Stop All</button>
                </form>
            </div>
        </div>
        <%
            if (request.getAttribute("files") != null) {
        %>
        <div class="row apk-data">
            <table id="example" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th></th>
                        <th>Name</th>
                        <th>Progress</th>
                        <th>Status</th>
                    </tr>
                </thead>
            </table>
            <%
                List<JSONObject> files = (List) request.getAttribute("files");
            %>
            <%
                String res = "";
                    for (JSONObject obj : files) {
                        res += "{\"name\":\"" + obj.getString("name") + "\",\"progress\":'" + 
                                 "<div class=\"progress\">" 
                                    + "<div class=\"progress-bar progress-bar-striped active\" role=\"progressbar\""
                                    + "aria-valuenow=\"10\" aria-valuemin=\"10\" aria-valuemax=\"100\""
                                    + "style=\"width: 40%\">" + obj.getString("name") + "</div>" + "</div>" 
                                    + "',\"status\":\""
                                + obj.getString("version") + "\"},";
                    }
                    res = res.substring(0, res.length() - 1);
            %>
            <!-- datatables drop down menu -->
            <script>
                var table = $('#example')
                        .DataTable(
            <%out.println("{ \"columns\": [ {\"className\": 'details-control',\"data\": null,\"defaultContent\": ''},{ \"data\": \"name\"},{\"data\": \"progress\"},{\"data\": \"status\"}], \"paging\": false,\"searching\": false, \"data\":[" + res + " ], }");%>
                );
                var hashCode = function(str){
                    var hash = 0;
                    if (str.length == 0) return hash;
                    for (i = 0; i < str.length; i++) {
                        char = str.charCodeAt(i);
                        hash = ((hash<<5)-hash)+char;
                        hash = hash & hash; // Convert to 32bit integer
                    }
                    return hash;
                };
                
                function format ( d ) {
                    // `d` is the original data object for the row
                    console.log(d.name);
                    return  '<div class="row">' + 
                                '<div style="height:300px;" class="col-sm-4" id="flot-gui-elements-not-seen-' + hashCode(d.name)+ '">'+
                                
                                '</div>'+
                                '<div style="height:300px;" class="col-sm-4" id="flot-gui-elements-explored-' + hashCode(d.name) + '">'+
                                    
                                '</div>'+
                                '<div style="height:300px;" id="flot-gui-screens-explored-' + hashCode(d.name) + '" class="col-sm-4">'+
                                    
                                '</div>'+
                            '</div>';
                };
                 
                $(document).ready(function() {
                    // Add event listener for opening and closing details
                    $('#example tbody').on('click', 'td.details-control', function () {
                        var tr = $(this).closest('tr');
                        var row = table.row( tr );
                 
                        if ( row.child.isShown() ) {
                            // This row is already open - close it
                            row.child.hide();
                            tr.removeClass('shown');
                        }
                        else {
                            // Open this row
                            row.child( format(row.data()) ).show();
                            createChartGUIElementsToExplore("#flot-gui-elements-not-seen-" + hashCode(row.data().name).toString());
                            createChartGUIScreensExplored("#flot-gui-elements-explored-" + hashCode(row.data().name).toString());
                            createChartGUIElementsExplored("#flot-gui-screens-explored-" + hashCode(row.data().name).toString());
                            tr.addClass('shown');
                        }
                    } );
                } );
            </script>
        </div>
        <%
            }
        %>
        <div class="row">
            <div class="col-sm-4">
                <button class="btn btn-default pull-left" type="button">Open
                    output folder</button>
            </div>
            <div class="col-sm-4"></div>
            <div class="col-sm-4"></div>
        </div>
    </div>
    </main>
</body>

<jsp:include page="../partials/footer.jsp" />

</html>