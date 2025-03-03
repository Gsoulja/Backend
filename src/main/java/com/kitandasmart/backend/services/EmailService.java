package com.kitandasmart.backend.services;

import com.kitandasmart.backend.util.GraphEmailSender;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {
    @Value("${spring.mail.username}")
    private String email;
    @Value("${smtp.server}")
    private String smtp_server;
    @Value("${smtp.port}")
    private int smtp_port;
    @Value("${spring.mail.password}")
    private String password;

    @NotNull
    public Session getSession() {
        // Set SMTP properties

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtp_server);
        properties.put("mail.smtp.port", smtp_port);

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
        return session;
    }

    public void sendEmails(String userEmail, String magicToken) {
        try {
            // Create GraphEmailSender with your credentials
            GraphEmailSender sender = new GraphEmailSender(
                    "3241235d-7f17-4243-8a5b-0ba5aef8e63f",
                    "S1m8Q~Y2jopF0Ut3MnUc13DJCfjF.q_OoqY4Iama",
                    "cf32cf73-55cd-4b11-9cb4-6ed67808cff3",
                    "noreply@kitandasmart.com"
            );

            // Send the login token email
            sender.sendLoginToken(userEmail, magicToken);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
