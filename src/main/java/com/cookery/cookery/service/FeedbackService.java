package com.cookery.cookery.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cookery.cookery.entity.Feedback;
import com.cookery.cookery.entity.User;
import com.cookery.cookery.repository.FeedbackRepository;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public void saveFeedback(String feedbackText, User user) {
        Feedback feedback = new Feedback();
        feedback.setUser(user); // Associate the feedback with the user
        feedback.setFeedback(feedbackText);
        feedback.setSubmissionDate(new Date());
        feedback.setStatus("Pending"); // Set a default status

        feedbackRepository.save(feedback); // Persist the feedback to the database
    }
}
