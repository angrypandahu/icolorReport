<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <asset:javascript src="fp.js"/>
    <asset:stylesheet src="fp.css"/>
</head>

<body>
<filterpane:filterPane domain="com.domain.report.Report" associatedProperties="user.username" dialog="true"/>

<a href="#list-report" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                             default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><filterpane:filterButton text="Search"/></li>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
        <sec:access expression="hasRole('ROLE_ADMIN')">
            <li><g:link class="group" action="createGroupReport" params="${filterParams}"><g:message
                    code="default.new.label" args="['GroupReport']"/></g:link></li>
        </sec:access>
        <sec:access expression="hasRole('ROLE_MENGTUO')">
            <li><g:link class="group" action="createMonthReport" params="${filterParams}"><g:message
                    code="default.new.label" args="['MonthReport']"/></g:link></li>
        </sec:access>
    </ul>
</div>

<div id="list-report" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:if test="${user.reportGroup.name == 'GROUP_ICOLOR'}">
        <f:table collection="${reportList}"
                 properties="['id', 'user', 'content', 'question', 'share', 'reportDate', 'isSend']"/>
    </g:if>
    <g:if test="${user.reportGroup.name == 'GROUP_WAIBAO'}">
        <f:table collection="${reportList}"
                 properties="['id', 'user', 'content', 'workHours', 'reportDate', 'isSend']"/>
    </g:if>

    <div class="pagination">

        <g:paginate total="${reportCount ?: 0}" params="${filterParams}"/>
        <g:totalCount total="${reportCount ?: 0}"/>
        <export:formats formats="['excel', 'rtf']" params="${filterParams}"/>
    </div>
</div>
</body>
</html>