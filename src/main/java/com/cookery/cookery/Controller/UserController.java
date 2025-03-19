package com.cookery.cookery.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cookery.cookery.entity.User;
import com.cookery.cookery.service.UserService;



@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    //Display Registration form
    @GetMapping("/register")
    public String registrationForm(Model model) {
        logger.info("Accessing registration form");
        model.addAttribute("user", new User());
        return "register";
    }
    
    //Save new user
    //Try-catch handling for debugging save actions
    //If-else statement verifies email validity
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user)
    {
        try{
            User existingUser = userService.findByEmail(user.getEmail());
            if (existingUser != null)
            {
                logger.warn("User exists");
                return "register";
            }
            else{
                logger.info("User successfully registered");
                userService.saveUser(user);
                return "redirect:/login";}
            }
        catch (Exception e)
        {
            logger.error("Error registering user: " + e.getMessage());
            return "register";
        }
    }

    //User Information Page
    @GetMapping("/userInfo")
    public String userInformationForm(Model model) {
        //Retrieve currently logged-in users username through Spring Security Security Context Holder
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else { username=principal.toString();}

        User user = userService.findByUsername(username);

        model.addAttribute("user", user);

        return "userInformation";
    }
    

    /**FIND USER FOR TESTING**/
    @GetMapping("/{username}")
    public User getUserbyUsername(@PathVariable String username) {
        
        return userService.findByUsername(username);
    }
    
}
