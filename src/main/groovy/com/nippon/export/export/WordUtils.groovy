package com.nippon.export.export

import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFRun
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.apache.poi.xwpf.usermodel.XWPFTableRow
import org.apache.xmlbeans.XmlOptions
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by hupanpan on 2017/2/4.
 *
 */
class WordUtils {
    final static def logger = Logger.getLogger(WordUtils.class)

    public static void merge(InputStream src1, InputStream src2, OutputStream dest) throws Exception {
        OPCPackage src1Package = OPCPackage.open(src1);
        OPCPackage src2Package = OPCPackage.open(src2);
        XWPFDocument src1Document = new XWPFDocument(src1Package);
        CTBody src1Body = src1Document.getDocument().getBody();
        XWPFDocument src2Document = new XWPFDocument(src2Package);
        CTBody src2Body = src2Document.getDocument().getBody();
        appendBody(src1Body, src2Body);
        src1Document.write(dest);
    }

    public static void appendBody(CTBody src, CTBody append) throws Exception {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String srcString = src.xmlText();
        String appendString = append.xmlText(optionsOuter);
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
        String sufix = srcString.substring(srcString.lastIndexOf("<"));
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);
        src.set(makeBody);

    }

    /**
     * 替换段落里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    public static void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext()) {
            para = iterator.next();
            _replaceInPara(para, params);
        }
    }

    /**
     * 替换段落里面的变量
     * @param para 要替换的段落
     * @param params 参数
     */
    private static void _replaceInPara(XWPFParagraph para, Map<String, Object> params) {
        List<XWPFRun> runs;
        Matcher matcher;
        if (this.matcher(para.getParagraphText()).find()) {
            runs = para.getRuns();
            for (int i = 0; i < runs.size(); i++) {
                XWPFRun run = runs.get(i);
                String runText = run.toString();
               logger.info(runText)
                matcher = this.matcher(runText);
                if (matcher.find()) {
                    def hasTab = false
                    while ((matcher = this.matcher(runText)).find()) {
                        def group = matcher.group(1)
                        if (group.startsWith("tab.")) {
                            hasTab = true;
                        }
                        def getText =ReportUtils.get255String(params.get(group))
                        logger.info(group+"--->" + getText)
                        runText = matcher.replaceFirst(getText);

                    }
                    //直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
                    //所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
                    para.removeRun(i);

                    if (hasTab) {
                        def split = runText.split("\r\n");
                        int j = i;
                        split.each { it ->
                            def newRun = para.insertNewRun(j++);
                            newRun.addTab();
                            newRun.setText(it);
                            newRun.addBreak();
                        }

                    } else {
                        def newRun = para.insertNewRun(i)
                        newRun.setText(runText);
                    }

                }
            }
        }
    }

    /**
     * 替换表格里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    public static void replaceInTable(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFTable> iterator = doc.getTablesIterator();
        XWPFTable table;
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        List<XWPFParagraph> paras;
        while (iterator.hasNext()) {
            table = iterator.next();
            rows = table.getRows();
            for (XWPFTableRow row : rows) {
                cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    paras = cell.getParagraphs();
                    for (XWPFParagraph para : paras) {
                        _replaceInPara(para, params);
                    }
                }
            }
        }
    }

    /**
     * 正则匹配字符串
     * @param str
     * @return
     */
    private static Matcher matcher(String str) {
        def reg = /\$\{(.+?)}/
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    /**
     * 关闭输入流
     * @param is
     */
    private static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     * @param os
     */
    private static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("reportDate", "2014-02-28");
        params.put("user.username", "胡盼盼");
        params.put("tab.content", "哈哈哈都是发生了贷款纠纷");
        String filePath = "C:\\report\\icolor\\icolorTemplate.docx";
        InputStream is = new FileInputStream(filePath);
        XWPFDocument doc = new XWPFDocument(is);
        //替换段落里面的变量
//        _replaceInPara(doc, params);
        //替换表格里面的变量
        replaceInTable(doc, params);

        OutputStream os = new FileOutputStream("C:\\report\\icolor\\testPoi2.docx");
        doc.write(os);
        close(os);


    }


}
