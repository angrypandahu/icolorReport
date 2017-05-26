<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'weekly.label', default: 'Weekly')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
    <script type="text/javascript">
        function getReportDate() {
            var val = $('#reportDate').val();
            var $createWeeklyId = $('a[class="create"]');
            var href = $createWeeklyId.attr("href");
            $createWeeklyId.attr("href", href + "?reportDate=" + val);

        }
    </script>
</head>

<body>
<a href="#create-weekly" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                               default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>

        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create" onclick="getReportDate()"><g:message
                code="default.new.label" args="[entityName]"/></g:link></li>

    </ul>
</div>

<div id="create-weekly" class="content scaffold-create" role="main">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${this.weekly}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.weekly}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form action="save">
        <fieldset class="form">
            <f:with bean="weekly">
                <g:hiddenField name="user" value="${weekly.user.id}"/>
                <div class="fieldcontain required">
                    <label for="reportDate">reportDate
                        <span class="required-indicator">*</span>
                    </label><input name="reportDate" required="" id="reportDate" type="date"
                                   value="${weekly.reportDate}"/>
                </div>
                <f:field property="content"/>
                <f:field property="question"/>
                <f:field property="isSend"/>

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
