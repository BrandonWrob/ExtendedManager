package wolfcafe.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Recipe for the coffee maker. Recipe is a Data Access Object (DAO) is tied to
 * the database using Hibernate libraries. RecipeRepository provides the methods
 * for database CRUD operations.
 */
@Entity
@Table ( name = "recipes" )
public class Recipe {

    /** Recipe id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long             id;

    /** Recipe name */
    private String           name;

    /** Recipe price */
    private Integer          price;

    /** Recipe ingredients */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Ingredient> ingredients;

    /**
     * Empty constructor for Hibernate.
     */
    public Recipe () {
        // empty constructor
    }

    /**
     * Creates a recipe from all the fields.
     *
     * @param id
     *            id of recipe
     * @param name
     *            name of recipe
     * @param price
     *            price of recipe
     * @param ingredients
     *            ingredients in recipe
     */
    public Recipe ( final Long id, final String name, final Integer price, final List<Ingredient> ingredients ) {
        super();
        this.id = id;
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }

    /**
     * Get the ID of the Recipe.
     *
     * @return the ID
     */
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate).
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * Sets the recipe ingredients.
     *
     * @param ingredients
     *            the ingredients to set
     */
    public void setIngredients ( final List<Ingredient> ingredients ) {
        this.ingredients = ingredients;
    }

    /**
     * Returns the ingredients of the recipe.
     *
     * @return Returns the ingredients
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }
}
