package com.kazurayam.katalon.keyword

import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication
import javax.mail.SendFailedException
import javax.mail.Session
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.kms.katalon.core.annotation.Keyword

public class MailKeyword {

    private String senderAddress;
    private String senderPassword;
    private String host;
    private String port;

    public static class Builder {
        private String senderAddress;
        private String senderPassword;
        private String host;
        private String port;
        public Builder(String host, String port) {
            Objects.requireNonNull(host, "host must not be null");
            Objects.requireNonNull(port, "port must not be null")
            if (host.length() == 0) {
                throw new IllegalArgumentException("host must not be an empty string");
            }
            
            if (port.length() == 0) {
                throw new IllegalArgumentException("port must not be an empty string")
            }
            this.host = host
            this.port = port
        }
        public Builder senderAddress(String senderAddress) {
            Objects.requireNonNull(senderAddress, "senderAddress must not be null");
            if (senderAddress.length() == 0) {
                throw new IllegalArgumentException("senderAddress must not be an empty string");
            }
            this.senderAddress = senderAddress;
            return this;
        }
        public Builder senderPassword(String senderPassword) {
            Objects.requireNonNull(senderPassword, "senderPassword must not be null");
            if (senderPassword.length() == 0) {
                throw new IllegalArgumentException("senderPassword must not be an empty string");
            }
            this.senderPassword = senderPassword;
            return this;
        }
        public MailKeyword build() {
            return new MailKeyword(this);
        }
    }


    /**
     * 
     * @param builder
     */
    private MailKeyword(Builder builder) {
        this.senderAddress = builder.senderAddress;
        this.senderPassword = builder.senderPassword;
        this.host = builder.host;
        this.port = builder.port;
    }

    /**
     * 
     * @param htmlMessage
     * @return true if sent message successfully.
     */
    @Keyword
    public boolean sendMail(
            String receiverAddress,
            String subject,
            String htmlMessage,
            String mimeType = 'text/html; charset=iso-2022-jp') {
        Objects.requireNonNull(receiverAddress, "receiverAddress must not be null")
        Objects.requireNonNull(subject, "subject must not be null")
        Objects.requireNonNull(htmlMessage, "htmlMessage must not be null")
        Objects.requireNonNull(mimeType, "mimeType must not be null")
        if (this.senderAddress == null) {
            throw new IllegalStateException("senderAddress is null. Please set it by calling MailKeyword.Builder.senderAddress(String)")
        }
        if (this.senderPassword == null) {
            throw new IllegalStateException("senderPassword is null. Please set it by calling MailKeyword.Builder.senderPassword(String)")
        }
        if (this.host == null) {
            throw new IllegalStateException("host is null. Please set it by calling MailKeyword.Builder.host(String)")
        }
        if (this.port == null) {
            throw new IllegalStateException("port is null. Please set it by calling MailKeyword.Builder.port(String)")
        }
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", this.host);
        props.put("mail.smtp.port", this.port);
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(this.senderAddress, this.senderPassword);
                    }
                });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.senderAddress));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(receiverAddress));
            message.setSubject(subject);
            message.setContent(htmlMessage, mimeType);
            Transport.send(message);
        } catch (SendFailedException e) {
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true
    }
}