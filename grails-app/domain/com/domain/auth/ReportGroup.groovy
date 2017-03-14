package com.domain.auth

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes='name')
@ToString(includes='name', includeNames=false, includePackage=false)
class ReportGroup implements Serializable {

	private static final long serialVersionUID = 1

	String name

	String getName() {
		return name
	}
	static constraints = {
		name blank: false, unique: true
	}

	static mapping = {
		cache true
	}
}
