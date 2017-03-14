package com.domain.auth

import com.domain.gantt.GanttTasks
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'username')
@ToString(includes = 'displayName', includeNames = false, includePackage = false)
class User implements Serializable {

    private static final long serialVersionUID = 1

    transient springSecurityService

    String username
    String displayName
    String password
    String email
    String wxOpenId
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    ReportGroup reportGroup
    static hasMany = [ganttTasks: GanttTasks]

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this)*.role
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }

    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true
        displayName blank: false
        reportGroup nullable: true
        wxOpenId nullable: true
        enabled nullable: true
        accountExpired nullable: true
        accountLocked nullable: true
        passwordExpired nullable: true
        password blank: false, password: true
    }
    static mapping = {
        password column: '`password`'
    }
}
