package com.cookery.cookery.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cookery.cookery.entity.Recipe;
import com.cookery.cookery.repository.RecipeRepository;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public List<Recipe> findAll(){
        return recipeRepository.findAll();
    }

    public Optional<Recipe> findById(Long id){
        return recipeRepository.findById(id);
    }

    //CRUD FUNCTIONALITY BELOW
    public void save(Recipe recipe){
        recipeRepository.save(recipe);
    }

    public void deleteById(Long id){
        recipeRepository.deleteById(id);
    }
}
