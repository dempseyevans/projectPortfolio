package com.cookery.cookery.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cookery.cookery.entity.Ingredient;
import com.cookery.cookery.repository.IngredientRepository;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    public List<Ingredient> findAll(){
        return ingredientRepository.findAll();
    }

    public Optional<Ingredient> findById(Long id) {
            return ingredientRepository.findById(id);
        }

    public List<Ingredient> findAllByUser(Long userId){
        return ingredientRepository.findByUserId(userId);
    }

    
    
    //CRUD FUNCTIONALITY BELOW
    public Ingredient save(Ingredient ingredient){
        return ingredientRepository.save(ingredient);
    }
    
    public void deleteById(Long id){
        ingredientRepository.deleteById(id);
    }
}
