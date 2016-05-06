/**
 * LegalWise 2.0 Scripts for Documents page.
 * @author	Fernando Gomez Reyes
 */
// handles work with documents
// Document:
//		Status: -1: error, 0: uploading, 
//				1: pending activation, 2: active
var documents = {
	busy: false,
	documents: [],
	offset: 0,
	textFilter: null,
	message: {
		show: false,
		isError: false,
		title: null,
		msg: null
	},
	init: function() {
		documents.getDocs();
	},
	getDocs: function(page) {
		documents.busy = true;
		general.server.getDocs(
			page, 
			documents.textFilter, 
			function(d) {
				documents.busy = false;
				documents.documents = page ? 
						documents.documents.concat(d) : d;
			},
			function(e) {
				documents.busy = false;
				documents.showMessge(
    				"There was a problem while obtaining documents",
    				e.msg || "No further information available",
    				true);
			});
	},
	upload: function(files) {
		angular.forEach(files, function(f, i) {
			var nm = (f.name || "").toLowerCase(),
				type = nm.endsWith(".pdf") ? "pdf" :
					nm.endsWith(".docx") ? "docx" : "Invalid format",			
				status = type == "pdf" || type == "docx" ? 0 : -1;
			var doc = documents.toDocument(f);
			documents.add(doc);
			
			// go upload
			if (status == 0)
				documents._upload(doc, f);
		});
	},
	toDocument: function(f) {
		return {
			documentId: null,
			uploadedOn: null,
			uploadedBy: null,
			file: {
				fileId: null,
				name: f.name,
				size: f.size,
				mimeType: f.type
			},
			plainText: null,
			status: "UPLOADING"
		};
	},
	add: function(d) {
		documents.documents.unshift(d);
	},
	showMessge: function(title, msg, error) {
		documents.message.show = true;
		documents.message.isError = error;
		documents.message.title = title;
		documents.message.msg = msg;
	},
	hideMessage: function() {
		documents.message.show = false;
		documents.message.isError = false;
		documents.message.title = null;
		documents.message.msg = null;
	},
	_upload: function(doc, file) {
		var fd = new FormData();
		fd.append("file", file, file.name);
		
		general.server.upload(fd, 
			function(v) {
				doc.documentId = v.documentId;
				doc.uploadedOn = v.uploadedOn;
				doc.uploadedBy = v.uploadedBy;
				doc.file = v.file;
				doc.plainText = v.plainText;
				doc.status = v.status;
			}, 
			function(r) {
				doc.status = "ERROR";
			});
	}
};

// general object, main functionality
var general = {
	ctrl: function($scope) {
		$scope.documents = documents;
		$scope.serverUrl = window.serverUrl;
		general.scope = $scope;	
		
		documents.init();
	},
	apply: function() {
 		var phase = general.scope.$root.$$phase;
		if (phase != '$apply' && phase != '$digest')
			general.scope.$apply();
 	},
 	server: {
 		upload: function(formdata, onSuccess, onError) {
 			general._call("POST", "/service/upload",
 					formdata, onSuccess, onError);
 		},
 		getDocs: function(offset, textFilter, onSuccess, onError) {
 			general._call("GET", 
 				"/service/documents?offset=" + (offset || 0) + 
 					"&textFilter=" + (textFilter || ""), 
 				"", onSuccess, onError);
 	 	}
 	},
 	_call: function(method, serviceUrl, body, onSuccess, onError) {
 		var xhr = new XMLHttpRequest();
		xhr.open(method, 
				(general.scope.serverUrl || "") + serviceUrl, true);
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
angular.module('lwDocuments',[])
	.controller('ctrl', general.ctrl)
	.directive('lwDrop', function() {
		return {
			restrict : 'A',
			link : function(scope, elem, attrs) {
				var cnt = 0;
				var cancelEvents = function(event) {
					event.preventDefault();  
					event.stopPropagation();
				},
				activate = function(event) {
					cancelEvents(event);
					cnt++;
					elem.addClass("active");
				},
				deactivate =  function(event) {
					cancelEvents(event);
					cnt--;
					if (cnt == 0)
						elem.removeClass("active");
				},
				submit = function(event) {
					cancelEvents(event);
					elem.removeClass("active");
					documents.upload(
						event.originalEvent.dataTransfer.files);
					general.apply();
				};
				
				$.event.props.push('dataTransfer');
				elem.on("dragover", cancelEvents);
				elem.on("dragenter", activate);
				elem.on("dragleave", deactivate);
				elem.on("drop", submit);
				return elem;
			}
		};
	})
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
	});
