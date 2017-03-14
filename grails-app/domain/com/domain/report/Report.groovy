package com.domain.report

import com.domain.auth.User
import org.joda.time.LocalDate

class Report {
    User user
    LocalDate reportDate
    String content
    String question
    String share
    Double workHours
    boolean isSend
    Date dateCreated
    Date lastUpdated
    static constraints = {
        user blank: false
        reportDate blank: false
        content blank: false,widget:'textarea'
        question nullable: true,widget:'textarea'
        share nullable: true,widget:'textarea'
        workHours defaultValue:0
        isSend defaultValue:false
        dateCreated nullable: true
        lastUpdated nullable: true
        reportDate(unique: 'user')

    }

    static mapping = {
        autoTimestamp true
    }

}
