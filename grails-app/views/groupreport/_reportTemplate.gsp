<ol class="property-list content">
    <li class="fieldcontain">
        <span id="content-id" class="property-label"><g:message code="report.id.label" default="Id"/></span>

        <div class="property-value" aria-labelledby="content-id">${report.id}</div>
    </li>
    <li class="fieldcontain">
        <span id="content-reportDate" class="property-label"><g:message code="report.reportDate.label" default="ReportDate"/></span>

        <div class="property-value" aria-labelledby="content-reportDate"><g:formatDate date="${report.reportDate}" format="yyyy-MM-dd"/> </div>
    </li>
    <li class="fieldcontain">
        <span id="content-user" class="property-label"><g:message code="report.user.label"
                                                                   default="User"/></span>

        <div class="property-value" aria-labelledby="content-user">${report.user.username}</div>
    </li>
    <li class="fieldcontain">
        <span id="content-label" class="property-label"><g:message code="report.content.label"
                                                                   default="Content"/></span>

        <div class="property-value" aria-labelledby="content-label">${report.content}</div>
    </li>
    <li class="fieldcontain">
        <span id="question-label" class="property-label"><g:message code="report.question.label"
                                                                    default="Question"/></span>

        <div class="property-value" aria-labelledby="question-label">${report.question}</div>
    </li>
    <li class="fieldcontain">
        <span id="share-label" class="property-label"><g:message code="report.share.label"
                                                                    default="Share"/></span>

        <div class="property-value" aria-labelledby="share-label">${report.share}</div>
    </li>
</ol>