package wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolfcafe.dto.RecipeDto;
import wolfcafe.service.RecipeService;

/**
 * Controller for Recipes.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/recipes" )
public class RecipeController {

    /** Connection to RecipeService */
    @Autowired
    private RecipeService recipeService;

    /**
     * REST API method to provide GET access to all recipes in the system.
     *
     * @return JSON representation of all recipes
     */
    @GetMapping
    public List<RecipeDto> getRecipes () {
        return recipeService.getAllRecipes();
    }

    /**
     * REST API method to provide GET access to a specific recipe, as indicated
     * by the path variable provided (the name of the recipe desired).
     *
     * @param name
     *            recipe name
     * @return response to the request
     */
    @GetMapping ( "{name}" )
    public ResponseEntity<RecipeDto> getRecipe ( @PathVariable ( "name" ) final String name ) {
        final RecipeDto recipeDto = recipeService.getRecipeByName( name );
        return ResponseEntity.ok( recipeDto );
    }

    /**
     * REST API method to provide POST access to the Recipe model.
     *
     * @param recipeDto
     *            the recipe to be saved.
     * @return ResponseEntity indicating success if the recipe could be saved,
     *         or an error if it could not be
     */
    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe ( @RequestBody final RecipeDto recipeDto ) {
        if ( recipeService.isDuplicateName( recipeDto.getName() ) ) {
            return new ResponseEntity<>( recipeDto, HttpStatus.CONFLICT );
        }
        if ( recipeService.getAllRecipes().size() < 3 ) {
            RecipeDto savedRecipeDto;
            try {
                savedRecipeDto = recipeService.createRecipe( recipeDto );
            }
            catch ( final IllegalArgumentException e ) {
                return new ResponseEntity<>( recipeDto, HttpStatus.UNSUPPORTED_MEDIA_TYPE );
            }
            return ResponseEntity.ok( savedRecipeDto );
        }
        else {
            return new ResponseEntity<>( recipeDto, HttpStatus.INSUFFICIENT_STORAGE );
        }
    }

    /**
     * REST API method to allow deleting a Recipe from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the recipe to delete (as a path variable).
     *
     * @param recipeId
     *            the id of the recipe to delete
     * @return Success if the recipe could be deleted; an error if the recipe
     *         does not exist
     */
    @DeleteMapping ( "{id}" )
    public ResponseEntity<String> deleteRecipe ( @PathVariable ( "id" ) final Long recipeId ) {
        recipeService.deleteRecipe( recipeId );
        return ResponseEntity.ok( "Recipe deleted successfully." );
    }

    /**
     * REST API method to allow editing a recipe from the CoffeeMaker's
     * Inventory, by making a PUT request to the API endpoint indicating the
     * recipe to edit (as a path variable) with a new RecipeDto object in the
     * request body to represent the updated Recipe.
     *
     * @param recipeId
     *            the id of the recipe to update.
     * @param recipeDto
     *            the recipe to be saved.
     * @return ResponseEntity indicating success if the recipe could be updated
     *         with the request body, or an error if it could not be
     */
    @PutMapping ( "{id}" )
    public ResponseEntity<RecipeDto> updateRecipe ( @PathVariable ( "id" ) final Long recipeId,
            @RequestBody final RecipeDto recipeDto ) {
        final RecipeDto updatedRecipe = recipeService.updateRecipe( recipeId, recipeDto );
        return ResponseEntity.ok( updatedRecipe );
    }
}
