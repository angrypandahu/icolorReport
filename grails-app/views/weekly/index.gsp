<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'weekly.label', default: 'Weekly')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <asset:javascript src="fp.js"/>
    <asset:stylesheet src="fp.css"/>
</head>

<body>
<filterpane:filterPane domain="com.domain.report.Weekly" associatedProperties="user.username" dialog="true"/>
<a href="#list-weekly" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                             default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><filterpane:filterButton text="Search"/></li>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>

        <li><g:link class="group" params="${filterParams}" action="createGroupWeekly"><g:message
                code="default.download.label" args="[entityName]"/></g:link></li>

    </ul>
</div>

<div id="list-weekly" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <f:table collection="${weeklyList}" properties="['id', 'user', 'content', 'reportDate', 'schedules', 'isSend']"/>

    <div class="pagination">
        <g:paginate total="${weeklyCount ?: 0}" params="${filterParams}"/>
        <g:totalCount total="${weeklyCount ?: 0}"/>
    </div>
</div>
</body>
</html>