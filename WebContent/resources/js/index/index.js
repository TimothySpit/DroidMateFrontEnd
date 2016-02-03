requirejs([ "./../common" ], function(util) {
	requirejs([ 'bootstrap' ]);
	requirejs([ 'bootbox' ]);
	
	requirejs([ '../js/index/handleStandardButtons' ]);
	requirejs([ '../js/index/handleInlineButton' ]);
	requirejs([ '../js/index/handleExplorationButton' ]);
	requirejs([ '../js/index/handleSelectAPKPathButton' ]);
	requirejs([ '../js/index/handleUpdate' ]);
});