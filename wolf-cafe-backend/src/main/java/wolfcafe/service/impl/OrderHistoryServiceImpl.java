package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderHistoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.MultiRecipe;
import edu.ncsu.csc326.wolfcafe.entity.OrderHistory;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.mapper.OrderHistoryMapper;
import edu.ncsu.csc326.wolfcafe.repository.OrderHistoryRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.OrderHistoryService;
import edu.ncsu.csc326.wolfcafe.service.TaxService;
import lombok.AllArgsConstructor;

/**
 * implementation of OrderHistoryService interface
 */
@Service
@AllArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {

    /** Reference to TaxService */
    @Autowired
    private final TaxService             taxService;

    /** reference to OrderHistoryRepository */
    @Autowired
    private final OrderHistoryRepository orderHistoryRepository;

    /** reference to OrderHistoryRepository */
    @Autowired
    private final UserRepository         userRepository;

    /**
     * Helper method to calculate total for an order
     *
     * @param recipes
     *            the recipes in order
     * @return total cost of an order
     */
    double calcTotal ( final List<MultiRecipe> recipes ) {
        Double total = (double) 0; // stores the total
        // iterates through all recipes in an order
        for ( final MultiRecipe recipe : recipes ) {
            // stores amount of the recipe in the order
            final int amount = recipe.getAmount();
            // adds recipe total * amount to the current total
            total = total + recipe.getPrice() * amount;
        }
        // gets tax for order and adds it to the total
        final Double tax = taxService.calcTax( total );
        total = total + tax;
        return total; // returns total cost with tax included
    }

    /**
     * Helper method to get recipes in order and return a string of it
     *
     * @param recipes
     *            the recipes in the order
     * @return string representation of recipes in the order
     */
    String recipesInOrder ( final List<MultiRecipe> recipes ) {
        final StringBuilder recipesInTheOrder = new StringBuilder();
        // iterates through all recipes in an order
        for ( final MultiRecipe recipe : recipes ) {
            // stores amount of the recipe in the order
            final int amount = recipe.getAmount();
            // gets the name
            final String name = recipe.getName();
            // appends the new information
            recipesInTheOrder.append( name ).append( ": " ).append( amount ).append( ", " );
        }
        // removes trailing comma and space (unless order was empty)
        if ( recipesInTheOrder.length() > 0 ) {
            recipesInTheOrder.setLength( recipesInTheOrder.length() - 2 );
        }
        // return it as a string
        return recipesInTheOrder.toString();
    }

    /**
     * Helper method to format strings to get ingredients of recipes in order
     * and return a string representation of it
     *
     * @param recipes
     *            the recipes in the order
     * @return a string representation of ingredients of recipes in the order
     */
    String ingredientsInOrder ( final List<MultiRecipe> recipes ) {
        // uses a map to allow for pair value, and a tree to keep alphabetic
        // order, this way we can have consistent results for testing
        final Map<String, Integer> ingredientAmounts = new TreeMap<>();
        // iterates through all recipes
        for ( final MultiRecipe recipe : recipes ) {
            final int amount = recipe.getAmount();
            final List<Ingredient> ingredients = recipe.getIngredients();
            // iterates through each ingredient in the recipe
            for ( final Ingredient ingredient : ingredients ) {
                final String ingredientName = ingredient.getName();
                final Integer ingredientAmount = ingredient.getAmount();
                final Integer totalAmount = ingredientAmount * amount;
                // adds the ingredient's total amount to the map
                ingredientAmounts.merge( ingredientName, totalAmount, Integer::sum );
            }
        }

        // creates a string builder to turn it into a string
        final StringBuilder result = new StringBuilder();
        for ( final Map.Entry<String, Integer> entry : ingredientAmounts.entrySet() ) {
            if ( result.length() > 0 ) {
                result.append( ", " );
            }
            result.append( entry.getKey() ).append( ":" ).append( entry.getValue() );
        }
        return result.toString();
    }

    /**
     * Method to make an Order History Dto
     *
     * @param orderDto
     *            takes a input of an orderDto it wants to add to history
     * @return the OrderHistoryDto for the input order dto or null if invalid
     *         user
     */
    @Override
    public OrderHistoryDto makeOrderHistory ( final String usernameOrEmail, final OrderDto orderDto ) {
        // gets id of order
        final Long id = orderDto.getId();
        // gets recipes of order it is making a history object for
        final List<MultiRecipe> recipes = orderDto.getRecipes();
        // uses the recipe list and helper methods to get necessary fields
        // including:
        // total, string representation of recipes and ingredients.
        final Double total = calcTotal( recipes );
        final String recipesUsedInOrder = recipesInOrder( recipes );
        final String ingredientsUsedInOrder = ingredientsInOrder( recipes );
        final Optional<User> user = userRepository.findByUsernameOrEmail( usernameOrEmail, usernameOrEmail );
        if ( user.isPresent() ) {
            final User validUser = user.get();
            // makes a orderHistory entity and saves it
            final OrderHistory orderHistory = new OrderHistory( id, false, recipesUsedInOrder, ingredientsUsedInOrder,
                    total, validUser.getUsername() );
            orderHistoryRepository.save( orderHistory );
            // turns it into a Dto and returns it
            final OrderHistoryDto orderHistoryDto = OrderHistoryMapper.mapToOrderHistoryDto( orderHistory );
            return orderHistoryDto;
        }
        else {
            // returns null if it fails, but since it uses info of a logged in
            // user this can't occur (unless through an api call)
            return null;
        }
    }

    /**
     * Method to update a order status so that it is recognizable as picked up
     *
     * @param id
     *            represents the id of the order
     * @return false if not found, true if found and updated
     */
    @Override
    public boolean updateOrderHistoryStatus ( final Long id ) {
        // tries to find the order history using given id
        final OrderHistory orderHistoryBeingUpdated = orderHistoryRepository.findById( id ).orElse( null );
        // returns false if not found
        if ( orderHistoryBeingUpdated == null ) {
            return false;
        }

        // if found then it sets picked up to true
        orderHistoryBeingUpdated.setPickedUp( true );

        // saves the updated order and returns true
        orderHistoryRepository.save( orderHistoryBeingUpdated );
        return true;
    }

    /**
     * Method that gets the history of all orders. Since we only considered
     * picked-uped orders as apart of history this only returns orders that meet
     * that requirement
     *
     * @return the history of all picked up orders
     */
    @Override
    public List<OrderHistory> getOrderHistory () {
        // gets all the order histories
        final List<OrderHistory> allOrderHistory = orderHistoryRepository.findAll();
        // makes a list to store the picked up ones
        final List<OrderHistory> pickedUpOrders = new ArrayList<OrderHistory>();
        // iterates through all order histories and adds the ones that have been
        // picked up to the list
        for ( final OrderHistory orderHistory : allOrderHistory ) {
            if ( orderHistory.getPickedUp() ) {
                pickedUpOrders.add( orderHistory );
            }
        }
        // returns the list of picked up ones
        return pickedUpOrders;
    }

    /**
     * Method that gets the history of an order by an id
     *
     * @param id
     *            the id of the order it is getting
     *
     * @return the history of all picked up orders
     */
    @Override
    public OrderHistory getHistoryById ( final Long id ) {
        return orderHistoryRepository.findById( id ).orElse( null );
    }

    /**
     * Method that gets the history of all orders for a specified user. Since we
     * only considered picked-uped orders as apart of history this only returns
     * orders that meet that requirement
     *
     * @param username
     *            the username of user we want the orders of
     * @return the history of all picked up orders
     */
    @Override
    public List<OrderHistory> getUserHistory ( final String username ) {
        // gets all the order histories
        final List<OrderHistory> allOrderHistory = orderHistoryRepository.findAll();
        // makes a list to store the picked up ones that belong to the specified
        // user
        final List<OrderHistory> pickedUpOrders = new ArrayList<OrderHistory>();
        // iterates through all order histories and adds the ones that have been
        // picked up and belong to the specified user to the list
        for ( final OrderHistory orderHistory : allOrderHistory ) {
            if ( orderHistory.getPickedUp() && orderHistory.getUsername().equals( username )) {
                pickedUpOrders.add( orderHistory );
            }
        }
        // returns the list of picked up ones that belong to the specified user
        return pickedUpOrders;
    }

}
