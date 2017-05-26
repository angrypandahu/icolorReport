<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<a href="#create-report" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                               default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>

        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="create-report" class="content scaffold-create" role="main">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${this.report}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.report}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form action="save">
        <fieldset class="form">
            <f:with bean="report">
                <g:hiddenField name="user" value="${user?.id}"/>
                <div class="fieldcontain required">
                    <label for="reportDate">reportDate
                        <span class="required-indicator">*</span>
                    </label><input name="reportDate" required="" id="reportDate" type="date"
                                   value="${report.reportDate}"/>
                </div>
                <f:field property="content"/>
                <g:if test="${user.reportGroup.name == 'GROUP_ICOLOR'}">
                    <f:field property="question"/>
                    <f:field property="share"/>
                    <g:hiddenField name="workHours" value="0"/>
                </g:if>
                <g:if test="${user.reportGroup.name == 'GROUP_WAIBAO'}">
                    <f:field property="workHours"/>
                </g:if>
            </f:with>
        </fieldset>
        <fieldset class="buttons">
            <g:submitButton name="create" class="save"
                            value="${message(code: 'default.button.create.label', default: 'Create')}"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
