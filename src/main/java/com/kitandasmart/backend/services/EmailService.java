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
    @Value("${email}")
    private String email;
    @Value("${client_id}")
    private String client_id;
    @Value("${client_secret}")
    private String client_secret;
    @Value("${tenant_id}")
    private String tenant_it;



    public void sendEmails(String userEmail, String magicToken) {
        try {
            // Create GraphEmailSender with your credentials
            GraphEmailSender sender = new GraphEmailSender(
                    client_id,
                    client_secret,
                    tenant_it,
                    email
            );

            // Send the login token email
            sender.sendLoginToken(userEmail, magicToken);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
