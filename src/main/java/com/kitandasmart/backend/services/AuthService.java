package com.kitandasmart.backend.services;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


    public String generateToken(String userEmail)  {
        String magicToken = UUID.randomUUID().toString();
        tokenStore.put(magicToken,userEmail);

        return magicToken;
    }

    public  boolean validateToken(String magicToken) {
        return tokenStore.containsKey(magicToken);
    }

    public String getTokenUserName(String magicToken) {
        return tokenStore.get(magicToken);
    }
}
