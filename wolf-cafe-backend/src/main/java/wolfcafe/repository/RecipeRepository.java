package edu.ncsu.csc326.wolfcafe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Recipe;

/**
 * RecipeRepository for working with the DB through the 
 * JpaRepository.
 */
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
	
	/**
     * Finds a Recipe object with the provided name. Spring will generate code
     * to make this happen.  Optional let's us call .orElseThrow() when a client
     * works with the method and the value isn't found in the database.
     * 
     * @param name
     *            Name of the recipe
     * @return Found recipe, null if none.
     */
    Optional<Recipe> findByName(String name);

}
