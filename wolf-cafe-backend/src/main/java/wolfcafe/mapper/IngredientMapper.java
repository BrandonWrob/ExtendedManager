/**
 * 
 */
package edu.ncsu.csc326.wolfcafe.mapper;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;

/**
 * Converts between IngredientDto and Ingredient entity.
 */
public class IngredientMapper {
	
	/**
	 * Converts an Ingredient entity to IngredientDto.
	 * @param ingredient Ingredient object to convert
	 * @return IngredientDto object
	 */
	public static IngredientDto mapToIngredientDto(Ingredient ingredient) {
		IngredientDto ingredientDto = new IngredientDto();
		ingredientDto.setId(ingredient.getId());
		ingredientDto.setName(ingredient.getName());
		ingredientDto.setAmount(ingredient.getAmount());
		return ingredientDto;
	}	
	
	/**
	 * Converts an IngredientDto to Ingredient entity.
	 * @param ingredientDto IngredientDto object to convert
	 * @return Ingredient object
	 */
	public static Ingredient mapToIngredient(IngredientDto ingredientDto) {
		Ingredient ingredient = new Ingredient();
		ingredient.setId(ingredientDto.getId());
		ingredient.setName(ingredientDto.getName());
		ingredient.setAmount(ingredientDto.getAmount());
		return ingredient;
	}

}

