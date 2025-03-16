package com.cookery.cookery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cookery.cookery.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByUserId(Long userId);

    
}
