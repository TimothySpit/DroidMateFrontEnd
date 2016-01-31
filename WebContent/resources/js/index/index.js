requirejs([ "./../common" ], function(util) {
	requirejs([ 'bootstrap' ]);
	requirejs([ 'bootbox' ]);
	
	requirejs([ '../js/index/handleInlining' ]);
	requirejs([ '../js/index/handleExploration' ]);
	requirejs([ '../js/index/handleUI' ]);
});