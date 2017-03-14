<g:if test="${!notRequired}">
    <div class="fieldcontain required">
        <label for="${field}">${label}
            <span class="required-indicator">*</span>
        </label><input id="${field}" name="${field}" type="${inputType}" required="required" value="${val}"/>
    </div>
</g:if>

<g:if test="${notRequired}">
    <div class="fieldcontain">
        <label for="${field}">${label}
            <span class="required-indicator"> </span>
        </label>
        <input id="${field}" name="${field}" type="${inputType}" value="${val}"/>
    </div>
</g:if>
