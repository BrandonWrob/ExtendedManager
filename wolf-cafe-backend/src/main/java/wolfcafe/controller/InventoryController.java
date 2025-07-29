package wolfcafe.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolfcafe.dto.InventoryDto;
import wolfcafe.entity.Ingredient;
import wolfcafe.service.InventoryService;

/**
 * Controller for CoffeeMaker's inventory. The inventory is a singleton; there's
 * only one row in the database that contains the current inventory for the
 * system.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/inventory" )
public class InventoryController {
	
	private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    /**
     * Connection to inventory service for manipulating the Inventory model.
     */
    @Autowired
    private InventoryService inventoryService;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * Inventory.
     *
     * @return response to the request
     */
    @GetMapping
    public ResponseEntity<InventoryDto> getInventory () {
        final InventoryDto inventoryDto = inventoryService.getInventory();
        return ResponseEntity.ok( inventoryDto );
    }

    /**
     * REST API endpoint to provide update access to the CoffeeMaker's singleton
     * Inventory.
     *
     * @param inventoryDto
     *            amounts to add to inventory
     * @return ResponseEntity indicating success if the inventory could be
     *         updated, or an error if it could not be
     */
    @PutMapping
    public ResponseEntity<InventoryDto> updateInventory ( @RequestBody final InventoryDto inventoryDto ) {
        InventoryDto savedInventoryDto = new InventoryDto();
        try {
            savedInventoryDto = inventoryService.updateInventory( inventoryDto );
            
        }
        catch ( final IllegalArgumentException e ) {
        	log.warn("Failed to update inventory");
            return new ResponseEntity<>( inventoryDto, HttpStatus.UNSUPPORTED_MEDIA_TYPE );
        }
        // makes log for inventory
        List<Ingredient> updatedIngredients = savedInventoryDto.getIngredients();

        StringBuilder inventoryLog = new StringBuilder();
        
        for (Ingredient ingredient : updatedIngredients) {
            inventoryLog.append("Ingredient: ")
                        .append(ingredient.getName())
                        .append(", Amount: ")
                        .append(ingredient.getAmount())
                        .append(" | ");
        }

        // remove trailing " | "
        if (inventoryLog.length() > 0) {
            inventoryLog.setLength(inventoryLog.length() - 3);
        }

        log.info("Updated Inventory is: {}", inventoryLog.toString());
        return ResponseEntity.ok( savedInventoryDto );
    }

    /**
     * REST API endpoint to provide create access to the CoffeeMaker's singleton
     * Inventory.
     *
     * @param inventoryDto
     *            amounts to initialize in the inventory
     * @return ResponseEntity indicating success if the inventory could be
     *         created, or an error if it could not be
     */
    @PostMapping
    public ResponseEntity<InventoryDto> createInventory ( @RequestBody final InventoryDto inventoryDto ) {
        InventoryDto savedInventoryDto = new InventoryDto();
        try {
            savedInventoryDto = inventoryService.createInventory( inventoryDto );
        }
        catch ( final IllegalArgumentException e ) {
        	log.warn("Failed to create new inventory");
            return new ResponseEntity<>( inventoryDto, HttpStatus.UNSUPPORTED_MEDIA_TYPE );
        }
        // makes log for inventory
        List<Ingredient> updatedIngredients = savedInventoryDto.getIngredients();

        StringBuilder inventoryLog = new StringBuilder();
        
        for (Ingredient ingredient : updatedIngredients) {
            inventoryLog.append("Ingredient: ")
                        .append(ingredient.getName())
                        .append(", Amount: ")
                        .append(ingredient.getAmount())
                        .append(" | ");
        }

        // remove trailing " | "
        if (inventoryLog.length() > 0) {
            inventoryLog.setLength(inventoryLog.length() - 3);
        }

        log.info("Updated Inventory is: {}", inventoryLog.toString());
        return ResponseEntity.ok( savedInventoryDto );
    }

}
