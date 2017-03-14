package com.domain.gantt

import com.domain.auth.User
import groovy.transform.ToString

@ToString(includes = 'text', includeNames = false, includePackage = false)
class GanttTasks {
    String type
    String text
    Date startDate
    Date endDate
    Double progress
    Double sorter
    Long parent
    String root
    Date dateCreated
    Date lastUpdated
    static belongsTo = User
    static hasMany = [users: User]
    static constraints = {
        type blank: false, inList: ["task", "project", "milestone"]
        text blank: false
        startDate nullable: false
        endDate nullable: false
        progress nullable: false, min: 0d, max: 1.0d
        sorter nullable: false
        parent nullable: true
        root nullable: true
    }
    static mapping = {
        autoTimestamp true
    }
}
