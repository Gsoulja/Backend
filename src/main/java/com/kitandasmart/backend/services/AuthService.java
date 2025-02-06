package com.kitandasmart.backend.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {


    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    public void generateToken(String userEmail) {
        String magicToken = UUID.randomUUID().toString();
        tokenStore.put(magicToken,userEmail);

        final String username = "ateresa1011@gmail.com"; // Your Gmail
        final String appPassword = ""; // Your App Password

        // Set up mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Authenticate using app password
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("glodyfigueiredo@outlook.com"));
            message.setSubject("Test Email from Java");
            message.setText("Hello, this is a test email sent from Java using Gmail App Password! http://localhost:8081/send-token?email="+magicToken);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public  boolean validateToken(String magicToken) {
        return tokenStore.containsKey(magicToken);
    }

    public String getTokenUserName(String magicToken) {
        return tokenStore.get(magicToken);
    }
}
