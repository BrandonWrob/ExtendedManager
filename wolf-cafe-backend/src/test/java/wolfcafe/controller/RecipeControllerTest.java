package edu.ncsu.csc326.wolfcafe.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;
import jakarta.persistence.EntityManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests RecipeController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTest {
	/** Mock MVC for testing controller */
	@Autowired
	private MockMvc mvc;
	
    /** Reference to EntityManager */
    @Autowired
    private EntityManager     entityManager;
	
	/** Reference to recipe repository*/
	@Autowired
	private RecipeRepository recipeRepository;
	
	   /** Reference to ingredient repository*/
    @Autowired
    private IngredientRepository ingredientRepository;
    
    /** Reference to IngredientService (and IngredientServiceImpl). */
    @Autowired
    private IngredientService ingredientService;
	
    /** first list of ingredients for testing */
	private List<Ingredient> ingredientsList;
	/** second list of ingredients for testing */
	private List<Ingredient> ingredientsList2;
	
	/**
	 * Sets up the test case.
	 * @throws java.lang.Exception if error
	 */
	@BeforeEach
	public void setUp() throws Exception {
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE inventory" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();
        
		recipeRepository.deleteAll();
	    ingredientRepository.deleteAll();
	    
        ingredientService.createIngredient( new IngredientDto( 1L, "coffee", 3 ) );
        ingredientService.createIngredient( new IngredientDto( 2L, "milk", 5 ) );
        ingredientService.createIngredient( new IngredientDto( 3L, "cream", 4 ) );
        ingredientService.createIngredient( new IngredientDto( 4L, "sugar", 6 ) );
        ingredientService.createIngredient( new IngredientDto( 5L, "pumpkin spice", 8 ) );
        ingredientService.createIngredient( new IngredientDto( 6L, "vanilla", 10 ) );
		
        ingredientsList = new ArrayList<Ingredient>();
        ingredientsList.add( new Ingredient( "coffee", 3 ) );
        ingredientsList.add( new Ingredient( "milk", 5 ) );
        ingredientsList.add( new Ingredient( "cream", 4 ) );

        ingredientsList2 = new ArrayList<Ingredient>();
        ingredientsList2.add( new Ingredient( "sugar", 6 ) );
        ingredientsList2.add( new Ingredient( "pumpkin spice", 8 ) );
        ingredientsList2.add( new Ingredient( "vanilla", 10 ) );
	}
	
    /**
     * Tests the GET /api/recipes endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
	public void testGetRecipes() throws Exception {
		String recipe = mvc.perform(get("/api/recipes"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		assertFalse(recipe.contains("Mocha"));
	}
	
    /**
     * Tests the GET /api/recipes/{name} endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
	public void testGetRecipeByName() throws Exception {
		//Create recipe
		RecipeDto recipeDto = new RecipeDto(0L, "Mocha", 200, ingredientsList);
		
		mvc.perform(post("/api/recipes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(recipeDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Mocha"))
				.andExpect(jsonPath("$.price").value("200"))
		        .andExpect(jsonPath("$.ingredients").isArray())
		        .andExpect(jsonPath("$.ingredients.length()").value(ingredientsList.size()))
		        .andExpect(jsonPath("$.ingredients[0].name").value(ingredientsList.get(0).getName()))
		        .andExpect(jsonPath("$.ingredients[0].amount").value(ingredientsList.get(0).getAmount()))
		        .andExpect(jsonPath("$.ingredients[1].name").value(ingredientsList.get(1).getName()))
		        .andExpect(jsonPath("$.ingredients[1].amount").value(ingredientsList.get(1).getAmount()))
		        .andExpect(jsonPath("$.ingredients[2].name").value(ingredientsList.get(2).getName()))
		        .andExpect(jsonPath("$.ingredients[2].amount").value(ingredientsList.get(2).getAmount()));
		
		//Get recipe
	    String recipeName = "Mocha";
	    mvc.perform(get("/api/recipes/" + recipeName))
	            .andDo(print())
	            .andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Mocha"))
				.andExpect(jsonPath("$.price").value("200"))
		        .andExpect(jsonPath("$.ingredients").isArray())
		        .andExpect(jsonPath("$.ingredients.length()").value(ingredientsList.size()))
		        .andExpect(jsonPath("$.ingredients[0].name").value(ingredientsList.get(0).getName()))
		        .andExpect(jsonPath("$.ingredients[0].amount").value(ingredientsList.get(0).getAmount()))
		        .andExpect(jsonPath("$.ingredients[1].name").value(ingredientsList.get(1).getName()))
		        .andExpect(jsonPath("$.ingredients[1].amount").value(ingredientsList.get(1).getAmount()))
		        .andExpect(jsonPath("$.ingredients[2].name").value(ingredientsList.get(2).getName()))
		        .andExpect(jsonPath("$.ingredients[2].amount").value(ingredientsList.get(2).getAmount()));
	}
	
    /**
     * Tests the POST /api/recipes endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
	public void testCreateRecipe() throws Exception {
		RecipeDto recipeDto = new RecipeDto(0L, "Mocha", 200, ingredientsList2);
		
		mvc.perform(post("/api/recipes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(recipeDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Mocha"))
				.andExpect(jsonPath("$.price").value("200"))
		        .andExpect(jsonPath("$.ingredients").isArray())
		        .andExpect(jsonPath("$.ingredients.length()").value(ingredientsList2.size()))
		        .andExpect(jsonPath("$.ingredients[0].name").value(ingredientsList2.get(0).getName()))
		        .andExpect(jsonPath("$.ingredients[0].amount").value(ingredientsList2.get(0).getAmount()))
		        .andExpect(jsonPath("$.ingredients[1].name").value(ingredientsList2.get(1).getName()))
		        .andExpect(jsonPath("$.ingredients[1].amount").value(ingredientsList2.get(1).getAmount()))
		        .andExpect(jsonPath("$.ingredients[2].name").value(ingredientsList2.get(2).getName()))
		        .andExpect(jsonPath("$.ingredients[2].amount").value(ingredientsList2.get(2).getAmount()));
		
		// duplicate
		mvc.perform(post("/api/recipes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(recipeDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.name").value("Mocha"))
				.andExpect(jsonPath("$.price").value("200"))
		        .andExpect(jsonPath("$.ingredients").isArray())
		        .andExpect(jsonPath("$.ingredients.length()").value(ingredientsList2.size()))
		        .andExpect(jsonPath("$.ingredients[0].name").value(ingredientsList2.get(0).getName()))
		        .andExpect(jsonPath("$.ingredients[0].amount").value(ingredientsList2.get(0).getAmount()))
		        .andExpect(jsonPath("$.ingredients[1].name").value(ingredientsList2.get(1).getName()))
		        .andExpect(jsonPath("$.ingredients[1].amount").value(ingredientsList2.get(1).getAmount()))
		        .andExpect(jsonPath("$.ingredients[2].name").value(ingredientsList2.get(2).getName()))
		        .andExpect(jsonPath("$.ingredients[2].amount").value(ingredientsList2.get(2).getAmount()));
		
	      RecipeDto recipeDto2 = new RecipeDto(1L, "Chai", -10, ingredientsList);
	      
	      // negative price
	      mvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(recipeDto2))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.name").value("Chai"))
                .andExpect(jsonPath("$.price").value("-10"))
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.ingredients.length()").value(ingredientsList.size()))
                .andExpect(jsonPath("$.ingredients[0].name").value(ingredientsList.get(0).getName()))
                .andExpect(jsonPath("$.ingredients[0].amount").value(ingredientsList.get(0).getAmount()))
                .andExpect(jsonPath("$.ingredients[1].name").value(ingredientsList.get(1).getName()))
                .andExpect(jsonPath("$.ingredients[1].amount").value(ingredientsList.get(1).getAmount()))
                .andExpect(jsonPath("$.ingredients[2].name").value(ingredientsList.get(2).getName()))
                .andExpect(jsonPath("$.ingredients[2].amount").value(ingredientsList.get(2).getAmount()));
	      
	      RecipeDto recipeDto3 = new RecipeDto(1L, "Chai", 100, ingredientsList);
	      
          // add second
          mvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(recipeDto3))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chai"))
                .andExpect(jsonPath("$.price").value("100"))
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.ingredients.length()").value(ingredientsList.size()));
          
        RecipeDto recipeDto4 = new RecipeDto(2L, "Coffee", 50, ingredientsList);
          
        // add third
        mvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(recipeDto4))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Coffee"))
                .andExpect(jsonPath("$.price").value("50"))
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.ingredients.length()").value(ingredientsList.size()));
        
        RecipeDto recipeDto5 = new RecipeDto(3L, "Water", 50, ingredientsList2);
        
        // attempt to add fourth
        mvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(recipeDto5))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInsufficientStorage())
                .andExpect(jsonPath("$.name").value("Water"))
                .andExpect(jsonPath("$.price").value("50"))
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.ingredients.length()").value(ingredientsList2.size()));
	}
	
    /**
     * Tests the DELETE /api/recipes/{id} endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
	public void testDeleteRecipe() throws Exception {
		RecipeDto recipeDto = new RecipeDto(0L, "Mocha", 200, ingredientsList);
		
		mvc.perform(post("/api/recipes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(recipeDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Mocha"))
				.andExpect(jsonPath("$.price").value("200"));

		//Get recipe
	    String recipeName = "Mocha";
	    String recipe = mvc.perform(get("/api/recipes/" + recipeName))
	            .andDo(print())
	            .andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();
	    
	    // Perform assertions on the returned recipeDto
	    assertTrue(recipe.contains("Mocha"));
	    
	    // Use Gson to parse the JSON response
	    JsonObject jsonObject = JsonParser.parseString(recipe).getAsJsonObject();
	    Long createdRecipeId = jsonObject.get("id").getAsLong();
	    // Perform the delete request
	    mvc.perform(delete("/api/recipes/" + createdRecipeId))
	            .andDo(print())
	            .andExpect(status().isOk())
	            .andExpect(content().string("Recipe deleted successfully."));

	    // Optional: Perform a GET request to ensure the recipe has been deleted
	    mvc.perform(get("/api/recipes/" +  createdRecipeId))
	            .andDo(print())
	            .andExpect(status().isNotFound());
	}
	
    /**
     * Tests the PUT /api/recipes/{id} endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
	public void testUpdateRecipe() throws Exception {
	    
        ingredientService.createIngredient( new IngredientDto( 7L, "icecream", 10 ) );
        ingredientsList.add( new Ingredient( "icecream", 3 ) );
	    
       RecipeDto recipeDto = new RecipeDto(0L, "Mocha", 200, ingredientsList);
        
       // post recipe
       mvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(recipeDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mocha"))
                .andExpect(jsonPath("$.price").value("200"));
       
       assertEquals(11, ingredientService.getAllIngredients().size());
                
        // get recipe
        String recipeName = "Mocha";
        String recipe = mvc.perform(get("/api/recipes/" + recipeName))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        // Perform assertions on the returned recipeDto
        assertTrue(recipe.contains("Mocha"));
        
        // Use Gson to parse the JSON response
        JsonObject jsonObject = JsonParser.parseString(recipe).getAsJsonObject();
        Long createdRecipeId = jsonObject.get("id").getAsLong();
        
        RecipeDto updatedRecipeDto = new RecipeDto(createdRecipeId, "Mocha", 100, ingredientsList2);
        
        // update recipe
        mvc.perform(put("/api/recipes/" + createdRecipeId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(TestUtils.asJsonString(updatedRecipeDto))
              .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.name").value("Mocha"))
              .andExpect(jsonPath("$.price").value("100"))
              .andExpect(jsonPath("$.ingredients").isArray())
              .andExpect(jsonPath("$.ingredients.length()").value(ingredientsList2.size()))
              .andExpect(jsonPath("$.ingredients[0].name").value(ingredientsList2.get(0).getName()))
              .andExpect(jsonPath("$.ingredients[0].amount").value(ingredientsList2.get(0).getAmount()))
              .andExpect(jsonPath("$.ingredients[1].name").value(ingredientsList2.get(1).getName()))
              .andExpect(jsonPath("$.ingredients[1].amount").value(ingredientsList2.get(1).getAmount()))
              .andExpect(jsonPath("$.ingredients[2].name").value(ingredientsList2.get(2).getName()))
              .andExpect(jsonPath("$.ingredients[2].amount").value(ingredientsList2.get(2).getAmount()));
        
        assertEquals(10, ingredientService.getAllIngredients().size());
	}
}
