package com.nippon.mail

import grails.util.Environment
import org.apache.log4j.Logger

import javax.mail.Message
import javax.mail.Multipart
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

/**
 * Created by hupanpan on 2017/2/10.
 *
 */
class NipponMailUtils {
    static def  logger = Logger.getLogger(NipponMailUtils.class)
    private static void sendMailToNipponInProduct(String to,
                                        String subject, String body) throws Exception {
        if(!Environment.isDevelopmentMode()){
//            String host = "smtp.163.com";
            String from = "LiYiSong.KLT/NPChina";
            String password = "edi4fier";
            String host = "134.127.17.66";
            Properties props = System.getProperties();
//    props.put("mail.smtp.starttls.enable", true);
            /* mail.smtp.ssl.trust is needed in script to avoid error "Could not convert socket to TLS"  */
            props.setProperty("mail.smtp.ssl.trust", host);
            props.put("mail.smtp.auth", false);
            props.put("mail.store.protocol", "pop3");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", host);
            props.put("mail.pop3.host", host);
            props.put("mail.user", from);
            props.put("mail.from", "webmaster@nipponpaint.com.cn");
            props.put("mail.smtp.password", password);
            props.put("mail.smtp.port", "25");

            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
//    message.setFrom(new InternetAddress(from));
            Multipart multipt = new MimeMultipart();
            MimeBodyPart msgbody = new MimeBodyPart();
            msgbody.setContent(body, "text/html;charset=utf-8");
            multipt.addBodyPart(msgbody);
            message.setContent(multipt);
            InternetAddress toAddress = new InternetAddress(to);

            message.addRecipient(Message.RecipientType.TO, toAddress);

            message.setSubject(subject);

            Transport transport = session.getTransport("smtp");

            transport.connect(host, from, password);

            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }else {
            logger.info("###########isDevelopmentMode-->No send Mail")
        }

    }
    public static void sendMailToNippon(String to,
                                        String subject, String body) throws Exception {
        sendMailToNipponInProduct(to,subject,body);
    }
}
