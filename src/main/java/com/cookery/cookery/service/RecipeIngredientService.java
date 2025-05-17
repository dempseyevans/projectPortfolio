package com.cookery.cookery.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cookery.cookery.entity.RecipeIngredient;
import com.cookery.cookery.repository.RecipeIngredientRepository;

@Service
public class RecipeIngredientService {

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    public List<RecipeIngredient> findAll() {
        return recipeIngredientRepository.findAll();
    }

    public void save(RecipeIngredient recipeIngredient) {
        recipeIngredientRepository.save(recipeIngredient);
    }

    public void deleteById(Long id) {
        recipeIngredientRepository.deleteById(id);
    }
}
