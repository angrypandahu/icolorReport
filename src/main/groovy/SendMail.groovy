/**
 * Created by hupanpan on 2016/12/27.
 *
 */
import javax.mail.*
import javax.mail.internet.*


public static void simpleMail(String from, String password, String to,
                              String subject, String body) throws Exception {

//    String host = "smtp.163.com";
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

    InternetAddress toAddress = new InternetAddress(to);

    message.addRecipient(Message.RecipientType.TO, toAddress);

    message.setSubject(subject);
    message.setText(body);

    Transport transport = session.getTransport("smtp");

    transport.connect(host, from, password);

    transport.sendMessage(message, message.getAllRecipients());
    transport.close();
}

/* Set email address sender */
String s1 = "LiYiSong.KLT/NPChina";

/* Set password sender */
String s2 = "edi4fier";

/* Set email address sender */
String s3 = "angrypandahu@163.com"

/*Call function */
simpleMail(s1, s2, s3, "Test Groovy send mail", "Hello Groovy");