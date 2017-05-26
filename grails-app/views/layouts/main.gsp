<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    %{--<meta http-equiv="X-UA-Compatible" content="IE=edge"/>--}%
    <title>
        <g:layoutTitle default="IColor Report"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <asset:stylesheet src="bootstrap/css/bootstrap.min.css"/>
    <asset:stylesheet src="application.css"/>
    <asset:stylesheet src="bootstrap/css/font-awesome.min.css"/>
    <asset:stylesheet src="bootstrap/css/navbar.css"/>
    <asset:stylesheet src="bootstrap/css/stMenu.css"/>
    <style>
    #menu-12 i {
        margin-right: 5px;
    }

    </style>


    <g:layoutHead/>
</head>

<body>
<g:if test="${request.requestURI == '/'}">
    <a href="https://github.com/angrypandahu/icolorReport" target="_blank">
        <div id="fork-me">
            <p>Fork me on Github</p>
        </div>
    </a>
</g:if>
<div id="st-container" class="st-container st-effect-9">
    <nav class="st-menu st-effect-9" id="menu-12">
        <h2 class="icon icon-lab">Settingss</h2>
        <ul>
            <sec:ifLoggedIn>
                <li><g:link controller="user" class="controller" action="me" target="_blank"><i
                        class="glyphicon glyphicon-user"></i> <sec:loggedInUserInfo field="username"/></g:link></li>
                <li><g:link controller="user" class="controller" action="updatePassword" target="_blank"><i
                        class="glyphicon glyphicon-pencil"></i>password</g:link></li>
                <li><g:link controller="logout" class="controller"><i
                        class="glyphicon glyphicon-log-out"></i>Logout</g:link></li>
            </sec:ifLoggedIn>
            <sec:ifNotLoggedIn>
                <li><g:link controller="login" class="controller"><i
                        class="glyphicon glyphicon-log-in"></i>Login</g:link></li>
            </sec:ifNotLoggedIn>

        </ul>
    </nav>

    <div class="st-pusher">
        <div class="st-content">
            <div class="st-content">
                <div class="grailsnavbar grailsnavbar-default">
                    <div class="grailsnavbar-container">
                        <div class="grailsnavbar-header">
                            <a class="grailsnavbar-brand" href="/"><i class="fa grails-icon"><img
                                    src="/assets/grails-cupsonly-logo-white.svg"></i> IColorReport</a>
                        </div>
                        <a id="nav-icon" href="javascript:toggleNavIcon();">
                            <span></span>
                            <span></span>
                            <span></span>
                        </a>

                        <div class="grailsnavbar-collapse collapse">
                            <ul id="grailsnavbar-nav" class="grailsnavbar-nav grailsnavbar-right closemobile">
                                <li><g:link controller="ganttTasks" class="controller">Task</g:link></li>
                                <li><g:link controller="report" class="controller">Report</g:link></li>
                                <li><g:link controller="weekly" class="controller">WeeklyReport</g:link></li>

                                <li><a data-effect="st-effect-9" class="st-trigger" href="#">Settings</a></li>
                                %{--<li><a href="http://grails.org/search.html"><i class="fa fa-search"></i></a></li>--}%
                            </ul>
                        </div>
                    </div>
                </div>
                <!-- CONTENT -->
                <g:layoutBody/>

                <div class="footer" role="contentinfo"></div>
            </div>
        </div>
    </div>
</div>


<div id="spinner" class="spinner" style="display:none;">
    <g:message code="spinner.alt" default="Loading&hellip;"/>
</div>

%{--<asset:javascript src="application.js"/>--}%
<asset:javascript src="classie.js"/>
<asset:javascript src="sidebarEffects.js"/>
<asset:javascript src="navbar.js"/>
</body>
</html>
