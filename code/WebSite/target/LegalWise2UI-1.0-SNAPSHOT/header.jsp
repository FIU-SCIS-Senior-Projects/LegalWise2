<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html"
    pageEncoding="UTF-8" %>

<div class="content short header">
	<h4 class="text-one-line">
		Legal<span class="text-red">Wise</span>
		<span class="text-red">2.0</span>
	</h4>
	<div class="user-spot">		
		<span class="fa-stack fa-lg fa-pull-right">
			<i class="fa fa-circle fa-stack-2x text-green"></i>
			<i class="fa fa-user fa-stack-1x"></i>
		</span>
		<div class="fa-pull-right text-right text-one-line">
			<c:set var="type" value="${ requestScope['userType'] }" />
			<c:choose>
			    <c:when test="${ type == 'SUPER' }">
					<span class="h7 bck-blue text-white"
						>&nbsp;SUPER&nbsp;</span>
			    </c:when>
			    <c:when test="${ type == 'ADMIN' }">
			        <span class="h7 bck-red text-white"
			        	>&nbsp;ADMIN&nbsp;</span>
			    </c:when>
			    <c:when test="${ type == 'REGULAR' }">
			        <span class="h7 bck-green text-white"
			        	>&nbsp;REGULAR&nbsp;</span>
			    </c:when>
			    <c:when test="${ type == 'GUEST' }">
			        <span class="h7 bck-gray text-white"
			        	>&nbsp;GUEST&nbsp;</span>
			    </c:when>
			</c:choose>			
			<span class="h6">
				<c:out value="${ requestScope['userFirstName'] }" />
				<c:out value="${ requestScope['userLastName'] }" />
			</span><br/>
			<span class="h6">
				<a href="#" ng-disabled="true">Profile</a> | 
				<a href="logout">Logout</a>
			</span>
		</div>
	</div>
</div>      