<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>
    <style>
    .controller {
        color: blue;
    }
    </style>
</head>

<body>


<div class="svg" role="presentation">
    <div class="grails-logo-container">
        <asset:image src="grails-cupsonly-logo-white.svg" class="grails-logo"/>
    </div>
</div>

<div id="content" role="main">
    <section class="row colset-2-its">

        <div id="controllers" role="navigation">

            <h2 class="controller">Functions:</h2>

            <ul>
                <li class="controller">
                    <g:link controller="ganttTasks" class="controller">Task</g:link>
                </li>
                <li class="controller">
                    <g:link controller="report" class="controller">Report</g:link>
                </li>
                <li class="controller">
                    <g:link controller="weekly" class="controller">WeeklyReport</g:link>
                </li>
                <li class="controller">
                    <g:link controller="user" class="controller" action="me">UserInfo</g:link>
                </li>
                <li class="controller">
                    <g:link controller="login" class="controller">Login</g:link>
                </li>
                <li class="controller">
                    <g:link controller="logout" class="controller">Logout</g:link>
                </li>
            </ul>

        </div>
    </section>
</div>

</body>
</html>
