package com.nippon.export.export

import com.domain.auth.User
import com.domain.report.Report
import com.domain.report.Weekly
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.grails.web.json.JSONObject
import org.joda.time.LocalDate

import java.text.MessageFormat
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Created by hupanpan on 2017/1/10.
 *
 */
class ReportUtils {
    final static String REPORT_PATH = "/reportTemplate/"
    final static String TEMPLATE_ICOLOR = "icolorTemplate.docx"
    final static String TEMPLATE_WEEKLY_ICOLOR = "icolorWeeklyTemplate.docx"
    final static String TEMPLATE_WAIBAO = "waibaoTemplate.xlsx"
    final static String TEMPLATE_MONTH_MENGTUO = "monthMengTuoTemplate.xlsx"
    final static String WAIBAO_EXCEL_NAME_PRE = "立邦项目外包人员开发日报_"
    final static def dateFormat = new SimpleDateFormat("yyyyMMdd")
    final static def dateFormat_2 = new SimpleDateFormat("yyyy.MM.dd")
    final static def dateFormat_3 = new SimpleDateFormat("MMdd")
    final static def dateFormat_4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    final static def dateFormat_5 = new SimpleDateFormat("yyy-MM-dd")
    final static int WAIBAO_CELL_USERNAME = 2;
    final static int WAIBAO_CELL_CONTENT = 4;
    final static int WAIBAO_CELL_WORKHOUR = 5;
    final static def logger = Logger.getLogger(ReportUtils.class)

    static def getRange(Report report, def range) {
        range.replaceText("\${reportDate}", dateFormat_2.format(report.getReportDate().toDate()));
        range.replaceText("\${user.username}", report.getUser().getDisplayName());
        range.replaceText("\${content}", changeLine(report.content));
        range.replaceText("\${share}", changeLine(report.share));
        range.replaceText("\${question}", changeLine(report.question));
        return range;
    }

    static def toReportString(Report report, String contentTemplate) {
        String content = contentTemplate;
        content = content.replace('reportDate', dateFormat_2.format(report.getReportDate().toDate()));
        content = content.replace('user.username', report.getUser().getDisplayName());
        content = content.replace('content', changeLine(report.content));
        content = content.replace('share', changeLine(report.share));
        content = content.replace('question', changeLine(report.question));
        return content;
    }

    static def changeLine(def content) {
        def retContent = ""
        def defaultIfEmpty = StringUtils.defaultIfEmpty(content, "").split("\r\n")
        defaultIfEmpty.each {
            retContent += "\t" + it + "\n";
        }
        return retContent
    }


    public static void writeReportToDoc(Report report, OutputStream outputStream, File template) {
        def reports = new ArrayList<Report>();
        reports.add(report);
        writeReportToDoc(reports, outputStream, template);
    }

    public static void writeReportToDoc(List<Report> reports, OutputStream outputStream, File template) {
        XWPFDocument templateDocument = new XWPFDocument(new FileInputStream(template));
        def body = templateDocument.getDocument().getBody();
        reports.eachWithIndex { report, index ->
            def map = new HashMap<>();
            map.put('reportDate', dateFormat_2.format(report.getReportDate().toDate()))
            map.put('user.username', report.getUser().getDisplayName())
            map.put('tab.content', report.content);
            map.put('tab.share', report.share);
            map.put('tab.question', report.question);
            if (index == 0) {
                WordUtils.replaceInTable(templateDocument, map)
            } else {
                def newDocument = new XWPFDocument(new FileInputStream(template));
                WordUtils.replaceInTable(newDocument, map)
                def newBody = newDocument.getDocument().getBody();
                WordUtils.appendBody(body, newBody);
            }
        }
        templateDocument.write(outputStream);

    }

    public static String getWeeklyFormatName(Weekly weekly) {
        def reportDate = weekly.reportDate;
        def ofWeek = reportDate.dayOfWeek;
        def monday = reportDate.minusDays(ofWeek - 1);
        def friday = reportDate.minusDays(ofWeek - 5);
        return dateFormat_2.format(monday.toDate()) + "-" + dateFormat_2.format(friday.toDate())
    }

    public static void writeWeeklyToDoc(List<Weekly> weeklies, OutputStream outputStream, File template) {
        XWPFDocument templateDocument = new XWPFDocument(new FileInputStream(template));
        def body = templateDocument.getDocument().getBody();
        weeklies.eachWithIndex { weekly, index ->
            def map = new HashMap<>();
            map.put('reportDate', getWeeklyFormatName(weekly))
            map.put('user.username', weekly.getUser().getDisplayName())
            map.put('tab.content', weekly.content);
            map.put('tab.review', StringUtils.defaultIfBlank(weekly.review, ""));
            def schedules = weekly.schedules;
            schedules.each { schedule ->
                def key = "tab.content.${schedule.dayOfWeek}".toString();
                map.put(key, schedule.content);
                def key2 = "pro.${schedule.dayOfWeek}".toString()
                def progress = schedule.progress ? "${schedule.progress}%" : "";
                map.put(key2, progress);
            }
            if (index == 0) {
                WordUtils.replaceInTable(templateDocument, map)
            } else {
                def newDocument = new XWPFDocument(new FileInputStream(template));
                WordUtils.replaceInTable(newDocument, map)
                def newBody = newDocument.getDocument().getBody();
                WordUtils.appendBody(body, newBody);
            }
        }
        templateDocument.write(outputStream);

    }


    public static void writeReportToExcel(List<Report> reports, OutputStream outputStream, FileInputStream fileInputStream) {
        def report = reports.get(0);
        def date = report.getReportDate().toDate();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 1);
        def monday = c.getTime();
        c.add(Calendar.DATE, 6);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        writeExcelContent(reports, workbook, monday)
        workbook.setForceFormulaRecalculation(true);
        workbook.write(outputStream);
        workbook.close()

    }

    static String get255String(Object o) {
        String returnStr = "";
        if (o != null) {
            def defaultIfBlank = StringUtils.defaultIfBlank(o.toString(), "");
            returnStr = defaultIfBlank.substring(0, Math.min(255, defaultIfBlank.length()));
        }
        return returnStr

    }

    static void writeMengTuoMonthReportToExcel(User user, List<Report> reports, OutputStream outputStream, FileInputStream fileInputStream) {
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        workbook.setSheetName(0, user.getDisplayName());
        XSSFSheet sheetAt = workbook.getSheetAt(0);
        def rowIndex = 5
        def xSSFRow;
        LocalDate reportDate = new LocalDate();
        reports.eachWithIndex { Report report, int i ->
            xSSFRow = sheetAt.getRow(rowIndex++)
            if (i == 0) {
                reportDate = report.getReportDate()
            }
            xSSFRow.getCell(0).setCellValue(report.getReportDate().dayOfMonth + "日");
            xSSFRow.getCell(1).setCellValue(report.getContent());
            xSSFRow.getCell(3).setCellValue(report.getWorkHours());

        }
        sheetAt.getRow(1).getCell(1).setCellValue(user.getDisplayName())
        sheetAt.getRow(37).getCell(1).setCellValue(user.getDisplayName())
        def monthOfYear = reportDate.getMonthOfYear();
        def year = reportDate.getYear();
        def firstMonthDay = new LocalDate(year, monthOfYear, 1);
        def lastMonthDay = firstMonthDay.minusMonths(-1).minusDays(1);
        sheetAt.getRow(3).getCell(3).setCellValue(firstMonthDay.toDate())
        sheetAt.getRow(38).getCell(1).setCellValue(lastMonthDay.toDate())
        workbook.setForceFormulaRecalculation(true);
        workbook.write(outputStream);
        workbook.close()

    }

    private static void writeExcelContent(List<Report> reports, XSSFWorkbook workbook, Date monday) {

        XSSFSheet sheetAt = workbook.getSheetAt(0);
        def templateNum = sheetAt.getRow(99).getCell(0).getNumericCellValue();
        def row = sheetAt.getRow(3);
        row.getCell(0).setCellValue(monday);
        Calendar c = Calendar.getInstance();
        c.setTime(monday);
        Map<Integer, Set<Report>> map = [:];
        for (int i = 0; i < 7; i++) {
            map.put(i, new LinkedHashSet<Report>())
        }

        for (report in reports) {
            def reportDate = report.getReportDate().toDate();
            c.setTime(reportDate);
            def dayOfWeek = getDaySub(monday, c.getTime());
            Set<Report> set = map.get(dayOfWeek);
            set?.add(report);
        }
        def keySet = map.keySet();
        for (key in keySet) {
            def setOfReports = map.get(key);
            def reportRow = getReportRow(key, templateNum.intValue());
            setOfReports.sort();
            for (oneReport in setOfReports) {
                def cell = sheetAt.getRow(reportRow).getCell(WAIBAO_CELL_USERNAME)
                cell.setCellValue(oneReport.getUser().getDisplayName());
                sheetAt.getRow(reportRow).getCell(WAIBAO_CELL_CONTENT).setCellValue(oneReport.getContent());
                sheetAt.getRow(reportRow).getCell(WAIBAO_CELL_WORKHOUR).setCellValue(oneReport.getWorkHours());
                reportRow++;
            }

        }

    }

    private static int getReportRow(int dayOfWeek, int templateNum) {
        return 3 + dayOfWeek * (templateNum + 1);

    }


    public static int getDaySub(Date beginDate, Date endDate) {
        long day = 0;
        try {
            day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
            //System.out.println("相隔的天数="+day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    public static String getWaiBaoExcelName(int year, int month, int day, String option) {
//        def instance = Calendar.getInstance();
//        instance.set(year, month, day);
//        if (option == "GreaterThan") {
//            instance.add(Calendar.DAY_OF_YEAR, 1);
//        } else if (option == "LessThan") {
//            instance.add(Calendar.DAY_OF_YEAR, -1);
//        }
//
//        def monday = getMonday(instance.getTime());
//        def sunday = getSunday(instance.getTime());
//        def format = dateFormat_3.format(monday)
//        def format1 = dateFormat_3.format(sunday)
        def format = dateFormat.format(new Date());
        return WAIBAO_EXCEL_NAME_PRE + format;
    }

    public static String getMonthMengTuoExcelName(String year, String month, String userName) {
        String MONTH_MENGTUO_EXCEL_NAME_PRE = "上海立邦Hybris项目_{0}年{1}月工作报告_{2}"
        return MessageFormat.format(MONTH_MENGTUO_EXCEL_NAME_PRE, year, month, userName);
    }


    public static Date getMonday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 1);
        return c.getTime();
    }

    public static Date getSunday(Date date) {
        Calendar c = Calendar.getInstance();
        def monday = getMonday(date);
        c.setTime(monday)
        c.add(Calendar.DATE, 6);
        def sunDay = c.getTime();
        return c.getTime();
    }

    public static def userToJson(User user) {
        def userJson = new JSONObject();
        userJson.put("id", user.id)
        userJson.put("username", user.username)
        userJson.put("email", user.email)
        userJson.put("displayName", user.displayName)
        userJson.put("reportGroup", user.reportGroup.name)
        userJson.put("wxOpenId", user.wxOpenId)
        return userJson
    }

    public static def reportToJson(Report report) {
        def reportJson = new JSONObject();
        reportJson.put("id", report.id)
        reportJson.put("user", userToJson(report.user))
        reportJson.put("reportDate", report.reportDate)
        reportJson.put("content", report.content)
        reportJson.put("question", report.question)
        reportJson.put("share", report.share)
        reportJson.put("workHours", report.workHours)
        reportJson.put("isSend", report.isSend)
        return reportJson
    }
}
