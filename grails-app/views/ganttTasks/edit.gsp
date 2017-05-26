<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'ganttTasks.label', default: 'GanttTasks')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<a href="#edit-ganttTasks" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                 default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>

        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <sec:access expression="hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')  ">
            <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                                  args="[entityName]"/></g:link></li>
        </sec:access>
    </ul>
</div>

<div id="edit-ganttTasks" class="content scaffold-edit" role="main">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${this.ganttTasks}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.ganttTasks}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form resource="${this.ganttTasks}" method="PUT">
        <g:hiddenField name="version" value="${this.ganttTasks?.version}"/>
        <fieldset class="form">
            <f:with bean="ganttTasks">
            %{--<f:field property="type"/>--}%
                <f:field property="text"/>
                <f:field property="startDate"/>
                <f:field property="endDate"/>
                <f:field property="progress"/>
                <sec:access expression="hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')  ">
                    <f:field property="users"/>
                </sec:access>
            </f:with>
        </fieldset>
        <fieldset class="buttons">
            <input class="save" type="submit"
                   value="${message(code: 'default.button.update.label', default: 'Update')}"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
