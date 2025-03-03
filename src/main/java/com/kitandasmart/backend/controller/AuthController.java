package com.kitandasmart.backend.controller;


import com.kitandasmart.backend.services.AuthService;
import com.kitandasmart.backend.services.EmailService;
import com.kitandasmart.backend.services.UserService;
import com.kitandasmart.backend.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/send-token")
    public ResponseEntity<String> sendToken(@RequestParam String email)
    {
        String magicToken= authService.generateToken(email);
        userService.registerUser(email);

        emailService.sendEmails(email, magicToken);
        return ResponseEntity.ok("Magic token sent to your email");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String token){
        if(!authService.validateToken(token)) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String userEmail = authService.getTokenUserName(token);
        UserDetails userDetails = userService.loadUserByUsername(userEmail);
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("jwt", jwt);

        return ResponseEntity.ok(response);
    }

}
