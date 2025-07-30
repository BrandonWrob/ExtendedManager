package wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import wolfcafe.entity.Ingredient;
import jakarta.transaction.Transactional;

/**
 * Tests IngredientRepository.
 */
@DataJpaTest
@ActiveProfiles("localtest")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class IngredientRepositoryTest {
	
    /** Reference to ingredient repository */
	@Autowired
	private IngredientRepository ingredientRepository;
	
	/** first ingredient id */
	private Long ingredient1Id;
	/** second ingredient id */
	private Long ingredient2Id;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
	@BeforeEach
	public void setUp() throws Exception {
		ingredientRepository.deleteAll();
		
		Ingredient ingredient1 = new Ingredient("coffee", 5);
		Ingredient ingredient2 = new Ingredient("pumpkin spice", 10);
		
		ingredient1Id = ingredientRepository.save(ingredient1).getId();
		ingredient2Id = ingredientRepository.save(ingredient2).getId();
	}

	/**
	 * Tests retrieving ingredients by id from the repository.
	 */
	@Test
	@Transactional
	public void testAddIngredients() {
		Ingredient i1 = ingredientRepository.findById(ingredient1Id).get();
		assertAll("Ingredient contents",
				() -> assertEquals("coffee", i1.getName()),
				() -> assertEquals(5, i1.getAmount()));
		
		Ingredient i2 = ingredientRepository.findById(ingredient2Id).get();
		assertAll("Ingredient contents",
				() -> assertEquals("pumpkin spice", i2.getName()),
				() -> assertEquals(10, i2.getAmount()));
	}

}