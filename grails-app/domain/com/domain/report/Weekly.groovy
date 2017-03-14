package com.domain.report

import com.domain.auth.User
import groovy.transform.ToString
import org.joda.time.LocalDate

@ToString(includes = 'reportDate', includeNames = false, includePackage = false)
class Weekly {

    User user
    LocalDate reportDate
    String content
    String question
    boolean isSend
    Date dateCreated
    Date lastUpdated
    SortedSet schedules
    static hasMany = [schedules: Schedule]

    static constraints = {
        reportDate blank: false
        user blank: false
        content blank: false, widget: 'textarea'
        question nullable: true, widget: 'textarea'
        isSend defaultValue: false
        dateCreated nullable: true
        lastUpdated nullable: true
        reportDate(unique: 'user')
    }
    static mapping = {
        autoTimestamp true
    }
}
