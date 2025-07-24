/**
 *
 */
package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Ingredient for the coffee maker. Ingredient is a Data Access Object (DAO) is
 * tied to the database using Hibernate libraries. IngredientRepository provides
 * the methods for database CRUD operations.
 */
@Entity
public class Ingredient {

    /** id for ingredient entry */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long    id;
    /** name for ingredient entry */
    private String  name;
    /** amount for ingredient entry */
    private Integer amount;

    /**
     * Empty constructor for Hibernate.
     */
    public Ingredient () {
        // empty constructor
    }

    /**
     * Creates an Ingredient with all fields.
     *
     * @param name
     *            name of ingredient
     * @param amount
     *            amount of ingredient
     */
    public Ingredient ( final String name, final Integer amount ) {
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
