package org.gserve.api.logging;

import org.gserve.api.persistence.SystemVariables;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import java.util.Properties;

/**
 * Class for sending emails via SMTP as configured in Application's System Settings.
 * Created by: Dustin K. Redmond
 * Date: 08/07/2019 14:30
 */
@SuppressWarnings({"unused"})
public class SimpleEmail {

    private final String smtpUsername = SystemVariables.getValue("smtpUsername");
    private final String smtpPassword = SystemVariables.getValue("smtpPassword");
    private final String smtpServer = SystemVariables.getValue("smtpServer");
    private final String smtpPort = SystemVariables.getValue("smtpPort");

    /**
     * Sends an email using values set in system settings to resolve SMTP options.
     *
     * @param emailAddress Email address to which message will be sent.
     * @param subjectText Email subject text.
     * @param messageText Email message text.
     * @return True if email appears to have been sent successfully.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean send(String emailAddress, String subjectText, String messageText){
        Properties prop = new Properties();
        prop.put("mail.smtp.host", smtpServer);
        prop.put("mail.smtp.port", smtpPort);
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpUsername, smtpPassword);
                    }
                });
        try {
            javax.mail.Message message = new javax.mail.internet.MimeMessage(session);
            message.setFrom(new InternetAddress(smtpUsername));
            message.setRecipients(
                    javax.mail.Message.RecipientType.TO,
                    InternetAddress.parse(emailAddress)
            );
            message.setSubject(subjectText);
            message.setContent(messageText,"text/html; charset=utf-8");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
