<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html"
    pageEncoding="UTF-8" %>

<c:set var="url" scope="session">
<%
String uri = request.getRequestURI();
out.print(uri.substring(uri.lastIndexOf("/") + 1));
%>
</c:set>

<div class="content none">
	<ul class="tabs">
		<li class="${ url eq 'index.jsp' ? 'bck-white' : 'bck-gray' }">
			<a href="index">Search</a>
		</li>
		<c:if test="${ requestScope['userType'] == 'ADMIN' ||
			requestScope['userType'] == 'SUPER' }">			
			<li class="${ url eq 'documents.jsp' ? 'bck-white' : 'bck-gray' }">
				<a href="documents">Documents</a>
			</li>
			<li class="${ url eq 'users.jsp' ? 'bck-white' : 'bck-gray' }">
				<a href="users">Users</a>
			</li>
			<li class="${ url eq 'qa.jsp' ? 'bck-white' : 'bck-gray' }">
				<a href="qa">Questions</a>
			</li>
		</c:if>
	</ul>
</div>