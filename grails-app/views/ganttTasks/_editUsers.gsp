<li class="fieldcontain" id="removeUserId">
    <span id="users-label" class="property-label">Users</span>

    <div class="property-value" aria-labelledby="users-label"><ul>
        <g:each in="${task.users}" var="user">
            <li><a href="/user/show/${user.id}">${user.displayName}</a>&nbsp; <a
                    onclick="ajaxRemoveUser(${task.id}, ${user.id})">Delete</a></li>
        </g:each>

    </ul></div>
</li>