package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.payload.LoginRequest;
import com.ecommerce.ecommercebackend.payload.AuthResponse;
import com.ecommerce.ecommercebackend.payload.RegisterRequest;
import com.ecommerce.ecommercebackend.payload.PasswordResetRequest;
import com.ecommerce.ecommercebackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth") 
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /auth/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerUser(registerRequest);
            
            // Log the user in immediately after registration and return token
            String token = authService.loginUser(
                // Use email as username for login
                new LoginRequest(registerRequest.getEmail(), registerRequest.getPassword())
            );
            
            // Frontend expects { token: 'jwt_string' } with HTTP 201
            return new ResponseEntity<>(new AuthResponse(token), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Return HTTP 400 or 409 with the error message
            return new ResponseEntity<>(new AuthResponse(e.getMessage(), true), HttpStatus.BAD_REQUEST);
        }
    }

    // POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.loginUser(loginRequest);
            // Frontend expects { token: 'jwt_string' } with HTTP 200
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (RuntimeException e) {
            // Return HTTP 401 with the error message
            return new ResponseEntity<>(new AuthResponse(e.getMessage(), true), HttpStatus.UNAUTHORIZED);
        }
    }
    
    // POST /auth/forgot-password
    // Frontend sends: { "email": "..." }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null) {
                 return new ResponseEntity<>(new AuthResponse("Email is required.", true), HttpStatus.BAD_REQUEST);
            }
            authService.forgotPassword(email);
            
            // Return 202 (Accepted) or 200 (OK) to indicate the process started
            return ResponseEntity.accepted().build(); 
        } catch (RuntimeException e) {
            // In case of mail server error or bad request format
            return new ResponseEntity<>(new AuthResponse(e.getMessage(), true), HttpStatus.BAD_REQUEST);
        }
    }

    // POST /auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            
            // Return 200 OK on success
            return ResponseEntity.ok().build(); 
        } catch (RuntimeException e) {
            // Return HTTP 400 for invalid token or weak password errors
            return new ResponseEntity<>(new AuthResponse(e.getMessage(), true), HttpStatus.BAD_REQUEST);
        }
    }
}