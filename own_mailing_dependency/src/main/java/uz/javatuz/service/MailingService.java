package uz.javatuz.service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailingService {



    public static boolean sendMessage(
            String from,
            String to,
            String secretKey,
            String subject,
            String text,
            Boolean isSSL){
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");

        if (isSSL) {
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.port", "465");
        }
        else {
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.port", "587");
        }

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, secretKey);
            }
        });

        Message message = new MimeMessage(session);
        try{
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            System.out.println("Message sent successfully from dependency");
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
