package wolfcafe.dto;

import java.util.List;

import wolfcafe.entity.Ingredient;

/**
 * Used to transfer Inventory data between the client and server. This class
 * will serve as the response in the REST API.
 */
public class InventoryDto {

    /** id for inventory entry */
    private Long             id;
    /** list of ingredients for inventory entry */
    private List<Ingredient> ingredients;

    /**
     * Default InventoryDto constructor.
     */
    public InventoryDto () {
        // empty constructor
    }

    /**
     * Constructs an InventoryDto object from field values.
     *
     * @param id
     *            inventory id
     * @param ingredients
     *            all ingredients in the inventory
     */
    public InventoryDto ( final Long id, final List<Ingredient> ingredients ) {
        super();
        this.id = id;
        this.ingredients = ingredients;
    }

    /**
     * Gets the inventory id.
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Inventory id to set.
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the Ingredients of the entry in the DB.
     *
     * @return list of ingredients
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
}
