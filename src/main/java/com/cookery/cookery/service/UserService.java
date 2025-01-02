package com.cookery.cookery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cookery.cookery.entity.User;
import com.cookery.cookery.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user)
    {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        
        return userRepository.findByEmail(email);
    }

    

}
