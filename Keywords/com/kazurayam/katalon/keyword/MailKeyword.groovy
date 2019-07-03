package com.kazurayam.katalon.keyword

import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.kms.katalon.core.annotation.Keyword

public class MailKeyword {

    @Keyword
    public static void sendMail(String htmlMessage) {
        final String username = "kazuaki.matsuhashi@quick.jp";
        final String password = "Wakako1Akihiro2Hanae4";

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "172.24.172.4");
        props.put("mail.smtp.port", "25");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("kazuaki.matsuhashi@quick.jp"));
            message.setSubject("Test warning");
            message.setContent(htmlMessage, "text/html; charset=iso-2022-jp");

            System.out.println("Sending...");

            Transport.send(message);

            System.out.println("Sent.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}