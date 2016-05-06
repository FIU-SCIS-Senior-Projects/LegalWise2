/**
 * 
 */
var qa = {
	busy: false,
	qas: [],
	message: {
		show: false,
		isError: false,
		title: null,
		msg: null
	},
	init: function() {
		qa.addQa();
	},
	addQa: function() {
		qa.qas.push({
			question: null,
			answer: null,
			error: null
		});
	},
	removeQa: function(i)  {
		qa.qas.splice(i, 1);
	},
	submit: function() {
		qa.hideMessage();
		var e = false;
		angular.forEach(qa.qas, function(qA, i) {
			qA.error = null;
			var q = qA.question ? qA.question.trim() : null,
					a = qA.answer ? qA.answer.trim() : null;
					
			if (!q || !a) {
				qA.error = 
					"Both the question and answer fields must be filled";
				e = true;
			}
		});
		
		if (!e) {
			qa.busy = true;
			general.server.submit(qa.qas, 
				function(r) {
					qa.busy = false;
					qa.qas = [];
					qa.addQa();
					qa.showMessge(
							"Sucess",
			    			"The QA pairs were successfully submitted " + 
			    			"and processed",
			    			false);
				},
				function(e) {
					qa.busy = false;
					qa.showMessge(
							"There was a problem while submitting",
			    			e.msg || "No further information available",
			    			true);
				});
		}
	},
	showMessge: function(title, msg, error) {
		qa.message.show = true;
		qa.message.isError = error;
		qa.message.title = title;
		qa.message.msg = msg;
	},
	hideMessage: function() {
		qa.message.show = false;
		qa.message.isError = false;
		qa.message.title = null;
		qa.message.msg = null;
	}
};

var general = {
	ctrl: function($scope, $document) {
		$scope.qa = qa;
		$scope.serverUrl = window.serverUrl;
		general.scope = $scope;	
		
		// init
		qa.init();		
	},
	apply: function() {
 		var phase = general.scope.$root.$$phase;
		if (phase != '$apply' && phase != '$digest')
			general.scope.$apply();
 	},
 	server: {
 		submit: function(qas, onSuccess, onError) {
 			general._call("POST", 
 				"/service/qa", JSON.stringify(qas), onSuccess, onError);
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
angular.module('lwQa',[])
	.controller('ctrl', general.ctrl);