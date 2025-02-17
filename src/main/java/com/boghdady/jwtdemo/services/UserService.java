package com.boghdady.jwtdemo.services;

import com.boghdady.jwtdemo.entities.User;
import com.boghdady.jwtdemo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {

        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(e -> users.add(e));

        return users;
    }
}