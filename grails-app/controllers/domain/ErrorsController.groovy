package domain

import grails.plugin.springsecurity.annotation.Secured

@Secured('permitAll')
class ErrorsController {

    def error403() {
        render view: '/error403'
    }


    def error500() {
        render view: '/error'

    }
}