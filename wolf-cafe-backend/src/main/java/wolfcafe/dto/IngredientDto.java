package wolfcafe.dto;

/**
 * Used to transfer Ingredient data between the client and server. This class
 * will serve as the response in the REST API.
 */
public class IngredientDto {

    /** id for ingredient entry */
    private Long    id;
    /** name for ingredient entry */
    private String  name;
    /** amount for ingredient entry */
    private Integer amount;

    /**
     * Default IngredientDto constructor.
     */
    public IngredientDto () {
        // empty constructor
    }

    /**
     * Constructs an IngredientDto object from field values.
     *
     * @param id
     *            ingredient id
     * @param name
     *            name of ingredient
     * @param amount
     *            amount of ingredient
     */
    public IngredientDto ( final Long id, final String name, final Integer amount ) {
        super();
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    /**
     * Gets the ingredient id.
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Ingredient id to set.
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the ingredient's name.
     *
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the ingredient's name.
     *
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the ingredient's amount.
     *
     * @return the amount
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * Sets the ingredient's amount.
     *
     * @param amount
     *            the amount to set
     */
    public void setAmount ( final Integer amount ) {
        this.amount = amount;
    }
}
