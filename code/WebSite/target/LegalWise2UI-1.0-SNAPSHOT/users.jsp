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
	ng-app="lwUsers" ng-controller="ctrl">
	<div class="row">
		<div class="col-sm-4">
			<div class="control">										
				<i class="fa fa-filter control-icon h4"></i>			
				<input type="text" class="small" placeholder="Filter" 
					ng-model="users.filter.text"
					ng-change="users.getUsers()" />
			</div>
		</div>
		<div class="col-sm-3">
			<div class="control">										
				<i class="fa fa-sort control-icon h4"></i>
				<select class="small">
					<option>Name</option>
					<option>Created On</option>
				</select>
			</div>
		</div>
	</div>
	<hr />
	<div ng-if="users.busy" class="text-center">
		<h1><i class="fa fa-spinner fa-spin"></i></h1>
		<p class="margin-bottom-lg">Obtaining users...</p>
	</div>
	<div ng-if="users.message.show" class="text-center" 
		ng-class="{'text-red': users.message.isError,
			'text-green': !users.message.isError}">
		<h3>
			<i ng-if="!users.message.isError" class="fa fa-check"></i>
			<i ng-if="users.message.isError" class="fa fa-warning"></i>
		</h3>
		<h5 ng-bind="users.message.title"></h5>
		<p class="margin-bottom-lg" ng-bind="users.message.msg"></p>
	</div>
	<div ng-if="!users.busy">
		<table class="h6">
			<tr>
				<th></th>
				<th>First Name</th>
				<th>Last Name</th>
				<th>Company</th>
				<th>Email</th>
				<th>Created On</th>
				<th>Is Trial?</th>
				<th class="text-centrer">Status</th>
				<th></th>
			</tr>
			<tr ng-if="!users.users.length" class="text-gray text-center">
				<td colspan="9">No users to show</td>
			</tr>
			<tbody ng-repeat="user in users.users">
				<tr ng-if="users.actionFor.userId != user.userId">
					<td ng-switch="user.type">
						<span ng-switch-when="GUEST" 
							class="h7 bck-gray text-white"
							>&nbsp;GUEST&nbsp;</span>
						<span ng-switch-when="REGULAR" 
							class="h7 bck-green text-white"
							>&nbsp;REGULAR&nbsp;</span>
						<span ng-switch-when="ADMIN" 
							class="h7 bck-red text-white"
							>&nbsp;ADMIN&nbsp;</span>
						<span ng-switch-when="SUPER"
							class="h7 bck-blue text-white"
							>&nbsp;SUPER&nbsp;</span>
					</td>
					<td ng-bind="user.firstName"></td>
					<td ng-bind="user.lastName"></td>
					<td ng-bind="user.companyName"></td>
					<td>
						<a ng-href="{{'mailto:' + user.email}}" target="_blank"
							ng-bind="user.email"></a>
					</td>
					<td ng-bind="user.createdOn | date:'MM/dd/yyyy hh:mm a'"></td>
					<td>
						<span ng-bind="user.isTrial ? 'Yes' : 'No'"
							ng-class="{'text-red': user.isTrial}"></span>
						<span ng-if="user.isTrial" 
							ng-bind="', ends ' + 
								(user.createdOn 
									| ends:user.trialDuration
									| date:'MM/dd')"></span>
					</td>
					<td class="h4 text-centrer">
						<span ng-if="user.isLocked">
							<i title="Locked"
								class="fa fa-lock text-red"></i>
						</span>
						<span ng-if="!user.isLocked">							
							<i ng-if="user.isActive" title="Active"
								class="fa fa-check-circle text-green"></i>
							<i ng-if="!user.isActive" title="Inactive"
								class="fa fa-ban text-gray"></i>
						</span>						
					</td>					
					<td>
						<i ng-if="users.workingOn == user.userId"
							class="fa fa-spinner fa-spin"></i>
						<div class="menu"
							lw-prevent="click"
							ng-if="users.workingOn != user.userId">
							<button ng-click="users.openMenu(user)"
								type="button" class="small">							
								<i class="fa fa-ellipsis-h"></i>
							</button>
							
							<ul ng-if="users.menuFor == user.userId" 
								class="menu-list h5 text-one-line">
								<li ng-if="!user.isActive"
								ng-click="users.startAction(user, 'activate')">
									<i class="fa fa-check-circle fa-fw"></i>
									&nbsp;&nbsp;Activate
								</li>
								<li ng-if="user.isActive"
								ng-click="users.startAction(user, 'inactivate')">
									<i class="fa fa-ban fa-fw"></i>
									&nbsp;&nbsp;Inactivate
								</li>
								<li ng-if="!user.isLocked"
								ng-click="users.startAction(user, 'lock')">
									<i class="fa fa-lock fa-fw"></i>
									&nbsp;&nbsp;Lock
								</li>
								<li ng-if="user.isLocked"
								ng-click="users.startAction(user, 'unlock')">
									<i class="fa fa-unlock fa-fw"></i>
									&nbsp;&nbsp;Unlock
								</li>
							</ul>
						</div>
					</td>
				</tr>
				<tr ng-if="users.actionFor.userId == user.userId">
					<td ng-switch="users.actionFor.action" 
						colspan="9" class="text-right">
						<div ng-switch-when="activate">
							Are you sure you wish to activate
							<b ng-bind="user.firstName + ' ' + 
								user.lastName"></b>
							<button type="button"
								ng-click="users.activate(user)" 
								class="small red auto">Yes</button>	
							<button type="button" 
								ng-click="users.cancelAction()"
								class="small auto">No</button>
						</div>
						<div ng-switch-when="inactivate">
							Are you sure you wish to inactivate
							<b ng-bind="user.firstName + ' ' + 
								user.lastName"></b>
							<button type="button" 
								ng-click="users.inactivate(user)"
								class="small red auto">Yes</button>	
							<button type="button" 
								ng-click="users.cancelAction()"
								class="small auto">No</button>
						</div>
						<div ng-switch-when="lock">
							Are you sure you wish to lock
							<b ng-bind="user.firstName + ' ' + 
								user.lastName"></b>
							<button type="button" 
								ng-click="users.lock(user)"
								class="small red auto">Yes</button>	
							<button type="button" 
								ng-click="users.cancelAction()"
								class="small auto">No</button>
						</div>
						<div ng-switch-when="unlock">
							Are you sure you wish to unlock
							<b ng-bind="user.firstName + ' ' + 
								user.lastName"></b>
							<button type="button" 
								ng-click="users.unlock(user)"
								class="small red auto">Yes</button>	
							<button type="button" 
								ng-click="users.cancelAction()"
								class="small auto">No</button>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
</jsp:body>
<jsp:attribute name="js">
	<script src="public/js/users.js"></script>
</jsp:attribute>
</t:master>