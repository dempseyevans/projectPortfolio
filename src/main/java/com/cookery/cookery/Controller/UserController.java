package com.cookery.cookery.Controller;

import java.security.Principal;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cookery.cookery.dto.PasswordDto;
import com.cookery.cookery.entity.GenericResponse;
import com.cookery.cookery.entity.User;
import com.cookery.cookery.service.FeedbackService;
import com.cookery.cookery.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;



@Controller
@RequestMapping("/users")
public class UserController {

    private final FeedbackService feedbackService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messages;

    @Autowired
    private Environment env;


    UserController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }


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
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes)
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
                user.setRole("USER");
                userService.saveUser(user);
                redirectAttributes.addFlashAttribute("successMessage", "You're all set! Log in and visit your user Information page for help getting started!");
                return "redirect:/login";}
            }
        catch (Exception e)
        {
            logger.error("Error registering user: " + e.getMessage());
            return "register";
        }
    }

    //Show Reset Password Page
    @GetMapping("/resetPassword")
    public String showResetPasswordForm() {
        return "forgotPassword";
    }
    

    //Reset Password
    @PostMapping("/resetPassword")
    @ResponseBody
    public GenericResponse resetPassword(HttpServletRequest request, @RequestParam("email") String userEmail) {
    
        try{
            User user = userService.findByEmail(userEmail);
            logger.info("Recieved user email: " + user.getEmail());
            if(user == null){
                logger.info("no user found");
            }

            String token = UUID.randomUUID().toString();
            
            //Debugging Token creation
            logger.info("Created reset token: " + token + " for user: " + user.getEmail());
            userService.createPasswordResetTokenForUser(user, token);
            
            //Debugging E-mail sent to user
            logger.info("Sending reset email to user: " + user.getEmail());
            mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
            logger.info("email sent successfully");

            return new GenericResponse(
                "An email has been sent with instructions to reset your password"
            );
        } catch (Exception e){
            logger.error("Error during password reset: ", e);
            return new GenericResponse("An error occurred while processing your request");
        }
    }

    //METHODS FOR TOKEN and EMAIL
    private SimpleMailMessage constructResetTokenEmail(
        String contextPath, Locale locale, String token, User user) {
            String url = contextPath + "/users/changePassword?token=" + token;
            String message = "Please click the provided link to reset your password. This link expires in 24 hours:";
            return constructEmail("Reset Password", message + " \r\n" + url, user);
        }

    private SimpleMailMessage constructEmail(String subject, String body,
        User user) {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setSubject(subject);
            email.setText(body);
            email.setTo(user.getEmail());
            email.setFrom(env.getProperty("support.email"));
            return email;
        }

    //Get App URL
    private String getAppUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + 
            ":" + request.getServerPort() + request.getContextPath();
    }
    

    //Show change password page
    @GetMapping("/changePassword")
    public String showChangePasswordPage(Locale locale, Model model,
    @RequestParam("token") String token) {
        String result = userService.validatePasswordResetToken(token);
        if(result != null) {
            return "redirect:/login.html?lang="
                + locale.getLanguage() + "&message=Invalid or expired token.";
        } else {
            model.addAttribute("token", token);
            return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
        }
    }
    

    //User Information Page
    @GetMapping("/userInfo")
    public String userInformationForm(Model model) {
        //Retrieve currently logged-in users username 
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else { username=principal.toString();}

        User user = userService.findByUsername(username);

        model.addAttribute("user", user);

        return "userInformation";
    }

    //Save updated password
    @PostMapping("/savePassword")
    public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {

        String result = userService.validatePasswordResetToken(passwordDto.getToken());

        if(result != null) {
            return new GenericResponse(messages.getMessage(
                "auth.message." + result, null, locale));
        }

        Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if(user.isPresent()) {
            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            return new GenericResponse(messages.getMessage(
                "message.resetPasswordSuc", null, locale));
        } else {
            return new GenericResponse(messages.getMessage(
                "auth.message.invalid", null, locale));
        }
    }


    //User Feedback Form
    @PostMapping("/submitFeedback")
    public String submitFeedback(@RequestParam("feedback") String feedbackText, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        //Get ID of user
        User user = userService.findByUsername(principal.getName());

        //Save user feedback
        feedbackService.saveFeedback(feedbackText, user);

        //Feedback success message
        redirectAttributes.addFlashAttribute("successMessage", "Feedback successfully submitted!");

        model.addAttribute("user", user);
        
        return "redirect:/users/userInfo";
    }
    
    //Update User Info page
    @GetMapping("/edit")
    public String showUpdateUserInfoPage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("user", user);

        return "editUserInfo";
    }
    

    //Update user info
    @PostMapping("/update")
    public String updateUserInfo(@RequestParam("firstName") String firstname, @RequestParam("lastName") String lastName, @RequestParam("email") String email, Principal principal, RedirectAttributes redirectAttributes) {
        
        User user = userService.findByUsername(principal.getName());

        userService.updateUser(user, firstname, lastName, email);
        
        redirectAttributes.addFlashAttribute("successMessage", "Your information has successfully updated");

        return "redirect:/users/userInfo";
    }
    
    
}
