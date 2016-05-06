/**
 * LegalWise 2.0 Scripts for Search (index) page.
 * @author	Fernando Gomez Reyes
 */
var search = {
	busy: false,
	query: null,
	message: {
		show: false,
		isError: false,
		title: null,
		msg: null
	},
	history: [],
	result: null,
	init: function() {
		search.getHistory();
	},
	search: function(e, fromHistory) {		
		if (e.keyCode == 13 && search.query && search.query.length > 3) {
			search.busy = true;
			search.result = null;
			general.server.search(search.query,
				function(r) {
					search.busy = false;
					search.hideMessage();
					search.result = r;
					
					if (!fromHistory)
						search.history.push({
							"searchText": search.query,
							"performedOn": (new Date()).getTime()
						});
				},
				function(e) {
					search.busy = false;
					search.showMessge(
						"There was a problem while searching",
		    			e.msg || "No further information available",
		    			true);
				});
		}
	},
	searchHistory: function(h) {
		h.performedOn = (new Date()).getTime();
		search.query = h.searchText;
		search.search({keyCode: 13}, true);
	},
	getHistory: function(e) {
		search.history = [];
		general.server.getHistory(
			function(r) {
				search.history = r;
			},
			function(e) {
				// disregard
			});
	},
	showMessge: function(title, msg, error) {
		search.message.show = true;
		search.message.isError = error;
		search.message.title = title;
		search.message.msg = msg;
	},
	hideMessage: function() {
		search.message.show = false;
		search.message.isError = false;
		search.message.title = null;
		search.message.msg = null;
	},
	getAnswerType: function(doc) {
		var d =  doc.id || "";
		return d.startsWith("QA") ? "QA" : d.startsWith("HN") ? "HN" : null;
	}
};

//general object, main functionality
var general = {
	ctrl: function($scope, $document) {
		$scope.search = search;
		$scope.serverUrl = window.serverUrl;
		general.scope = $scope;	
		
		// init
		search.init();		
	},
	apply: function() {
 		var phase = general.scope.$root.$$phase;
		if (phase != '$apply' && phase != '$digest')
			general.scope.$apply();
 	},
 	server: {
 		search: function(query, onSuccess, onError) {
 			general._call("GET", 
 				"/service/search?query=" + encodeURI(query),
 				"", onSuccess, onError);
 		},
 		getHistory: function(onSuccess, onError) {
 			general._call("GET", "/service/history", "", onSuccess, onError);
 	 	}
 	},
 	_call: function(method, serviceUrl, body, onSuccess, onError) {
 		var xhr = new XMLHttpRequest();
		xhr.open(method, (window.serverUrl || "") + serviceUrl, true);
 		setTimeout(function() {
			xhr.send(body);			
			xhr.onreadystatechange = function() {
				if (xhr.readyState == 4) {
					var v = {};
        			try {        			
        				v = JSON.parse(xhr.responseText);		        			
        		    } catch(e){}
					
					if (xhr.status >= 200 && xhr.status < 400) {
						if (onSuccess)
							onSuccess(v || {});
	        		} else {
	        			if (onError)
	        				onError(v || {});
	        		}
	        		general.apply();
	        	}
			};
		}, 1000);
 	}
};

//angular app initialization and specs
angular.module('lwSearch',[])
	.controller('ctrl', general.ctrl)
	.filter('size', function() {
	    return function(x) {
	        var units = ["B", "kB", "MB", "GB"];
	        var i = 0;
	        while (x >= 1024) {
	        	x = x / 1024;
	        	i++;
	        }
	        
	        return x.toFixed(0) + " " + units[i];
	    };
	})
	.filter('cut', function() {
	    return function(x) {
	        return x && x.length > 200 ? x.substring(0, 200) + "..." : x;
	    };
	});
