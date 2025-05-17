package com.cookery.cookery.service;

import java.util.ArrayList;
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

    //Find all ingredients
    public List<Ingredient> findAll(){
        return ingredientRepository.findAll();
    }

    //Find all ingredients by their id
    public Optional<Ingredient> findById(Long id) {
            return ingredientRepository.findById(id);
        }

    //Find all ingredients by the userId
    public List<Ingredient> findAllByUser(Long userId){
        return ingredientRepository.findByUserId(userId);
    }

    //Search bar for ingredients
    public List<Ingredient> searchIngredients(String query, String username) {

        //New list for filtered ingredients
        List<Ingredient> filteredIngredients = new ArrayList<>();

        //List for all existing ingredients
        List<Ingredient> allIngredients = ingredientRepository.findByUserUsername(username);
        String lowerCaseQuery = query.toLowerCase();

        for (Ingredient ingredient : allIngredients) {
            // Check the ingredient's name
            if (ingredient.getName() != null && ingredient.getName().toLowerCase().contains(lowerCaseQuery)) {
                filteredIngredients.add(ingredient);
            }
            // Check the price category using symbols
            else if (lowerCaseQuery.equals("$") && ingredient.getPriceCategory() == 1) {
                filteredIngredients.add(ingredient);
            } else if (lowerCaseQuery.equals("$$") && ingredient.getPriceCategory() == 2) {
                filteredIngredients.add(ingredient);
            } else if (lowerCaseQuery.equals("$$$") && ingredient.getPriceCategory() == 3) {
                filteredIngredients.add(ingredient);
            }
        }
        
        return filteredIngredients;
    }
    
    //Save new ingredient
    public Ingredient save(Ingredient ingredient){
        return ingredientRepository.save(ingredient);
    }
    
    //Delete ingredient
    public void deleteById(Long id){
        ingredientRepository.deleteById(id);
    }
}
