package com.nippon.report

import com.nippon.NpStringUtils
import com.nippon.export.export.ReportUtils

class ReportTaglibTagLib {
    static defaultEncodeAs = [taglib: 'raw']
    def report = { attrs, body ->
        def report = attrs.get('report');
        out << render(template: "/report/reportTemplate", model: [report: report])
    }
//    def myList = { attrs, body ->
//        out << render(template: "/template/list")
//    }

    def myInput = { attrs, body ->
        def field = attrs.get('field');
        def val = attrs.get('val');
        def inputType = attrs.get('inputType');
        def label = attrs.get('label');
        def notRequired = attrs.get('notRequired');
        if (!inputType) {
            inputType = "text"
        }
        if (!label) {
            label = NpStringUtils.getLabelByField(field as String);
        }
        if (!notRequired) {
            notRequired = false;
        }
        if (inputType == "date") {
            val = ReportUtils.dateFormat_5.format(val);
        }
        out << render(template: "/report/myInput", model: [field: field, inputType: inputType, label: label, notRequired: notRequired, val: val])
    }
    def totalCount = { attrs ->
        def total = attrs.get('total');
        out << "共 ${total} 条"
    }

    def writeWithoutEncoding = { attrs ->
        out << attrs.input
    }

}
