/**
 *
 */
package wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolfcafe.dto.IngredientDto;
import wolfcafe.service.IngredientService;

/**
 * Controller for Ingredients.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/ingredients" )
public class IngredientController {

    /**
     * Connection to ingredient service for manipulating the Ingredient model.
     */
    @Autowired
    private IngredientService ingredientService;

    /**
     * REST API method to provide POST access to the Ingredient model.
     *
     * @param ingredientDto
     *            The valid Ingredient to be saved.
     * @return ResponseEntity indicating success if the ingredient could be
     *         saved to the inventory, or an error if it could not be
     */
    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient ( @RequestBody final IngredientDto ingredientDto ) {
        if ( ingredientDto.getAmount() < 0 ) {
            return new ResponseEntity<>( HttpStatus.UNSUPPORTED_MEDIA_TYPE );
        }
        final List<IngredientDto> allIngredients = ingredientService.getAllIngredients();
        for ( final IngredientDto i : allIngredients ) {
            if ( i.getName().equals( ingredientDto.getName() ) ) {
                return new ResponseEntity<>( HttpStatus.CONFLICT );
            }
        }
        final IngredientDto savedIngredientDto = ingredientService.createIngredient( ingredientDto );
        return ResponseEntity.ok( savedIngredientDto );
    }

    /**
     * REST API method to provide GET access to a specific ingredient, as
     * indicated by the path variable provided (the name of the ingredient
     * desired).
     *
     * @param id
     *            ingredient id
     * @return response to the request
     */
    @GetMapping ( "{id}" )
    public ResponseEntity<IngredientDto> getIngredientbyId ( @PathVariable ( "id" ) final Long id ) {
        final IngredientDto ingredientDto = ingredientService.getIngredientById( id );
        return ResponseEntity.ok( ingredientDto );
    }

    /**
     * REST API method to provide GET access to all ingredients in the system.
     *
     * @return JSON representation of all ingredients
     */
    @GetMapping
    public List<IngredientDto> getIngredients () {
        return ingredientService.getAllIngredients();
    }
}
