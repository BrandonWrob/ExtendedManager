package wolfcafe.service;

import java.util.List;

import wolfcafe.dto.RecipeDto;

/**
 * Interface defining the recipe behaviors.
 */
public interface RecipeService {
	
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
	RecipeDto createRecipe(RecipeDto recipeDto);
	
	/**
	 * Returns the recipe with the given id.
	 * @param recipeId recipe's id
	 * @return the recipe with the given id
	 * @throws ResourceNotFoundException if the recipe doesn't exist
	 */
	RecipeDto getRecipeById(Long recipeId);
	
	/**
	 * Returns the recipe with the given name.
	 * @param recipeName recipe's name
	 * @return the recipe with the given name.
	 * @throws ResourceNotFoundException if the recipe doesn't exist
	 */
	RecipeDto getRecipeByName(String recipeName);
	
	/**
	 * Returns true if the recipe already exists in the database.
	 * @param recipeName recipe's name to check
	 * @return true if already in the database
	 */
	boolean isDuplicateName(String recipeName);
	
	/**
	 * Returns a list of all the recipes.
	 * @return all the recipes
	 */
	List<RecipeDto> getAllRecipes();
	
	/**
	 * Updates the recipe with the given id with the recipe information.
	 * @param recipeId id of recipe to update
	 * @param recipeDto values to update
	 * @return updated recipe
	 * @throws ResourceNotFoundException if the recipe doesn't exist or any of the ingredients from the DTO are invalid
	 * @throws IllegalArgumentException if the recipe DTO contains a negative price, or a null or empty ingredients list
	 */
	RecipeDto updateRecipe(Long recipeId, RecipeDto recipeDto);
	
	/**
	 * Deletes the recipe with the given id.
	 * @param recipeId recipe's id
	 * @throws ResourceNotFoundException if the recipe doesn't exist
	 */
	void deleteRecipe(Long recipeId);

}
