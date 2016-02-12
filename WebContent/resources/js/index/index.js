requirejs([ "./../common" ], function(util) {
	requirejs([ 'bootstrap' ]);
	
	requirejs([ '../js/index/handleStandardButtons' ]);
	requirejs([ '../js/index/handleInlineButton' ]);
	requirejs([ '../js/index/handleExplorationButton' ]);
	requirejs([ '../js/index/handleSelectAPKPathButton' ]);
	requirejs([ '../js/index/handleUpdate' ]);
	requirejs([ '../js/index/handleStandardEvents' ]);
});