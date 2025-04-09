package com.cookery.cookery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cookery.cookery.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
}
