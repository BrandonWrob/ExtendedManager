/**
 * 
 */
package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;

/**
 * Interface defining the ingredient behaviors.
 */
public interface IngredientService {
	
	/**
	 * Creates the ingredient.
	 * @param ingredientDto ingredient to create
	 * @return updated ingredient after creation
	 * @throws IllegalArgumentException if the ingredient name is a duplicate in the inventory, or if 
	 * attempting to add a negative amount
	 */
	IngredientDto createIngredient(IngredientDto ingredientDto);
	
	/**
	 * Returns the ingredient with the given id.
	 * @param ingredientId ingredient's id
	 * @return the ingredient with the given id
	 * @throws ResourceNotFoundException if the ingredient doesn't exist
	 */
	IngredientDto getIngredientById(Long ingredientId);
	
	/**
	 * Returns a list of all the ingredients.
	 * @return all the ingredients
	 */
	List<IngredientDto> getAllIngredients();
	
	/**
	 * Deletes the ingredient with the given id.
	 * @param ingredientId ingredient's id
	 * @throws ResourceNotFoundException if the ingredient doesn't exist
	 */
	void deleteIngredient(Long ingredientId);
	
	/**
	 * Deletes all ingredients.
	 */
	void deleteAllIngredients();
	
	/**
	 * Updates the ingredient with the given id with the ingredient information
	 * @param ingredientId id of ingredient to update
	 * @param ingredientDto values to update with
	 * @return updated IngredientDto object
	 * @throws ResourceNotFoundException if the ingredient doesn't exist
	 */
	IngredientDto updateIngredient(Long ingredientId, IngredientDto ingredientDto);
}