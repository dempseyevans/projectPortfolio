package com.cookery.cookery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cookery.cookery.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    
}
