<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'ganttTasks.label', default: 'GanttTasks')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <asset:javascript src="fp.js"/>
    <asset:stylesheet src="fp.css"/>
</head>

<body>
<filterpane:filterPane domain="com.domain.gantt.GanttTasks" dialog="true"
                       associatedProperties="users.username"
                       filterPropertyValues="${['root':
                                                        [values: com.domain.gantt.GanttTasks.executeQuery('select t.text from GanttTasks t where t.type=\'project\' ')]]}"/>
<a href="#list-ganttTasks" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                 default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><filterpane:filterButton text="Search"/></li>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="task">Gantt Chart</g:link></li>
        <sec:access expression="hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')  ">
            <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                                  args="[entityName]"/></g:link></li>
        </sec:access>
    </ul>
</div>

<div id="list-ganttTasks" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <f:table collection="${ganttTasksList}" properties="['text', 'progress', 'startDate', 'endDate', 'users']"/>

    <div class="pagination">
        <g:paginate total="${ganttTasksCount ?: 0}" params="${filterParams}"/>
        <g:totalCount total="${ganttTasksCount ?: 0}"/>
        <export:formats formats="['excel']" params="${filterParams}"/>
    </div>
</div>
</body>
</html>