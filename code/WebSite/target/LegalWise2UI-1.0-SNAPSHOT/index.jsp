<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master>
<jsp:attribute name="header">
	<jsp:include page="header.jsp"/>
</jsp:attribute>
<jsp:attribute name="hasHeader">${ true }</jsp:attribute>
<jsp:body>
<jsp:include page="tabs.jsp"/> 
<div class="content bck-white"
	ng-app="lwSearch" ng-controller="ctrl">
	<div class="margin-bottom-lg text-center">
		<h1>
			Legal<span class="text-red">Wise</span>
			<span class="text-red">2.0</span>
		</h1>
	</div>
	<div class="control margin-bottom-lg">										
		<i class="fa fa-search control-icon h4"></i>						
		<input type="text"
			ng-model="search.query"
			ng-keypress="search.search($event)"
			autocomplete="off" 
			placeholder="Search LegalWise 2.0">
	</div>
	<div class="row">
		<div class="col-sm-9">
			<div ng-if="search.busy" class="text-center">
				<h1><i class="fa fa-spinner fa-spin"></i></h1>
				<p class="margin-bottom-lg">Searching...</p>
			</div>
		
			<div ng-if="search.message.show" class="text-center" 
				ng-class="{'text-red': search.message.isError,
					'text-green': !search.message.isError}">
				<h3>
					<i ng-if="!search.message.isError" 
						class="fa fa-check"></i>
					<i ng-if="search.message.isError" 
						class="fa fa-warning"></i>
				</h3>
				<h5 ng-bind="search.message.title"></h5>
				<p class="margin-bottom-lg" ng-bind="search.message.msg"></p>
			</div>
		
			<div ng-if="!search.result" class="text-gray text-center">			
				<h4>
					Enter the name of a document, a type of case, 
					a legal question, etc.
				</h4>		
				<p class="margin-bottom-lg">
					The results of your search will show here.	
				</p>
				<p class="margin-bottom-lg">
					<i class="fa fa-search fa-3x fa-border "></i>
				</p>
			</div>
			
			<div ng-if="search.result">
				<h4 class="margin-bottom-lg text-center">
					<span ng-bind="search.result.numFound || 0"></span>					
					<span class="text-gray">results found</span>
				</h4>
				
				<div ng-repeat="doc in search.result.docs"
						class="result-row margin-bottom-md">
					<div class="row" 
						ng-class="{'qa-row': search.isQa(doc)}">
						<div class="col-sm-9">
							<h5 class="margin-bottom-xs text-one-line"								 
								ng-attr-title="{{doc.title[0]}}">
								<span ng-if="search.getAnswerType(doc) == 'HN'" 
									class="bck-orange text-white h6">
									&nbsp;HN&nbsp;</span>
								<span ng-if="search.getAnswerType(doc) == 'QA'" 
									class="bck-green text-white h6">
									&nbsp;QA&nbsp;</span>
								<span ng-bind="doc.title[0]"></span>
							</h5>													
							<p class="h6 text-gray margin-bottom-sm">
								<span class="text-pre" 
									ng-if="doc.showAll"
									ng-bind="doc.body[0]">								
								</span>
								<a href="javascript:void(0)"
									ng-if="doc.showAll"
									ng-click="doc.showAll = false">
									Read less...
								</a>
								
								<span class="text-pre" 
									ng-if="!doc.showAll"
									ng-bind="doc.body[0] | cut">								
								</span>
								<a href="javascript:void(0)"
									ng-if="doc.body[0].length > 200 &&
										!doc.showAll"
									ng-click="doc.showAll = true">
									Read more...
								</a>								
							</p>
							<p class="h6 text-one-line"
								ng-if="doc.author[0]">
								<a ng-href="{{serverUrl
								}}/service/download?fileId={{doc.author[0]}}"
								ng-bind="doc.bibliography[0]"></a>
							</p>
						</div>
						<div class="col-sm-3 text-right"
							ng-if="doc.author[0]">
							<a ng-href="{{serverUrl
								}}/service/download?fileId={{doc.author[0]}}"
								class="button blue small auto"
								title="Download">
								<i class="fa fa-download"></i>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-3">
			<h5>
				<i class="fa fa-clock-o fa-fw"></i> Recent
			</h5>
			<hr class="small"/>
			<div class="margin-bottom-lg">
				<p class="text-gray " ng-if="!search.history.length">
					Nothing searched recently.		
				</p>
				<div ng-repeat="h in search.history | orderBy:'-performedOn'"
					class="margin-bottom-sm">
					<p class="text-one-line">
						<i class="fa fa-search"></i>
						<a href="javascript:void(0)" 
							ng-click="search.searchHistory(h)"
							ng-bind="h.searchText"></a>
					</p>					
					<p class="text-one-line h6"
						ng-bind="h.performedOn | date:'MM/dd/yyyy hh:mm a'">
					</p>
				</div>
			</div>
			
			<h5>
				<i class="fa fa-thumbs-o-up fa-fw"></i> Recommended
			</h5>
			<hr class="small"/>
			<p class="text-gray margin-bottom-lg">
				No recommendations yet.		
			</p>
		</div>
	</div>	
</div>
</jsp:body>
<jsp:attribute name="js">
	<script src="public/js/search.js"></script>
</jsp:attribute>
</t:master>