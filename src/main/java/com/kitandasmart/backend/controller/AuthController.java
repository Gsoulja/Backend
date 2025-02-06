package com.kitandasmart.backend.controller;


import com.kitandasmart.backend.services.AuthService;
import com.kitandasmart.backend.services.UserService;
import com.kitandasmart.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/send-token")
    public ResponseEntity<String> sendToken(@RequestParam String email)
    {
        try{
            authService.generateToken(email);
            userService.registerUser(email);
            return ResponseEntity.ok("Magic token sent to your email");
        } catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending magic token");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String token){
        if(!authService.validateToken(token))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String userEmail =authService.getTokenUserName(token);
        UserDetails userDetails = userService.loadUserByUsername(userEmail);
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(jwt);
    }
}
