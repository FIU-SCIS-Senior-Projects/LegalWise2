<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master>
<jsp:attribute name="hasHeader">${ false }</jsp:attribute>
<jsp:body>
<div class="content small bck-white">
	<div class="login-bck hidden-sm hidden-xs"></div>
	<div class="row">
		<div class="col-md-6">
			<form action="login" method="post">
				<div style="min-height: 290px;">			
					<div class="margin-bottom-xlg text-center">
						<h3>
							<span class="fa-stack fa-lg">
								 <i class="fa fa-gavel fa-stack-1x"></i>
								 <i class="fa fa-circle-o 
								 	text-gray fa-stack-2x"></i>
							</span>
						</h3>
						<h4>
							Legal<span class="text-red">Wise</span>
							<span class="text-red">2.0</span>
						</h4>
					</div>
					<c:if test="${ error != null }">
						<div class="margin-bottom-sm">
							<div class="text-red control">
								<i class="fa fa-warning control-icon"></i>
								<div>${ error }</div>
							</div>
						</div>	
					</c:if>
					<div class="margin-bottom-sm">
						<div class="control">										
							<i class="fa fa-user control-icon h4"></i>	
							<input type="text" name="un"
								placeholder="Username">
						</div>
						<div class="control">										
							<i class="fa fa-key control-icon h4"></i>						
							<input type="password" name="pw" 
								placeholder="Password">
						</div>
					</div>
					<div class="row h6">
						<div class="col-xs-6 text-center">
							<a href="#">Forgot Username</a>
						</div>
						<div class="col-xs-6 text-center">
							<a href="#">Forgot Password</a>
						</div>
					</div>
				</div>
				<button type="submit" class="control blue">
					<i class="fa fa-lock control-icon h4"></i>
					Login
				</button>
			</form>
		</div>
		<div class="col-md-6">
			<div class="hidden-sm hidden-xs" style="min-height: 290px;" 
				class="text-center">
				<p class="margin-bottom-lg text-white">
					We provide speedy access to a lage database of Legal
					Documents.
				</p>
				<div class="row text-center">
					<div class="col-sm-4">
						<span class="fa-stack fa-2x text-white" >
							<i class="fa fa-question fa-stack-1x"></i>
							<i class="fa fa-comment-o fa-stack-2x"></i>
						</span>
						<h5 class="text-white">Ask</h5>
					</div>
					<div class="col-sm-4">
						<span class="fa-stack fa-2x text-white">
							<i class="fa fa-search fa-stack-2x"></i>
						</span>
						<h5 class="text-white">Search</h5>
					</div>
					<div class="col-sm-4">
						<span class="fa-stack fa-2x text-white">
							<i class="fa fa-download fa-stack-2x"></i>
						</span>
						<h5 class="text-white">Download</h5>
					</div>
				</div>
			</div>
			<p class="margin-bottom-md visible-sm visible-xs">&nbsp;</p>
			<button class="control green">
				<i class="fa fa-user-plus control-icon h4"></i>
				Register
			</button>		
		</div>
	</div>
</div>	   
</jsp:body>
</t:master>