package com.boghdady.jwtdemo.services;

import com.boghdady.jwtdemo.dtos.LoginUserDto;
import com.boghdady.jwtdemo.dtos.RegisterUserDto;
import com.boghdady.jwtdemo.entities.User;
import com.boghdady.jwtdemo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class handling user authentication operations including signup and login
 */
@Service
public class AuthenticationService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor for dependency injection
     */
    @Autowired
    public AuthenticationService(
                                UserRepository userRepository,
                                AuthenticationManager authenticationManager,
                                PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user
     * @param input DTO containing registration details
     * @return newly created User entity
     */
    public User signup(RegisterUserDto input) {
        // Create new user instance
        User user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        // Save and return the new user
        return userRepository.save(user);
    }

    /**
     * Authenticates an existing user
     * @param input DTO containing login credentials
     * @return User entity if authentication successful
     * @throws RuntimeException if user not found
     */
    public User authenticate(LoginUserDto input) {
        // Verify credentials using authentication manager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        // Retrieve and return user if authentication successful
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }



}
