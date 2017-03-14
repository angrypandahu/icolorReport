package com.domain.report

import com.domain.auth.User
import com.nippon.export.export.ReportUtils
import com.nippon.mail.NipponMailUtils
import grails.transaction.Transactional
import org.joda.time.LocalDate
import org.springframework.security.access.prepost.PreAuthorize

@Transactional
class ReportService {
    def securityService

    @PreAuthorize("@securityService.isYourReport(#id)")
    def getReport(def id) {
        return Report.get(id)
    }

    def serviceMethod() {

    }

    def getThisWeekReports(User user, Weekly weekly) {
        def date = new LocalDate();
        if (weekly?.reportDate) {
            date = weekly.reportDate
        }else {
            weekly.setReportDate(date)
        }
        def week = date.getDayOfWeek();
        def monday = date.minusDays(week - 1);
        return Report.findAllByUserAndReportDateNotGreaterThanEquals(user, monday);

    }

    def sendRemindMail(User user) {
        try {
            def body = """请到
            <a href='http://134.119.11.24/'>IColor Report</a>
            填写今天的日报!<br/>
            提醒时间:${ReportUtils.dateFormat_4.format(new Date())}
""";
            def subject = "IColor日报提醒";
            log.info("###########sendRemindMail to ${user.username} Start###########");
            log.info("###########body ${body}")
            NipponMailUtils.sendMailToNippon(user.getEmail(), subject, body)
            log.info("###########sendRemindMail to ${user.username} End###########");

        } catch (Exception e) {
            log.error(e.message)
        }

    }

    def sendRemindMails() {
        def c = User.createCriteria()
        def hasSendReports = [-1L];

        def reports = Report.findAllByReportDate(new LocalDate())
        if (reports != null && reports.size() > 0) {
            hasSendReports = reports.user.id;
        }
        def results = c.list {
            like("email", "%@nipponpaint.com.cn")
            and {
                not {
                    'in'("id", hasSendReports)
                }
            }

        }
        results.each { user ->
            sendRemindMail(user);
        }
    }
}
