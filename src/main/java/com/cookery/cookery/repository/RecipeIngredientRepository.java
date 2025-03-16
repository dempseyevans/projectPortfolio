package com.cookery.cookery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cookery.cookery.entity.RecipeIngredient;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    
}
