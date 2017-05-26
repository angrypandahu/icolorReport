<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'ganttTasks.label', default: 'GanttTasks')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
    <asset:javascript src="jquery-2.1.4.min.js"/>
    <script type="text/javascript">
        function ajaxRemoveUser(taskId, userId) {
            var data = {};
            data['user.id'] = userId;
            $.ajax({
                url: '/ganttTasks/removeUser/' + taskId, type: 'POST', data: data, success: function (res, status) {
                    console.log(res);
                    var replaceHtml = $(res).html();
                    $("#removeUserId").html(replaceHtml);
                }, error: function (res) {
                }
            });

        }
    </script>

</head>

<body>
<a href="#show-ganttTasks" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
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

<div id="show-ganttTasks" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <f:with bean="ganttTasks">
        <ol class="property-list ganttTasks">
            <f:display property="text" wrapper="bootstrap3"/>
            <f:display property="progress"/>
            <f:display property="startDate"/>
            <f:display property="endDate"/>
            <sec:access expression="hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')  ">
                <g:render template="editUsers" model="[task: ganttTasks]"/>
            </sec:access>

        </ol>
    </f:with>


    <g:form resource="${this.ganttTasks}" method="DELETE">
        <fieldset class="buttons">
            <g:link class="edit" action="edit" resource="${this.ganttTasks}"><g:message code="default.button.edit.label"
                                                                                        default="Edit"/></g:link>
            <sec:access expression="hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')  ">
                <input class="delete" type="submit"
                       value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                       onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
            </sec:access>
        </fieldset>
    </g:form>
</div>
</body>
</html>
