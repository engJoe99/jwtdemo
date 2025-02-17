package com.boghdady.jwtdemo.configs;

import com.boghdady.jwtdemo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Configuration class for setting up authentication and security beans
@Configuration
public class ApplicationConfiguration {

    // Repository for accessing user data
    private final UserRepository userRepository;

    // Constructor injection of UserRepository
    @Autowired
    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Bean to provide user details service that loads user-specific data
    @Bean
    UserDetailsService userDetailsService() {
        // Returns a lambda that searches for users by email
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Bean for password encryption and verification(the existing one)
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean that provides the authentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean that provides the main authentication provider
    @Bean
    AuthenticationProvider authenticationProvider() {
        // Creates a DAO authentication provider that uses database-backed user details service
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Sets the user details service to load user data
        authProvider.setUserDetailsService(userDetailsService());
        // Sets the password encoder for password verification
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

}