<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master>
<jsp:attribute name="header">
	<jsp:include page="header.jsp"/>
</jsp:attribute>
<jsp:attribute name="hasHeader">${ true }</jsp:attribute>
<jsp:body>
<jsp:include page="tabs.jsp"/> 
<div class="content bck-white" ng-app="lwQa" ng-controller="ctrl">
	<div class="row">
		<h4 class="col-sm-9">
			Question-Answer Pairs
		</h4>
		<div class="col-sm-3" ng-if="qa.qas.length">
			<button type="button" class="small control green"
					ng-click="qa.submit()">
				<i class="fa fa-check control-icon h4"></i>
				Submit
			</button>
		</div>
	</div>
	
	<div ng-if="qa.busy" class="text-center">
		<h1><i class="fa fa-spinner fa-spin"></i></h1>
		<p class="margin-bottom-lg">Submitting...</p>
	</div>

	<div ng-if="qa.message.show" class="text-center" 
		ng-class="{'text-red': qa.message.isError,
			'text-green': !qa.message.isError}">
		<h3>
			<i ng-if="!qa.message.isError" 
				class="fa fa-check-circle"></i>
			<i ng-if="qa.message.isError" 
				class="fa fa-warning"></i>
		</h3>
		<h4 ng-bind="qa.message.title"></h4>
		<p class="margin-bottom-lg" ng-bind="qa.message.msg"></p>
	</div>
	
	<div ng-repeat="q in qa.qas">
		<hr />
		<div class="row margin-bottom-xs" ng-if="q.error">
			<div class="col-sm-3">&nbsp;</div>
			<h5 class="col-sm-9 text-red text-center" >
				<i class="fa fa-warning"></i>
				<span ng-bind="q.error"></span>
			</h5>
		</div>
		<div class="row">
			<div class="col-sm-3">
				Question
			</div>
			<div class="col-sm-9">
				<div class="row">
					<div class="col-xs-10">
						<div class="control">										
							<i class="fa fa-question control-icon h4"></i>			
							<input type="text" class="small" 
									placeholder="Question"
									ng-disabled="qa.busy"
									ng-model="q.question"/>
						</div>
					</div>
					<div class="col-xs-2" ng-if="qa.qas.length > 1">
						<button type="button" class="small red"
								ng-disabled="qa.busy"
								ng-click="qa.removeQa($index)">
							<i class="fa fa-remove"></i>
						</button>
					</div>
				</div>			
			</div>			
		</div>
		<div class="row">
			<div class="col-sm-3">
				Answer
			</div>
			<div class="col-sm-9">
				<div class="control">										
					<i class="fa fa-lightbulb-o control-icon h4 top"></i>			
					<textarea class="small" placeholder="Answer"
							ng-disabled="qa.busy"
							rows="2" ng-model="q.answer"></textarea>
				</div>
			</div>
		</div>
	</div>
	<hr />
	<div class="row">
		<h4 class="col-sm-9">
			&nbsp;
		</h4>
		<div class="col-sm-3" ng-if="!qa.busy">
			<button type="button" class="small control blue"
					ng-if="qa.qas.length <= 200"
					ng-click="qa.addQa()">
				<i class="fa fa-plus control-icon h4"></i>
				Add QA Pair
			</button>
		</div>
	</div>
</div>
</jsp:body>
<jsp:attribute name="js">
	<script src="public/js/qa.js"></script>
</jsp:attribute>
</t:master>