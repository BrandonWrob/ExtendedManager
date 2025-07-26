package wolfcafe.service;

import wolfcafe.dto.InventoryDto;
import wolfcafe.entity.Ingredient;
import wolfcafe.exception.ResourceNotFoundException;

/**
 * Interface defining the inventory behaviors.
 */
public interface InventoryService {

    /**
     * Creates the inventory.
     *
     * @param inventoryDto
     *            inventory to create
     * @return updated inventory after creation
     * @throws IllegalArgumentException
     *             if attempting to add a negative amount
     */
    InventoryDto createInventory ( InventoryDto inventoryDto );

    /**
     * Returns the single inventory.
     *
     * @return the single inventory
     */
    InventoryDto getInventory ();

    /**
     * Returns an Ingredient from the Inventory with the given name, or null if
     * it doesn't exist.
     *
     * @param name
     *            name of Ingredient to find
     * @return named Ingredient
     */
    Ingredient getInventoryIngredient ( String name );

    /**
     * Updates the contents of the inventory.
     *
     * @param inventoryDto
     *            values to update
     * @return updated inventory
     * @throws ResourceNotFoundException
     *             if the inventory does not exist
     * @throws IllegalArgumentException
     *             if the ingredient doesn't exist in the inventory, or if
     *             attempting to add a negative amount
     */
    InventoryDto updateInventory ( InventoryDto inventoryDto );

    /**
     * Returns true if the ingredient already exists in the inventory.
     *
     * @param inventoryDto
     *            current inventory
     * @param ingredientName
     *            ingredient's name to check
     * @return true if already in the inventory
     */
    boolean isDuplicateName ( InventoryDto inventoryDto, String ingredientName );

    /**
     * Compares a valid Recipe Ingredient with its corresponding Inventory
     * Ingredient and returns whether there is enough of that Ingredient in the
     * Inventory to make the Recipe.
     *
     * @param recIngredient
     *            Recipe Ingredient to compare
     * @return boolean based on the amount comparison
     */
    boolean checkIngredient ( Ingredient recIngredient );

}
