package com.cookery.cookery.service;

import java.util.Calendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cookery.cookery.entity.PasswordResetToken;
import com.cookery.cookery.entity.User;
import com.cookery.cookery.repository.PasswordResetTokenRepository;
import com.cookery.cookery.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    //Find user by username
    public User findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    //Find user by email
    public User findByEmail(String email) {
        
        return userRepository.findByEmail(email);
    }

    //Save the user
    public User saveUser(User user)
    {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    //Update user information
    public User updateUser(User user, String firstName, String lastName, String email){
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        return userRepository.save(user);
    }


    //PASSWORD RESET
    public void createPasswordResetTokenForUser(User user, String token) {
        
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);

    }

    //Retrieve user by the password rest token
    public Optional<User> getUserByPasswordResetToken(String token){
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if(resetToken != null && resetToken.getUser() != null){
            return Optional.of(resetToken.getUser());
        }
        return Optional.empty();
    }

    //Validate the password reset token
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
            : isTokenExpired(passToken) ? "expired"
            : null;
    }

    //Check if token is found
    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    //Check if token is expired
    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime()); 
    }

    //Change the user password
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
