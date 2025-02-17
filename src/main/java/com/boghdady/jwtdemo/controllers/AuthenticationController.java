package com.boghdady.jwtdemo.controllers;

import com.boghdady.jwtdemo.dtos.LoginUserDto;
import com.boghdady.jwtdemo.dtos.RegisterUserDto;
import com.boghdady.jwtdemo.entities.User;
import com.boghdady.jwtdemo.services.AuthenticationService;
import com.boghdady.jwtdemo.services.JwtService;
import com.boghdady.jwtdemo.responses.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(JwtService jwtService,
                                    AuthenticationService authenticationService) {

        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    /**
     * Handles user registration/signup
     * @param registerUserDto Data transfer object containing user registration details
     * @return ResponseEntity containing the registered User object
     */
    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }




    /**
     * Handles user authentication/login
     * @param loginUserDto Data transfer object containing user login credentials
     * @return ResponseEntity containing JWT token and expiration time in LoginResponse
     */
    @PostMapping("/login")
        public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);

            String jwtToken = jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        }



}