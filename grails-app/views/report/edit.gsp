<%@ page import="org.springframework.validation.FieldError" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<a href="#edit-report" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                             default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>

        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="edit-report" class="content scaffold-edit" role="main">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${this.report}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.report}" var="error">
                <li <g:if test="${error in FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form resource="${this.report}" method="PUT">
        <g:hiddenField name="version" value="${this.report?.version}"/>
        <fieldset class="form">
            <f:with bean="report">
                <g:myInput inputType="date" field="reportDate" val="${report.getReportDate().toDate()}"/>
                <f:field property="content"/>
                <g:if test="${user.reportGroup.name == 'GROUP_ICOLOR'}">
                    <f:field property="question"/>
                    <f:field property="share"/>
                </g:if>
                <g:if test="${user.reportGroup.name == 'GROUP_WAIBAO'}">
                    <f:field property="workHours"/>
                </g:if>
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
