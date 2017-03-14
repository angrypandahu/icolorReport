package com.domain.report

import com.domain.auth.Role
import com.domain.auth.User
import com.domain.gantt.GanttTasks
import grails.transaction.Transactional

@Transactional
class SecurityService {
    def springSecurityService

    def serviceMethod() {

    }

    boolean isYourReport(id) {
        def report = Report.get(id)
        def currentUser = springSecurityService.getCurrentUser()
        def authorities = currentUser.getAuthorities()
        def roleAdmin = Role.findByAuthority("ROLE_ADMIN")
        return currentUser == report.user || authorities.contains(roleAdmin)
    }

    boolean isYourTask(id) {
        def task = GanttTasks.get(id)
        def users = task.getUsers();
        def currentUser = springSecurityService.getCurrentUser()
        def authorities = currentUser.getAuthorities()
        def roleAdmin = Role.findByAuthority("ROLE_ADMIN")
        def roleManager = Role.findByAuthority("ROLE_MANAGER")
        return users.contains(currentUser) || authorities.contains(roleAdmin) || authorities.contains(roleManager)
    }
}
