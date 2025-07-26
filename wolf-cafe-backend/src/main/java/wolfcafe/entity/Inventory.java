package wolfcafe.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is a Data Access Object (DAO) is
 * tied to the database using Hibernate libraries. InventoryRepository provides
 * the methods for database CRUD operations.
 */
@Entity
public class Inventory {

    /** id for inventory entry */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long             id;
    /** ingredients for inventory entry */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Ingredient> ingredients;

    /**
     * Empty constructor for Hibernate.
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
    }

    /**
     * Creates an Inventory with all fields
     *
     * @param id
     *            inventory's id
     * @param ingredients
     *            inventory's ingredients
     */
    public Inventory ( final Long id, final List<Ingredient> ingredients ) {
        super();
        this.id = id;
        this.ingredients = ingredients;
    }

    /**
     * Returns the ID of the entry in the DB.
     *
     * @return long id
     */
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate).
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the Ingredients of the entry in the DB.
     *
     * @return List of ingredients
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Set the Ingredients of the Inventory (Used by Hibernate).
     *
     * @param ingredients
     *            the Ingredients
     */
    public void setIngredients ( final List<Ingredient> ingredients ) {
        this.ingredients = ingredients;
    }

    /**
     * Adds the given Ingredient to the list of Ingredients.
     *
     * @param ingredient
     *            the Ingredient
     */
    public void addIngredient ( final Ingredient ingredient ) {
        ingredients.add( ingredient );
    }
}
