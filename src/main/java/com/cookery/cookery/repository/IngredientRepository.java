package com.cookery.cookery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cookery.cookery.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findByUserId(Long userId);
}
