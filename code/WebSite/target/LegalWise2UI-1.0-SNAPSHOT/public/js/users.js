/**
 * LegalWise 2.0 Scripts for Users page.
 * @author	Fernando Gomez Reyes
 */
var users = {
	busy: false,
	users: [],
	workingOn: null,
	menuFor: null,
	filter: {
		text: null
	},
	actionFor: {
		userId: null,
		action: null
	},
	message: {
		show: false,
		isError: false,
		title: null,
		msg: null
	},
	init: function() {
		users.getUsers();
	},
	getUsers: function(page) {
		users.busy = true;		
		if (!page)
			users.users = [];
		
		general.server.getUsers(
			page || null, 
			users.filter.text || null,
			function(u) {
				users.busy = false;
				users.users = page ? users.users.concat(u) : u;
			},
			function(e) {
				users.busy = false;
				users.showMessge(
    				"There was a problem while obtaining users",
    				e.msg || "No further information available",
    				true);
			});
	},
	openMenu: function(user) {
		users.menuFor = user.userId;
	},
	closeMenu: function() {
		users.menuFor = null;
	},
	startAction: function(user, action) {
		users.actionFor.userId = user.userId;
		users.actionFor.action = action;
	},
	cancelAction: function() {
		users.actionFor.userId = null;
		users.actionFor.action = null;
	},
	lock: function(user) {
		user.isLocked = true;
		users.cancelAction();
		users.update(user);
	},
	unlock: function(user) {
		user.isLocked = false;
		users.cancelAction();
		users.update(user);
	},
	activate: function(user) {
		user.isActive = true;
		users.cancelAction();
		users.update(user);
	},
	inactivate: function(user) {
		user.isActive = false;
		users.cancelAction();
		users.update(user);
	},
	showMessge: function(title, msg, error) {
		users.message.show = true;
		users.message.isError = error;
		users.message.title = title;
		users.message.msg = msg;
	},
	hideMessage: function() {
		users.message.show = false;
		users.message.isError = false;
		users.message.title = null;
		users.message.msg = null;
	},
	update: function(user) {		
		users.workingOn = user.userId;
		general.server.update(user, 
			function() {
				users.workingOn = null;
				users.messgae = user;
				users.showMessge("User successfully updated", "", false);
				setTimeout(function() {
        			users.hideMessage();
	        		general.apply();
        		}, 8000);
			}, 
			function(e) {
				users.workingOn = null;
				users.messgae = user;
				users.showMessge(
    		    	"Could not update user",
    				e.msg || "No further information available",
    				true);
				setTimeout(function() {
        			users.hideMessage();
	        		general.apply();
        		}, 8000);
			});
	}
};

// general object, main functionality
var general = {
	ctrl: function($scope, $document) {
		$scope.users = users;
		general.scope = $scope;	
		
		// init
		users.init();
		
		// click
		$document.on("click", function() {
			users.closeMenu();
			general.apply();
		});
	},
	apply: function() {
 		var phase = general.scope.$root.$$phase;
		if (phase != '$apply' && phase != '$digest')
			general.scope.$apply();
 	},
 	server: {
 		update: function(user, onSuccess, onError) {
 			general._call("POST", "/service/user/update",
 				JSON.stringify(user), onSuccess, onError);
 		},
 		getUsers: function(offset, textFilter, onSuccess, onError) {
 			general._call("GET", 
 				"/service/users?offset=" + (offset || 0) + 
 					"&textFilter=" + (textFilter || ""), 
 				"", onSuccess, onError);
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

// angular app initialization and specs
angular.module('lwUsers',[])
	.controller('ctrl', general.ctrl)
	.directive('lwPrevent', function() {
		return {
			restrict : 'A',
			link : function(scope, elem, attrs) {
				elem.on(attrs.lwPrevent, function(e) {
					console.log("ddd");
					e.stopPropagation();
				});
			}
		};
	})
	.filter('ends', function() {
	    return function(from, duration) {
	        return new Date(from + (duration * 24 * 60 * 60 * 1000));
	    };
	});