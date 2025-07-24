package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Recipe;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.RecipeMapper;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.RecipeService;

/**
 * Implementation of the RecipeService interface.
 */
@Service
public class RecipeServiceImpl implements RecipeService {

	/** Connection to the repository to work with the DAO + database */
	@Autowired
	private RecipeRepository recipeRepository;
    /** Reference to IngredientService (and IngredientServiceImpl). */
	@Autowired
	private IngredientService ingredientService;
    /** Reference to InventoryService (and InventoryServiceImpl). */
	@Autowired
	private InventoryService inventoryService;
	
    /**
     * Creates a recipe with the given information.
     * @param recipeDto recipe to create
     * @return created recipe
     * @throws IllegalArgumentException
     *             if there are too many recipes in the system, if the recipe
     *             name is a duplicate, if the price is not a positive integer,
     *             if it doesn't contain at least one ingredient, or any of the
     *             ingredients have negative amounts
     */
	@Override
	public RecipeDto createRecipe(RecipeDto recipeDto) {
		List<Recipe> existingRecipes = recipeRepository.findAll();
	    if (existingRecipes.size() >= 3) {
	        throw new IllegalArgumentException("Too many recipes in the system. You cannot add more than 3 recipes.");
	    }
	    
	    // Check if the recipe name is a duplicate
	    if (isDuplicateName(recipeDto.getName())) {
	        throw new IllegalArgumentException("Recipe name already exists. Please choose a different name.");
	    }
	    
	    // Validate price
	    if (recipeDto.getPrice() <= 0) {
	        throw new IllegalArgumentException("Recipe price must be a positive integer.");
	    }
	    
	    // Validate ingredients
	    if (recipeDto.getIngredients() == null || recipeDto.getIngredients().isEmpty()) {
	        throw new IllegalArgumentException("A recipe must have at least one ingredient.");
	    }
	    
	    for (int i = 0; i < recipeDto.getIngredients().size(); i++) {
	    	if (recipeDto.getIngredients().get(i).getAmount() < 0) {
	    		throw new IllegalArgumentException("All ingredient amounts should be positive integers.");
	    	}
	    }
		Recipe recipe = RecipeMapper.mapToRecipe(recipeDto);
		Recipe savedRecipe = recipeRepository.save(recipe);
		return RecipeMapper.mapToRecipeDto(savedRecipe);
	}
	
	/**
	 * Returns the recipe with the given id.
	 * @param recipeId recipe's id
	 * @return the recipe with the given id
	 * @throws ResourceNotFoundException if the recipe doesn't exist
	 */
	@Override
	public RecipeDto getRecipeById(Long recipeId) {
		Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
				() -> new ResourceNotFoundException("Recipe does not exist with id " + recipeId)
		);
		return RecipeMapper.mapToRecipeDto(recipe);
	}
	
	/**
	 * Returns the recipe with the given name.
	 * @param recipeName recipe's name
	 * @return the recipe with the given name.
	 * @throws ResourceNotFoundException if the recipe doesn't exist
	 */
	@Override
	public RecipeDto getRecipeByName(String recipeName) {
		Recipe recipe = recipeRepository.findByName(recipeName).orElseThrow(
				() -> new ResourceNotFoundException("Recipe does not exist with name " + recipeName)
		);
		return RecipeMapper.mapToRecipeDto(recipe);		
	}
	
	/**
	 * Returns true if the recipe already exists in the database.
	 * @param recipeName recipe's name to check
	 * @return true if already in the database
	 */
	@Override
	public boolean isDuplicateName(String recipeName) {
		try {
			getRecipeByName(recipeName);
			return true;
		} catch (ResourceNotFoundException e) {
			return false;
		}
	}

	/**
	 * Returns a list of all the recipes.
	 * @return all the recipes
	 */
	@Override
	public List<RecipeDto> getAllRecipes() {
		List<Recipe> recipes = recipeRepository.findAll();
		return recipes.stream().map((recipe) -> RecipeMapper.mapToRecipeDto(recipe)).collect(Collectors.toList());
	}
	
	/**
	 * Updates the recipe with the given id with the recipe information.
	 * @param recipeId id of recipe to update
	 * @param recipeDto values to update
	 * @return updated recipe
	 * @throws ResourceNotFoundException if the recipe doesn't exist or any of the ingredients from the DTO are invalid
	 * @throws IllegalArgumentException if the recipe DTO contains a negative price, or a null or empty ingredients list
	 */
	@Override
	@Transactional
	public RecipeDto updateRecipe(Long recipeId, RecipeDto recipeDto) {
		Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
				() -> new ResourceNotFoundException("Recipe does not exist with id " + recipeId)
		);
		if (recipeDto.getPrice() < 0) {
			throw new IllegalArgumentException("Recipe price must be a positive integer.");
		}
		if (recipeDto.getIngredients() == null || recipeDto.getIngredients().isEmpty()) {
			throw new IllegalArgumentException("A recipe must have ingredients.");
		}
		
		List<Ingredient> toAdd = recipeDto.getIngredients();
		for (int i = 0; i < toAdd.size(); i++) {
			if (!validateIngredient(toAdd.get(i))) {
				throw new ResourceNotFoundException("Ingredient " + toAdd.get(i).getName() + " does not exist.");
			}
		}
		
          // delete old recipe ingredient objects out of ingredient database
        List<Ingredient> deleted = recipe.getIngredients();
        for(int i = 0; i < deleted.size(); i++) {
            ingredientService.deleteIngredient(deleted.get(i).getId());
        }
		
		recipe.getIngredients().clear();
		recipeRepository.save(recipe);
		
		recipe.setName(recipe.getName());
		recipe.setPrice(recipeDto.getPrice());
		recipe.setIngredients(recipeDto.getIngredients());
						
		Recipe savedRecipe = recipeRepository.save(recipe);
		
		return RecipeMapper.mapToRecipeDto(savedRecipe);
	}

	/**
	 * Deletes the recipe with the given id.
	 * @param recipeId recipe's id
	 * @throws ResourceNotFoundException if the recipe doesn't exist
	 */
	@Override
	public void deleteRecipe(Long recipeId) {
		Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
				() -> new ResourceNotFoundException("Recipe does not exist with id " + recipeId)
		);
		
		// delete old ingredient objects out of ingredient database
		List<Ingredient> deleted = recipe.getIngredients();
		for(int i = 0; i < deleted.size(); i++) {
			ingredientService.deleteIngredient(deleted.get(i).getId());
		}
		
		recipeRepository.delete(recipe);
	}
	
	/**
	 * Helper function to return whether the given ingredient exists within the inventory.
	 * 
	 * @param ingredient ingredient to check
	 * @return boolean based on ingredient status in the inventory
	 */
	private boolean validateIngredient(Ingredient ingredient) {
	    return inventoryService.isDuplicateName(inventoryService.getInventory(), ingredient.getName());
	}
}
