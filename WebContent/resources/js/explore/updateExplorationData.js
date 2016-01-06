$(function() {

	// retrieve files count
	$.ajaxSetup({
		cache : false
	});

	var updateInterval = 1000;
	var counter = 0;
	var numItems = 0;

	function getData() {
		$.ajaxSetup({
			cache : false
		});

		var fileName = $('#exploreFiles tbody tr:nth-child(' + (counter+1) + ') td:first-child .apk-name');
		$.ajax({
			url : "ExplorationData?update="
					+ fileName.text(),
			dataType : 'json',
			success : updateExplorationStatus,
			error : function() {
				setTimeout(getData, updateInterval);
			}
		});
	}

	function showReportButton(row) {
		
	}
	
	function updateExplorationStatus(_data) {
		var row = $('#exploreFiles tbody tr:nth-child(' + (counter+1) + ')');
		var progressBar = row.find('.progress-bar');
		progressBar.width(_data.progress+'%');
		progressBar.text(_data.progress+'%');
		counter = Math.min(numItems, counter + 1);
		
		if(_data.status == 'FINISHED') {
			showReportButton(row);
		}
		
		if (counter == numItems)
			counter = 0;
		setTimeout(getData, updateInterval);
	}

	$(document).ready(function() {
	// init update
	$.getJSON("ExplorationData?filesCount",
			function(data) {
				if (data.count) {
					//set up table
					$('#exploreFiles').DataTable({
						"ajax": {
				        	'url':'/DroidMate/ExplorationData?apkTableData',
				        	'dataSrc': function (json) {
				        		$( ".apk-data" ).removeClass( "hide" );
								 
								//start updating data
				        		numItems = json.data.length;
								setTimeout(getData, updateInterval);
				        		return json.data;
				        	}
						},
						 'searching': false,
				         'paging': false,
				         "columnDefs": [ {
				             "targets": 0,
				             "searchable": false,
				             "render": function ( data, type, row ) {
				                    return '<span class="apk-name">' + data + '</span>' + '<button ' +
				                    'class="btn btn-default pull-right" type="button">Show report' +
				                  '</button>'
				                }
				           }
				                         ,{
				             "targets": 1,
				             "searchable": false,
				             "render": function ( data, type, row ) {
				                    return '<div class="progress-bar" role="progressbar" aria-valuenow="10"' +
				                    'aria-valuemin="0" aria-valuemax="100" style="width:70%">' +
				                    data + '%' +
				                  '</div>'
				                }
				           } ]
					});
					
				}
			});
	});
});