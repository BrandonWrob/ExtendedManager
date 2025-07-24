/**
 * 
 */
package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.IngredientMapper;
import edu.ncsu.csc326.wolfcafe.mapper.InventoryMapper;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 * Implementation of the IngredientService interface.
 */
@Service
public class IngredientServiceImpl implements IngredientService {
	
	/** Connection to the repository to work with the ingredient DAO + database */
	@Autowired
	private IngredientRepository ingredientRepository;
	/** Connection to the repository to work with the inventory DAO + database */
	@Autowired
	private InventoryRepository inventoryRepository;
    /** Reference to InventoryService (and InventoryServiceImpl). */
	@Autowired
	private InventoryService inventoryService;

	/**
	 * Creates the ingredient.
	 * @param ingredientDto ingredient to create
	 * @return updated ingredient after creation
	 * @throws IllegalArgumentException if the ingredient name is a duplicate in the inventory, or if 
	 * attempting to add a negative amount
	 */
	@Override
	public IngredientDto createIngredient(IngredientDto ingredientDto) {
		
		Ingredient ingredient = IngredientMapper.mapToIngredient(ingredientDto);		
		
		InventoryDto inventoryDto = inventoryService.getInventory();
		Inventory inventory = InventoryMapper.mapToInventory(inventoryDto);
		
		if (!inventoryService.isDuplicateName(inventoryDto, ingredientDto.getName())) {
			if (ingredientDto.getAmount() >= 0) {
				Ingredient savedIngredient = ingredientRepository.save(ingredient);
				inventory.addIngredient(savedIngredient);
				inventoryRepository.save(inventory);
				return IngredientMapper.mapToIngredientDto(savedIngredient);
			} else {
				throw new IllegalArgumentException("Ingredient amount cannot be negative.");
			}
		} else {
			throw new IllegalArgumentException("Cannot add duplicate Ingredients.");
		}
	}
	
	/**
	 * Returns the ingredient with the given id.
	 * @param ingredientId ingredient's id
	 * @return the ingredient with the given id
	 * @throws ResourceNotFoundException if the ingredient doesn't exist
	 */
	@Override
	public IngredientDto getIngredientById(Long ingredientId) {
		Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(
				() -> new ResourceNotFoundException("Ingredient does not exist with id " + ingredientId)
		);
		return IngredientMapper.mapToIngredientDto(ingredient);
	}

	/**
	 * Returns a list of all the ingredients.
	 * @return all the ingredients
	 */
	@Override
	public List<IngredientDto> getAllIngredients() {
		List<Ingredient> ingredients = ingredientRepository.findAll();
		return ingredients.stream().map((ingredient) -> IngredientMapper.mapToIngredientDto(ingredient)).collect(Collectors.toList());
	}

	/**
	 * Deletes the ingredient with the given id.
	 * @param ingredientId ingredient's id
	 * @throws ResourceNotFoundException if the ingredient doesn't exist
	 */
	@Override
	public void deleteIngredient(Long ingredientId) {
		Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(
				() -> new ResourceNotFoundException("Ingredient does not exist with id " + ingredientId)
		);
		ingredientRepository.delete(ingredient);
	}

	/**
	 * Deletes all ingredients.
	 */
	@Override
	public void deleteAllIngredients() {
		ingredientRepository.deleteAll();
	}
	
	/**
	 * Updates the ingredient with the given id with the ingredient information
	 * @param ingredientId id of ingredient to update
	 * @param ingredientDto values to update with
	 * @return updated IngredientDto object
	 * @throws ResourceNotFoundException if the ingredient doesn't exist
	 */
	@Override
	public IngredientDto updateIngredient(Long ingredientId, IngredientDto ingredientDto) {
		Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(
				() -> new ResourceNotFoundException("Ingredient does not exist with id " + ingredientId)
		);
		
		ingredient.setName(ingredientDto.getName());
		ingredient.setAmount(ingredientDto.getAmount());
		
		Ingredient savedIngredient = ingredientRepository.save(ingredient);
		return IngredientMapper.mapToIngredientDto(savedIngredient);
	}
	
}
