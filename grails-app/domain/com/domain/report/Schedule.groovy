package com.domain.report

import groovy.transform.ToString

@ToString(includes = 'dayOfWeek', includeNames = false, includePackage = false)
class Schedule implements Comparable {
    static belongsTo = [weekly: Weekly]
    int dayOfWeek
    String content
    Double progress
    static constraints = {
        dayOfWeek blank: false, inList: [1, 2, 3, 4, 5]
        content blank: false
        progress nullable: true
        dayOfWeek(unique: 'weekly')
    }

    @Override
    int compareTo(Object o) {
        return dayOfWeek - o.dayOfWeek
    }
}
