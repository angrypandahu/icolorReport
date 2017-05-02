<%@ page import="com.domain.gantt.GanttTasks" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'ganttTasks.label', default: 'GanttTasks')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <asset:javascript src="jquery-2.2.0.min.js"/>
    <asset:javascript src="gantt/dhtmlxgantt.js"/>
    <asset:javascript src="gantt/locale_cn.js"/>
    <asset:javascript src="gantt/ganttUtils.js"/>
    <asset:stylesheet src="gantt/dhtmlxgantt_meadow.css" type="text/css" media="screen" title="no title"
                      charset="utf-8" id="skin"/>
    <asset:javascript src="fp.js"/>
    <asset:stylesheet src="fp.css"/>
    <style type="text/css">
    div.gantt_cal_light {
        height: 258px !important;
    }

    .gantt_task_progress {
        text-align: left;
        padding-left: 10px;
        box-sizing: border-box;
        color: white;
        font-weight: bold;
    }
    </style>

    <script type="text/javascript">
        function changeSkin(name) {
            var link = document.createElement("link");

            link.onload = function () {
                gantt.resetSkin();
                gantt.render();
            };

            link.rel = "stylesheet";
            link.type = "text/css";
            link.id = "skin";
            link.href = "/assets/gantt/dhtmlxgantt_" + name + ".css";
            document.head.replaceChild(link, document.querySelector("#skin"));

        }
        function selectTaskParent(item) {
            var url = "./taskList" + "?root=" + item.value;
            gantt.clearAll();
            gantt.load(url);

        }
        function toPercent(v) {
            if (v >= 0) {
                var toFixed = (v * 100).toFixed(0);
                return toFixed + '%';
            }
            return 'N/A'

        }
        $(function () {
            gantt.config.readonly = true;
            gantt.users = <g:writeWithoutEncoding input="${users}"/>;
            gantt.config.xml_date = "%Y-%m-%d";
            gantt.config.columns = [
                {name: "text", tree: true, width: '*'},
                {
                    name: "assigned", label: "分配", align: "center", width: 80,
                    template: function (item) {
                        return joinUsers(item.users, gantt.users);
                    }
                }
            ];
            gantt.templates.progress_text = function (start, end, task) {
                return "<span style='text-align:left;'>" + Math.round(task.progress * 100) + "% </span>";
            };
            gantt.config.scale_unit = "month";
            gantt.config.date_scale = "%F, %Y";
            gantt.config.scale_height = 50;
            gantt.config.subscales = [
                {unit: "week", step: 1, date: "%j, %D"}
            ];


            gantt.init("gantt_here");
            gantt.parse(<g:writeWithoutEncoding input="${ganttTasksList}"/>);
//            gantt.form_blocks["my_editor"] = {
//                render: function (sns) {
//                    return "<div class='dhx_cal_ltext' style='height:60px;'>Text&nbsp;<input type='text'><br/>Holders&nbsp;<input type='text'></div>";
//                },
//                set_value: function (node, value, task) {
//                    node.childNodes[1].value = value || "";
//                    node.childNodes[4].value = task.users || "";
//                },
//                get_value: function (node, task) {
//                    task.users = node.childNodes[4].value;
//                    return node.childNodes[1].value;
//                },
//                focus: function (node) {
//                    var a = node.childNodes[1];
//                    a.select();
//                    a.focus();
//                }
//            };
//            gantt.config.lightbox.sections = [
//                {name: "description", height: 200, map_to: "text", type: "my_editor", focus: true}
//            ];
//            gantt.config.lightbox.sections = [
//                {name: "description", height: 38, map_to: "text", type: "textarea", focus: true},
//                {
//                    name: "users",
//                    height: 40,
//                    map_to: "users",
//                    type: "select",
//                    options: users
//                }
////                {name: "time", type: "time", map_to: "auto", time_format:["%d", "%m", "%Y", "%H:%i"]}
//            ];
//            var url = "./taskList" + "?root=" + $("#taskParentId").val();
//            gantt.load(url);

//            gantt.attachEvent("onAfterTaskUpdate", function (id, item) {
//            });
//            gantt.attachEvent("onBeforeTaskUpdate", function (id, new_item) {
//                var data = toGanttJson(new_item);
//                $.ajax({
//                    url: './save', type: 'POST', data: data, success: function (res) {
//                        console.log(res);
//                        return res.success
//                    }, error: function (res) {
//                        console.log(res);
//                        return false;
//                    }
//                });
//
//            });
//            var dp = new gantt.dataProcessor(url);
//            dp.init(gantt);
        })

    </script>
</head>

<body>
<filterpane:filterPane domain="com.domain.gantt.GanttTasks" dialog="true"
                       associatedProperties="users.username" action="task"
                       filterPropertyValues="${['root':
                                                        [values: GanttTasks.executeQuery('select t.text from GanttTasks t where t.type=\'project\' ')]]}"/>
<a href="#list-ganttTasks" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                 default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><filterpane:filterButton text="Search"/></li>
        %{--<li>--}%

        %{--<form id="author-form" name="author-form" method="post">--}%
        %{--<filterpane:filterPane domain="com.domain.gantt.GanttTasks" dialog="true"--}%
        %{--associatedProperties="users.username" customForm="true" action="task"--}%
        %{--formName="author-form"/>--}%
        %{--<g:actionSubmit value="Apply Filter From Outside Filter Pane" action="taskList"/>--}%
        %{--</form>--}%
        %{--</li>--}%
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        %{--<li><g:select name="project" from="${ganttProjectList}" optionKey="id" optionValue="text" id="taskParentId"--}%
        %{--onChange="selectTaskParent(this)"/></li>--}%
    </ul>
</div>

%{--<div class="controls">--}%

%{--<button onclick="changeSkin('terrace')">Terrace</button>--}%
%{--<button onclick="changeSkin('skyblue')">Skyblue</button>--}%
%{--<button onclick="changeSkin('meadow')">Meadow</button>--}%
%{--<button onclick="changeSkin('broadway')">Broadway</button>--}%
%{--</div>--}%

<div id="gantt_here" style='width:100%; height:80%;'></div>
<script type="text/javascript">

</script>

</body>
</html>