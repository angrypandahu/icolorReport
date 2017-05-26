grails.mail.default.from = "angrypandahu@163.com"
grails {
    mail {
        host = "smtp.163.com"   //发送邮件服务器
        username = "angrypandahu@163.com"   //发送邮件邮箱
        from = "angrypandahu@163.com"
        password = "hupan12"  //发送邮件邮箱密码
        props = ["mail.smtp.auth": "true"]  //设置smtp的身份认证
    }
}

grails.plugin.springsecurity.ui.forgotPassword.postResetUrl = '/reset'
//grails.plugin.springsecurity.ui.forgotPassword.emailBody = 'emailBody...'
grails.plugin.springsecurity.ui.forgotPassword.emailFrom = 'angrypandahu@163.com'
//grails.plugin.springsecurity.ui.forgotPassword.emailSubject = 'emailSubject...'

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.domain.auth.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.domain.auth.UserRole'
grails.plugin.springsecurity.authority.className = 'com.domain.auth.Role'
grails.plugin.springsecurity.logout.postOnly = false// allows logout to work

grails.plugin.springsecurity.filterChain.chainMap = [
        [pattern: '/assets/**', filters: 'none'],
        [pattern: '/**/fonts/**', filters: 'none'],
        [pattern: '/**/js/**', filters: 'none'],
        [pattern: '/**/css/**', filters: 'none'],
        [pattern: '/**/images/**', filters: 'none'],
        [pattern: '/**/favicon.ico', filters: 'none'],
        [pattern: '/**', filters: 'JOINED_FILTERS']
]
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        [pattern: '/', access: ['permitAll']],
        [pattern: '/adminIndex', access: ['ROLE_ADMIN']],
        [pattern: '/error', access: ['permitAll']],
        [pattern: '/index', access: ['permitAll']],
        [pattern: '/index.gsp', access: ['permitAll']],
        [pattern: '/shutdown', access: ['permitAll']],
        [pattern: '/assets/**', access: ['permitAll']],
        [pattern: '/**/js/**', access: ['permitAll']],
        [pattern: '/**/css/**', access: ['permitAll']],
        [pattern: '/**/images/**', access: ['permitAll']],
        [pattern: '/**/favicon.ico', access: ['permitAll']],
        [pattern: '/register/**', access: ['permitAll']],
        [pattern: '/dbconsole/**', access: ['ROLE_ADMIN']],
        [pattern: '/console/**', access: ['ROLE_ADMIN']],
        [pattern: '/plugins/console/**', access: ['ROLE_ADMIN']],
        [pattern: '/static/console/**', access: ['ROLE_ADMIN']],
        [pattern: '/ganttTasks/**', access: ['ROLE_ADMIN','ROLE_USER','ROLE_MANAGER']],
        [pattern: '/user/**', access: ['ROLE_ADMIN']],
        [pattern: '/persistentLogin/**', access: ['ROLE_ADMIN']],
        [pattern: '/role/**', access: ['ROLE_ADMIN']],
        [pattern: '/userRole/**', access: ['ROLE_ADMIN']],
        [pattern: '/reportGroup/**', access: ['ROLE_ADMIN']],
        [pattern: '/aclClass/**', access: ['ROLE_ADMIN']],
        [pattern: '/securityInfo/**', access: ['ROLE_ADMIN']],
        [pattern: '/registrationCode/**', access: ['ROLE_ADMIN']],
        [pattern: '/quartz/**', access: ['ROLE_ADMIN']],
        [pattern: '/weekly/**', access: ['ROLE_ADMIN', 'ROLE_ICOLOR']],
        [pattern: '/schedule/**', access: ['ROLE_ADMIN', 'ROLE_ICOLOR']],
        [pattern: '/report/**', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/plugins/**', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/user/index/**', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/user/me/**', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/userRole/index/**', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/user/updatePassword/**', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/user/saveMe/**', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/reportGroup/index/**', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/wx/**', access: ['permitAll']]

]

grails.plugin.springsecurity.secureChannel.useHeaderCheckChannelSecurity = true
grails.plugin.springsecurity.secureChannel.secureHeaderName = 'X-Forwarded-Proto'
grails.plugin.springsecurity.secureChannel.secureHeaderValue = 'http'
grails.plugin.springsecurity.secureChannel.insecureHeaderName = 'X-Forwarded-Proto'
grails.plugin.springsecurity.secureChannel.insecureHeaderValue = 'https'

grails.resources.pattern = '/**'


