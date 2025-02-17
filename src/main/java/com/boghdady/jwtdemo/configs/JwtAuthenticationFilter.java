package com.boghdady.jwtdemo.configs;


import com.boghdady.jwtdemo.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Filter for handling JWT authentication in the request pipeline
 * Extends OncePerRequestFilter to guarantee a single execution per request
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Dependencies required for JWT processing and exception handling
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor for dependency injection
     * @param jwtService Service for JWT operations
     * @param userDetailsService Service to load user details from DB
     * @param handlerExceptionResolver Resolver for handling exceptions
     */
    @Autowired
    public JwtAuthenticationFilter(
                                    JwtService jwtService,
                                    UserDetailsService userDetailsService,
                                    HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Core filter method that processes each request
     * Validates JWT token and sets up security context
     */
    @Override
    protected void doFilterInternal(
                                    @NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Extract JWT token from Authorization header
        final String authHeader = request.getHeader("Authorization");

        // If no token found or invalid format, continue with filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract token by removing "Bearer " prefix
            final String jwt = authHeader.substring(7);
            // Get username/email from token
            final String userEmail = jwtService.extractUsername(jwt);

            // Get current authentication status
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Only process if user not already authenticated
            if (userEmail != null && authentication == null) {
                // Load user details from database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Validate token against user details
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // Add request details to authentication token
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            // Continue with filter chain
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            // Handle any exceptions during processing
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}