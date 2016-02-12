requirejs
		.config({
			shim : {
				'jquery' : {
					exports : '$'
				},
				'jquery.flot' : {
					deps : [ 'jquery' ],
					exports : '$.plot'
				},
				'jquery.flot.time' : {
					deps : [ 'jquery.flot' ]
				},
				'jquery.flot.canvas' : {
					deps : [ 'jquery.flot' ]
				},
				'jquery.flot.excanvas' : {
					deps : [ 'jquery.flot' ]
				},
				'jquery.flot.symbol' : {
					deps : [ 'jquery.flot' ]
				},
				'jquery.flot.pie' : {
					deps : [ 'jquery.flot' ]
				},
				'jquery.flot.tooltip' : {
					deps : [ 'jquery.flot' ]
				},
				'jquery.flot.axislabels' : {
					deps : [ 'jquery.flot' ]
				},
				'jquery.flot.navigate' : {
					deps : [ 'jquery.flot' ]
				},
				'bootstrap' : {
					deps : [ 'jquery' ]
				},
				'jstree' : {
					deps : [ 'jquery' ],
					exports : '$.jstree'
				},
				'jquery.droidmate.ajax' : {
					deps : [ 'jquery' ],
					exports : '$.droidmate'
				},
				'jquery.droidmate.explore' : {
					deps : [ 'jquery' ],
					exports : '$.droidmate'
				},
				'jquery.droidmate.inlining' : {
					deps : [ 'jquery' ],
					exports : '$.droidmate'
				},
				'jquery.droidmate.overlays' : {
					deps : [ 'jquery' ],
					exports : '$.droidmate'
				},
				'jquery.droidmate.dialogs' : {
					deps : [ 'jquery' ],
					exports : '$.droidmate'
				},
				'DataTables' : {
					deps : [ 'jquery' ]
				},
				'Spinner' : {
					deps : [ 'jquery' ]
				}
			},

			baseUrl : 'resources/libraries/',

			paths : {
				jquery : 'jquery/jquery-1.12.0.min',
				'jquery.flot' : 'flot/jquery.flot.min',
				'jquery.flot.time' : 'flot/jquery.flot.time.min',
				'jquery.flot.canvas' : 'flot/jquery.flot.canvas.min',
				'jquery.flot.excanvas' : 'flot/excanvas.min',
				'jquery.flot.symbol' : 'flot/jquery.flot.symbol.min',
				'jquery.flot.tooltip' : 'flot/flot-tooltip/jquery.flot.tooltip.min',
				'jquery.flot.pie' : 'flot/jquery.flot.pie.min',
				'jquery.flot.axislabels' : 'flot/flot-axislabels/jquery.flot.axislabels',
				'jquery.flot.navigate' : 'flot/jquery.flot.navigate.min',
				'bootstrap' : 'bootstrap/js/bootstrap.min',
				'bootbox' : 'bootboxjs/bootbox.min',
				'jstree' : 'jstree/jstree.min',
				'jquery.droidmate.ajax' : 'droidmate/jquery.droidmate.ajax',
				'jquery.droidmate.explore' : 'droidmate/jquery.droidmate.explore',
				'jquery.droidmate.inlining' : 'droidmate/jquery.droidmate.inlining',
				'jquery.droidmate.overlays' : 'droidmate/jquery.droidmate.overlays',
				'jquery.droidmate.dialogs' : 'droidmate/jquery.droidmate.dialogs',
				'DataTables' : 'datatables/media/js/jquery.dataTables.min',
				'Spinner' : 'spin/spin.min',
			}
		});