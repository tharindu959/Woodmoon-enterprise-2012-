package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.model.User;
import com.ecommerce.ecommercebackend.payload.LoginRequest;
import com.ecommerce.ecommercebackend.payload.RegisterRequest;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import com.ecommerce.ecommercebackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;

    // --- Register Logic ---
    public User registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email address is already registered.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        
        return userRepository.save(user);
    }

    // --- Login Logic ---
    public String loginUser(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password. Authentication failed.");
        }

        return jwtUtil.generateToken(request.getUsername());
    }
    
    // --- Forgot Password Logic ---
    public void forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        // Return success even if the email doesn't exist to prevent enumeration attacks
        if (userOpt.isEmpty()) {
             System.out.println("Attempted password reset for unknown email: " + email);
             return; 
        }
        
        User user = userOpt.get();
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        userRepository.save(user);
        
        // Construct the full reset URL for your Next.js application
        String resetUrl = "http://localhost:3000/reset-password?token=" + resetToken;
        emailService.sendResetPasswordEmail(user.getEmail(), resetUrl);
    }
    
    // --- Reset Password Logic ---
    public void resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetPasswordToken(token);
        
        if (userOpt.isEmpty() || token == null || token.isEmpty()) {
            throw new RuntimeException("Invalid or expired password reset token.");
        }
        
        User user = userOpt.get();
        
        // Basic check for new password strength/length if needed
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("New password must be at least 8 characters long.");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null); // Invalidate the token
        userRepository.save(user);
    }
}