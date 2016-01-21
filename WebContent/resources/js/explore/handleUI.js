define([ 'require', 'jquery', 'jstree', '../explore/apkExplorationTable',
		'jquery.flot', 'jquery.flot.tooltip', 'jquery.droidmate.ajax',
		'jquery.droidmate.inlining', 'DataTables' ], function(require) {

	var tableCreator = require('../explore/apkExplorationTable');
	var table = tableCreator.initModul($('#exploreFiles'));
});