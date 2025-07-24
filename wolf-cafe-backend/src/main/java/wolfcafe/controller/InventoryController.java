package edu.ncsu.csc326.wolfcafe.controller;

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

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 * Controller for CoffeeMaker's inventory. The inventory is a singleton; there's
 * only one row in the database that contains the current inventory for the
 * system.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/inventory" )
public class InventoryController {

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
            return new ResponseEntity<>( inventoryDto, HttpStatus.UNSUPPORTED_MEDIA_TYPE );
        }
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
            return new ResponseEntity<>( inventoryDto, HttpStatus.UNSUPPORTED_MEDIA_TYPE );
        }
        return ResponseEntity.ok( savedInventoryDto );
    }

}
