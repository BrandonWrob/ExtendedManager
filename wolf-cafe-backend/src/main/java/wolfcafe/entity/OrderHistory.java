package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** represents an order that a user has */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "order_history" )
public class OrderHistory {

    /** Order id */
    @Id
    @Column ( name = "id" )
    private Long    id;

    /**
     * whether or not the recipe was picked up, set to false since orderHistory
     * is made during creation of an order, not at pickup
     */
    @Column ( name = "picked_up" )
    private Boolean pickedUp = false;

    /**
     * String representation of recipes in the order and the amount Format:
     * "Black Coffee: 3, Ham Wrap: 1"
     */
    @Column ( name = "recipes_in_order" )
    private String  recipesInOrder;

    /**
     * String representation of ingredients in the order that are used and the
     * amount Format: "Coffee Beans: 14, Ham: 6, Bread: 2"
     */
    @Column ( name = "ingredients_used" )
    private String  ingredientsUsed;

    /**
     * Long integer that represents the total of an order
     */
    @Column ( name = "total" )
    private Double  total;

    /**
     * String to represent username
     */
    @Column ( name = "username" )
    private String  username;

}
