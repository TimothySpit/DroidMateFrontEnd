requirejs([ "./../common" ], function(util) {
	requirejs([ 'bootstrap' ]);
	requirejs([ 'bootbox' ]);
	
	requirejs([ '../js/explore/handleUpdate' ]);
	requirejs([ '../js/explore/handleStartup' ]);
	requirejs([ '../js/explore/handleExploreUpdate' ]);
	requirejs([ '../js/explore/handleButtons' ]);
});