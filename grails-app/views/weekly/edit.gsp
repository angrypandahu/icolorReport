<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'weekly.label', default: 'Weekly')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>

    </head>
    <body>
        <a href="#edit-weekly" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>

                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-weekly" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.weekly}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.weekly}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form resource="${this.weekly}" method="PUT">
                <g:hiddenField name="version" value="${this.weekly?.version}" />
                <fieldset class="form">
                    <f:with bean="weekly">
                        <div class="fieldcontain required">
                            <label for="reportDate">reportDate
                                <span class="required-indicator">*</span>
                            </label><input name="reportDate" required="" id="reportDate" type="date"
                                           value="${weekly.reportDate}"/>
                        </div>
                        <f:field property="content"/>
                        <f:field property="review"/>
                        <f:field property="isSend"/>
                        <f:field property="schedules"/>

                    </f:with>
                </fieldset>
                <fieldset class="buttons">
                    <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
