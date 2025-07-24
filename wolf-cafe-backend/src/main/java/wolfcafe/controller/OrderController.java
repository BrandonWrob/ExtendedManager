package edu.ncsu.csc326.wolfcafe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.MultiRecipe;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import edu.ncsu.csc326.wolfcafe.service.RecipeService;

/**
 * controller that handles API calls that deal with orders
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/orders" )
public class OrderController {

    /** reference to AuthService */
    @Autowired
    private AuthService   authService;

    /** reference to RecipeService */
    @Autowired
    private RecipeService recipeService;

    /** reference to OrderService */
    @Autowired
    private OrderService  orderService;

    /**
     * creates an order associated with the user's auth token where the given
     * orderDto is the order saved with the the user
     *
     * @param token
     *            the token in the auth header
     * @param orderDto
     *            the order to be saved
     * @return the saved order as a dto
     */
    @PostMapping
    public ResponseEntity<OrderDto> makeOrder ( @RequestHeader ( "Authorization" ) final String token,
            @RequestBody final OrderDto orderDto ) {
        final String username = authService.getUsername( token.substring( 7 ) );

        // preventing save collisions
        orderDto.setId( 0L );
        // should be false. If a person sent an api call trying to create a
        // fulfilled order, we just count it as unfulfilled.
        orderDto.setFulfilled( false );

        // checking that fields are not null, match previous, and are not 0.
        // Very extensive.

        // check for recipes being invalid
        if ( null == orderDto.getRecipes() || 0 == orderDto.getRecipes().size() ) {
            return new ResponseEntity<>( orderDto, HttpStatus.NOT_FOUND );
        }

        for ( final MultiRecipe multiRecipe : orderDto.getRecipes() ) { // we
                                                                        // need
                                                                        // to
                                                                        // check
                                                                        // that
                                                                        // every
                                                                        // multiRecipe
                                                                        // maps
                                                                        // to a
                                                                        // real
                                                                        // recipe

            RecipeDto realRecipe;
            try {
                realRecipe = recipeService.getRecipeByName( multiRecipe.getName() );
            }
            // catch recipe name doesn't exist
            catch ( final Exception e ) {
                return new ResponseEntity<>( orderDto, HttpStatus.NOT_FOUND );
            }
            multiRecipe.setId( 0L ); // preventing save collisions

            // bad means invalid, doesn't match real part, or null
            // check if amount is bad, price is bad, ingredients size is bad
            if ( null == multiRecipe.getAmount() || null == multiRecipe.getIngredients()
                    || realRecipe.getIngredients().size() != multiRecipe.getIngredients().size()
                    || !realRecipe.getPrice().equals( multiRecipe.getPrice() ) || multiRecipe.getAmount() <= 0 ) {
                return new ResponseEntity<>( orderDto, HttpStatus.NOT_FOUND );
            }

            // we are expecting that all ingredients appear as they do in the
            // recipe
            for ( int i = 0; i < multiRecipe.getIngredients().size(); i++ ) {
                final Ingredient ingredient = multiRecipe.getIngredients().get( i );
                final Ingredient realIngredient = realRecipe.getIngredients().get( i );

                // check if ingredient price or name is bad
                if ( !realIngredient.getName().equals( ingredient.getName() )
                        || !realIngredient.getAmount().equals( ingredient.getAmount() ) ) {
                    return new ResponseEntity<>( orderDto, HttpStatus.NOT_FOUND );
                }
                ingredient.setId( 0L ); // preventing save collisions
            }
        }
        try {
            // try to make the recipe
            final OrderDto savedOrderDto = orderService.makeOrder( username, orderDto );
            return new ResponseEntity<>( savedOrderDto, HttpStatus.OK );
        }
        catch ( final IllegalArgumentException e ) {
            // catch for not enough ingredients
            return new ResponseEntity<>( orderDto, HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * REST API GET endpoint for getting the list of orders
     *
     * @return a list of orders in the repository
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders () {

        // remove everything except for id and fulfilled because customers and
        // others can use this.
        final List<OrderDto> sanitizedOrderDtos = new ArrayList<OrderDto>();
        for ( final OrderDto orderDto : orderService.getOrders() ) {
            final OrderDto sanitizedOrderDto = new OrderDto();
            sanitizedOrderDto.setId( orderDto.getId() );
            sanitizedOrderDto.setFulfilled( orderDto.getFulfilled() );
            sanitizedOrderDtos.add( sanitizedOrderDto );
        }
        return new ResponseEntity<>( sanitizedOrderDtos, HttpStatus.OK );
    }

    /**
     * REST API PUT mapping with a path variable of id to updated an order to
     * fulfilled which has the id
     *
     * @param id
     *            the id of the order to be fulfilled
     * @return OrderDto the fulfilled order
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'MANAGER', 'ADMIN')" )
    @PutMapping ( "{id}" )
    public ResponseEntity<OrderDto> fulfillOrder ( @PathVariable final Long id ) {
        try {
            // try to fulfill
            return new ResponseEntity<>( orderService.fulfillOrder( id ), HttpStatus.OK );
        }
        catch ( final ResourceNotFoundException e ) {
            // order with id was not found
            return new ResponseEntity<>( null, HttpStatus.GONE );
        }
        catch ( final IllegalStateException e ) {
            // order was already fulfilled
            return new ResponseEntity<>( null, HttpStatus.CONFLICT );
        }

    }

    /**
     * REST API GET mapping with a path variable of id to get a specific order
     *
     * @param id
     *            the id of the order to be retrieved
     * @return OrderDto the order retrieved
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'MANAGER', 'ADMIN')" )
    @GetMapping ( "{id}" )
    public ResponseEntity<OrderDto> getOrder ( @PathVariable final Long id ) {
        try {
            // try to fulfill
            return new ResponseEntity<>( orderService.getOrderById( id ), HttpStatus.OK );
        }
        catch ( final ResourceNotFoundException e ) {
            // order with id was not found
            return new ResponseEntity<>( null, HttpStatus.GONE );
        }

    }

    /**
     * REST API DELETE mapping for picking up a specific order only a user can
     * pick up their own order
     *
     * @param token
     *            the token used to authenticate the user
     * @param id
     *            the id of the user
     * @return ResponseEntity(OrderDto) the order that was picked up
     */
    @PreAuthorize ( "hasAnyRole( 'CUSTOMER', 'STAFF', 'MANAGER', 'ADMIN')" )
    @DeleteMapping ( "{id}" )
    public ResponseEntity<OrderDto> pickupOrder ( @RequestHeader ( "Authorization" ) final String token,
            @PathVariable final Long id ) {

        // get the username from token
        final String username = authService.getUsername( token.substring( 7 ) );

        try {
            // try to pickup
            final OrderDto orderDto = orderService.pickupOrder( username, id );

            // pickup success
            return new ResponseEntity<>( orderDto, HttpStatus.OK );
        }
        catch ( final WolfCafeAPIException e ) {
            // pickup failed
            return new ResponseEntity<>( null, e.getStatus() );
        }
    }

    /**
     * REST API GET mapping for getting only the orders of a specific user
     *
     * @param token
     *            the token used to authenticate the user
     * @return ResponseEntity(List(OrderDto)) a list of the orders the user has
     */
    @PreAuthorize ( "hasAnyRole( 'CUSTOMER', 'STAFF', 'MANAGER', 'ADMIN')" )
    @GetMapping ( "user" )
    public ResponseEntity<List<OrderDto>> viewOrdersStatus ( @RequestHeader ( "Authorization" ) final String token ) {
        // get the username from token
        final String username = authService.getUsername( token.substring( 7 ) );

        // return the orders the customer has
        return new ResponseEntity<>( orderService.getOrdersByCustomer( username ), HttpStatus.OK );

    }
}
