package com.domain.gantt

import com.domain.auth.User
import com.domain.report.Report
import com.nippon.export.export.ReportUtils
import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.security.access.prepost.PreAuthorize

@Transactional
class GanttTasksService {

    def serviceMethod() {

    }

    def taskListByRoot(Long root) {
        return GanttTasks.findAllByRootAndType(root, "task")
    }

    def projectList() {
        def list = GanttTasks.findAllByType("project")
        list.sort(new Comparator<GanttTasks>() {
            @Override
            int compare(GanttTasks o1, GanttTasks o2) {
                return o1.sorter < o2.sorter ? 1 : -1
            }
        })
        return list
    }

    def taskToJson(GanttTasks task) {
        def oneTask = new JSONObject()
        if (task) {
            def users = new JSONArray()
            task.users?.each { user ->
                users.add(user.id)
            }

            oneTask.put("id", task.getId())
            oneTask.put("text", task.getText())
            oneTask.put("start_date", ReportUtils.dateFormat_5.format(task.startDate))
            def daySub = ReportUtils.getDaySub(task.startDate, task.endDate)
            oneTask.put("duration", daySub)
            oneTask.put("type", task.type)
            oneTask.put("open", true)
            oneTask.put("users", users)
            oneTask.put("order", task.sorter)
            oneTask.put("progress", task.progress.doubleValue())
            def parent = 0L;
            if (task.parent) {
                parent = task.parent;
            }
            oneTask.put("parent", parent)
        }
        return oneTask
    }

    def removeUser(ganttTasksId, userId) {
        log.info(ganttTasksId)
        def ganttTasks = GanttTasks.findById(Long.parseLong(ganttTasksId))
        def user = User.findById(Long.parseLong(userId))
        return ganttTasks.removeFromUsers(user)
    }

    def importTask(String no, String text, String userName, sorter, root) {
        text = no + " " + text;
        def ganttTask = GanttTasks.findByText(text);
        def start = ReportUtils.dateFormat_5.parse("2017-05-10")
        def end = ReportUtils.dateFormat_5.parse("2017-06-10")
        def split = no.split("\\.")
        def hierarchyType = split.length
        GanttTasks rootTask = GanttTasks.get(root)
        if (rootTask) {
            if (!ganttTask) {
                ganttTask = new GanttTasks(text: text, root: rootTask.getText(), sorter: sorter, startDate: start, endDate: end, progress: 0.0, parent: rootTask.getId(), type: 'task');
                if (hierarchyType > 1) {
                    def parent = no.substring(0, no.lastIndexOf("."))
                    def parentTask = GanttTasks.findByTextLikeAndRoot("${parent}%",rootTask.getText())
                    if (parentTask) ganttTask?.setParent(parentTask.getId());
                }
                ganttTask.save(flush: true);
            }
            addTaskToUser(userName, ganttTask.getId())
            rootTask.setParent(null)
            rootTask.save(flush: true)

        }


    }

    def addTaskToUser(String name, Long taskId) {
        if (name != null && name != "") {
            if (name.contains("&")) {
                def list = name.split("&");
                list?.each { oneName ->
                    addUser(oneName, taskId)
                }
            } else {
                addUser(name, taskId);
            }


        }

    }

    String getHierarchy(String no) {
        def split = no.split("\\.")
        StringBuffer hierarchy = new StringBuffer("");
        split?.each { one ->
            hierarchy.append(StringUtils.leftPad(one, 2, "0"))
        }
        return hierarchy.toString()
    }

    def addUser(oneName, taskId) {
        def byDisplayName = User.findByDisplayName(oneName)
        def task = GanttTasks.get(taskId)
        if (byDisplayName && task && !task.getUsers()?.contains(byDisplayName)) {
            task.addToUsers(byDisplayName)
            task.save(flush: true)
        }
    }

    def securityService

    @PreAuthorize("@securityService.isYourTask(#id)")
    getTask(id) {
        return GanttTasks.get(id)
    }


    def deleteTask(def id) {
        def task = GanttTasks.get(id)
        def users = task.users;
        users?.each { user ->
            task.removeFromUsers(user)
        }
        task.delete(flush: true)

    }

    Set<GanttTasks> getParents(GanttTasks task) {
        def set = new HashSet<GanttTasks>();
        set.add(task);
        def parent = task.getParent()
        def parentTask = GanttTasks.get(parent);
        if (parentTask) {
            set.addAll(getParents(parentTask))
        }
        return set
    }

}
