package com.ecommerce.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; 

    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    
    // Field for password reset functionality
    private String resetPasswordToken;
}