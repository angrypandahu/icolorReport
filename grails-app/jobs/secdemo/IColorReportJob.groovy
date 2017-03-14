package secdemo

import org.apache.log4j.Logger

class IColorReportJob {
    def reportService
    static triggers = {
        cron name: 'remindReportTrigger', cronExpression: "0 0 17 * * ?"
    }
    def group = "IColorGroup"
    def description = "IColor Report remind job"

    def execute() {
        def instance = Calendar.getInstance();
        def dayOfWeek = instance.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek >= 2 && dayOfWeek <= 6){
            log.info("###RemindReportTrigger")
            reportService.sendRemindMails();
        }else {
            log.info("Today is holiday")
        }
    }
}
