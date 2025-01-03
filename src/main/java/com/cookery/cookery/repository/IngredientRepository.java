package com.cookery.cookery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cookery.cookery.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

}
