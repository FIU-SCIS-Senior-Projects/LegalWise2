<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="data.Connector" %>
<t:master>
<jsp:attribute name="header">
	<jsp:include page="header.jsp"/>
</jsp:attribute>
<jsp:attribute name="hasHeader">${ true }</jsp:attribute>
<jsp:body>
<jsp:include page="tabs.jsp"/>
<div class="content bck-white"
	ng-app="lwDocuments" ng-controller="ctrl">
	<div class="row">
		<div class="col-sm-4">
			<div class="control">										
				<i class="fa fa-filter control-icon h4"></i>			
				<input type="text" class="small" placeholder="Filter" />
			</div>
		</div>
		<div class="col-sm-3">
			<div class="control">										
				<i class="fa fa-sort control-icon h4"></i>
				<select class="small">
					<option>Name</option>
					<option>Uploaded On</option>
				</select>
			</div>
		</div>
		<div class="col-sm-2">
			&nbsp;
		</div>
		<div class="col-sm-3">
			<!-- 
			<button type="button" 
				class="small control blue">
				<i class="fa fa-upload control-icon h4"></i>
				Upload
			</button>
			 -->
		</div>
	</div>
	<hr />
	<div ng-if="documents.busy" class="text-center">
		<h1><i class="fa fa-spinner fa-spin"></i></h1>
		<p class="margin-bottom-lg">Obtaining documents...</p>
	</div>
	<div ng-if="documents.message.show" class="text-center" 
		ng-class="{'text-red': documents.message.isError,
			'text-green': !documents.message.isError}">
		<h3>
			<i ng-if="!documents.message.isError" class="fa fa-check"></i>
			<i ng-if="documents.message.isError" class="fa fa-warning"></i>
		</h3>
		<h5 ng-bind="documents.message.title"></h5>
		<p class="margin-bottom-lg" ng-bind="documents.message.msg"></p>
	</div>
	<div class="dropzone" lw-drop>
		<div class="text-center text-gray margin-bottom-xlg">
			<p ng-if="!documents.documents.length" >
				There are no documents to show at this time.
			</p>
			<h4 class="margin-bottom-lg">
				You may upload documents by dropping 
				them right here.
			</h4>
			<i class="fa fa-upload fa-3x fa-border"></i>
		</div>
		<div class="row" ng-if="documents.documents.length">		
			<div class="col-sm-12">			
				<div ng-repeat="doc in documents.documents"
					class="document pull-left">
					
					<p class="h7 margin-bottom-xs" 
						ng-switch="doc.file.mimeType">
						<span ng-switch-when="application/pdf"
						class="bck-red text-white">&nbsp;PDF&nbsp;</span>
						<span ng-switch-when="application/docx"
						class="bck-blue text-white">&nbsp;DOCX&nbsp;</span>
						<span ng-switch-default
							class="bck-gray text-white" 
							>&nbsp;<span 
							ng-bind="doc.file.mimeType"></span>&nbsp;</span>
					</p>
					<div class="row margin-bottom-xs">
						<h6 class="col-xs-3 text-gray">Name</h6>
						<h6 class="col-xs-9 text-one-line"
							ng-bind="doc.file.name" 
							ng-attr-title="{{doc.file.name}}"></h6>
					</div>
					<div class="row margin-bottom-xs">
						<h6 class="col-xs-3 text-gray">Size</h6>
						<h6 class="col-xs-9 text-one-line"
							ng-bind="doc.file.size | size" 
							ng-attr-title="{{doc.file.size | size}}"></h6>
					</div>	
					<div class="row margin-bottom-lg">
						<h6 class="col-xs-3 text-gray">Status</h6>
						<div class="col-xs-9 text-one-line h6"
							ng-switch="doc.status">
							<span ng-switch-when="'ERROR'"
								class="bck-red text-white">
								&nbsp;Error&nbsp;
							</span>
							<span ng-switch-when="UPLOADING"
								class="">
								<i class="fa fa-spin fa-spinner"></i> 
								Uploading&nbsp;
							</span>
							<span ng-switch-when="PENDING_ACTIVATION"
								class="bck-orange text-white">
								&nbsp;Pending Activation&nbsp;
							</span>
							<span ng-switch-when="ACTIVE"
								class="bck-green text-white">
								&nbsp;Active&nbsp;
							</span>
							<span ng-switch-when="INACTIVE"
								class="bck-gray text-white">
								&nbsp;Inactive&nbsp;
							</span>
							<span ng-switch-default
								class="bck-gray text-white">
								&nbsp;N/A&nbsp;
							</span>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-12">
							<a ng-href="{{serverUrl
							}}/service/download?fileId={{doc.file.fileId}}"
								class="button blue small control"
								title="Download">
								<i class="fa control-icon 
									fa-download"></i>
								Download
							</a>					
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="indicator text-center">
			<h5 class="margin-bottom-md">
				Drop Documents here<br>to upload them instantly.
			</h5>
			<div class="bounce">				
				<span class="fa-stack fa-4x" >
					 <i class="fa fa-cloud fa-stack-2x text-blue"></i>
					 <i class="fa fa-arrow-up fa-stack-1x text-white"></i>
				</span>
			</div>
		</div>
	</div>
</div>
</jsp:body>
<jsp:attribute name="js">
	<script src="public/js/documents.js"></script>
</jsp:attribute>
</t:master>