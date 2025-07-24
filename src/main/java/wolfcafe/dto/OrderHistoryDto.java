package edu.ncsu.csc326.wolfcafe.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** represents an order that a user has */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryDto {

    /** Order id */
    @Id
    private Long    id;

    /**
     * whether or not the recipe was picked up, set to false since orderHistory
     * is made during creation of an order, not at pickup
     */
    private Boolean pickedUp = false;

    /**
     * String representation of recipes in the order and the amount Format:
     * "Black Coffee: 3, Ham Wrap: 1"
     */
    private String  recipesInOrder;

    /**
     * String representation of ingredients in the order that are used and the
     * amount Format: "Coffee Beans: 14, Ham: 6, Bread: 2"
     */
    private String  ingredientsUsed;

    /**
     * Long integer that represents the total of an order
     */
    private Double  total;

    /**
     * String to represent username
     */
    private String  username;

}
