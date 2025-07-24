
package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.InventoryMapper;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 * Implementation of the InventoryService interface.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    /**
     * Connection to the repository to work with the inventory DAO + database
     */
    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Creates the inventory.
     *
     * @param inventoryDto
     *            inventory to create
     * @return updated inventory after creation
     * @throws IllegalArgumentException
     *             if attempting to add a negative amount
     */
    @Override
    public InventoryDto createInventory ( final InventoryDto inventoryDto ) {
        if ( validateIngredients( inventoryDto.getIngredients() ) ) {
            final Inventory inventory = InventoryMapper.mapToInventory( inventoryDto );
            final Inventory savedInventory = inventoryRepository.save( inventory );
            return InventoryMapper.mapToInventoryDto( savedInventory );
        }
        else {
            throw new IllegalArgumentException( "Ingredient amount cannot be negative." );
        }
    }

    /**
     * Returns the single inventory.
     *
     * @return the single inventory
     */
    @Override
    public InventoryDto getInventory () {
        final List<Inventory> inventory = inventoryRepository.findAll();
        if ( inventory.size() == 0 ) {
            final InventoryDto newInventoryDto = new InventoryDto();
            newInventoryDto.setIngredients( new ArrayList<Ingredient>() );
            final InventoryDto savedInventoryDto = createInventory( newInventoryDto );
            return savedInventoryDto;
        }
        return InventoryMapper.mapToInventoryDto( inventory.get( 0 ) );
    }

    /**
     * Returns an Ingredient from the Inventory with the given name, or null if
     * it doesn't exist.
     *
     * @param name
     *            name of Ingredient to find
     * @return named Ingredient
     */
    @Override
    public Ingredient getInventoryIngredient ( final String name ) {
        final List<Ingredient> ingredients = getInventory().getIngredients();
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( ingredients.get( i ).getName().equals( name ) ) {
                return ingredients.get( i );
            }
        }
        return null;
    }

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
    @Override
    public InventoryDto updateInventory ( final InventoryDto inventoryDto ) {
        final Inventory inventory = inventoryRepository.findById( 1L ).orElseThrow(
                () -> new ResourceNotFoundException( "Inventory does not exist with id of " + inventoryDto.getId() ) );

        final List<Ingredient> toAdd = inventoryDto.getIngredients();
        for ( int i = 0; i < toAdd.size(); i++ ) {
            final Ingredient current = getInventoryIngredient( toAdd.get( i ).getName() );
            if ( current != null ) {
                if ( toAdd.get( i ).getAmount() >= 0 ) {
                    current.setAmount( current.getAmount() + toAdd.get( i ).getAmount() );
                }
                else {
                    throw new IllegalArgumentException( "Ingredient amount cannot be negative." );
                }
            }
            else {
                throw new ResourceNotFoundException( "Ingredient " + toAdd.get( i ).getName() + " does not exist." );
            }
        }

        final Inventory savedInventory = inventoryRepository.save( inventory );

        return InventoryMapper.mapToInventoryDto( savedInventory );
    }

    /**
     * Returns true if the ingredient already exists in the inventory.
     *
     * @param inventoryDto
     *            current inventory
     * @param ingredientName
     *            ingredient's name to check
     * @return true if already in the inventory
     */
    @Override
    public boolean isDuplicateName ( final InventoryDto inventoryDto, final String ingredientName ) {
        final List<Ingredient> ingredients = inventoryDto.getIngredients();
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( ingredients.get( i ).getName().equals( ingredientName ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compares a valid Recipe Ingredient with its corresponding Inventory
     * Ingredient and returns whether there is enough of that Ingredient in the
     * Inventory to make the Recipe.
     *
     * @param recIngredient
     *            Recipe Ingredient to compare
     * @return boolean based on the amount comparison
     */
    @Override
    public boolean checkIngredient ( final Ingredient recIngredient ) {
        final Ingredient invIngredient = getInventoryIngredient( recIngredient.getName() );
        if ( invIngredient != null ) {
            return invIngredient.getAmount() >= recIngredient.getAmount();
        }
        else {
            throw new IllegalArgumentException( "Ingredient " + recIngredient.getName() + " does not exist." );
        }
    }

    /**
     * Helper function to determine if a list of ingredients have valid amounts.
     *
     * @param ingredients
     *            list of ingredients to check
     * @return boolean based on whether the ingredient amounts are valid
     */
    private boolean validateIngredients ( final List<Ingredient> ingredients ) {
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( ingredients.get( i ).getAmount() < 0 ) {
                return false;
            }
        }
        return true;
    }
}
