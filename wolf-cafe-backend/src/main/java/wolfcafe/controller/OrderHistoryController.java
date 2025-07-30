package wolfcafe.controller;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolfcafe.dto.OrderDto;
import wolfcafe.dto.OrderHistoryDto;
import wolfcafe.dto.RecipeDto;
import wolfcafe.entity.Ingredient;
import wolfcafe.entity.MultiRecipe;
import wolfcafe.entity.OrderHistory;
import wolfcafe.entity.User;
import wolfcafe.repository.UserRepository;
import wolfcafe.service.AuthService;
import wolfcafe.service.OrderHistoryService;
import wolfcafe.service.RecipeService;

/**
 * controller that handles API calls that deal with orders
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/orders/history" )
public class OrderHistoryController {
	
	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    /** reference to AuthService */
    @Autowired
    private AuthService         authService;

    /** reference to RecipeService */
    @Autowired
    private RecipeService       recipeService;

    /** reference to OrderHistoryService */
    @Autowired
    private OrderHistoryService orderHistoryService;

    /** reference to the User Repository */
    @Autowired
    private UserRepository      userRepository;

    /**
     * creates an order associated with the user's auth token where the given
     * orderDto is the order saved with the the user
     *
     * @param token
     *            the token in the auth header (how they get the username)
     * @param orderDto
     *            the order to be saved
     * @return the saved order history as a dto if successful, or the order dto
     *         and error if order itself is invalid
     */
    @PostMapping
    public ResponseEntity<?> makeOrderHistory ( @RequestHeader ( "Authorization" ) final String token,
            @RequestBody final OrderDto orderDto ) {
        final String usernameOrEmail = authService.getUsername( token.substring( 7 ) );

        // confirms the order history is valid, even though in the system this
        // is called after making the
        // order where it should always be valid, we take precaution in case a
        // malicious user tries to
        // input a invalid one using the API call
        orderDto.setFulfilled( false );
        if ( null == orderDto.getRecipes() || 0 == orderDto.getRecipes().size() ) {
        	log.error("Order creation failed: No recipes provided by user [{}]", usernameOrEmail);
            return new ResponseEntity<>( orderDto, HttpStatus.NOT_FOUND );
        }

        for ( final MultiRecipe multiRecipe : orderDto.getRecipes() ) {
        	if (multiRecipe == null) {
                log.error("Order creation failed: Null recipe provided by user [{}]", usernameOrEmail);
                return new ResponseEntity<>(orderDto, HttpStatus.NOT_FOUND);
            }
            RecipeDto realRecipe;
            try {
                realRecipe = recipeService.getRecipeByName( multiRecipe.getName() );
            }
            // catch recipe name doesn't exist
            catch ( final Exception e ) {
            	log.error("Order creation failed: Recipe [{}] not found for user [{}]", multiRecipe.getName(), usernameOrEmail);
                return new ResponseEntity<>( orderDto, HttpStatus.NOT_FOUND );
            }
            multiRecipe.setId( 0L ); // preventing save collisions

            // bad means invalid, doesn't match real part, or null
            // check if amount is bad, price is bad, ingredients size is bad
            if ( null == multiRecipe.getAmount() || null == multiRecipe.getIngredients()
                    || realRecipe.getIngredients().size() != multiRecipe.getIngredients().size()
                    || !realRecipe.getPrice().equals( multiRecipe.getPrice() ) || multiRecipe.getAmount() <= 0 ) {
            	log.error("Order creation failed: Invalid recipe [{}] by user [{}]", multiRecipe.getName(), usernameOrEmail);
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
                	 log.error("Order creation failed: Ingredient mismatch in recipe [{}] for user [{}]", multiRecipe.getName(), usernameOrEmail);
                    return new ResponseEntity<>( orderDto, HttpStatus.NOT_FOUND );
                }
                ingredient.setId( 0L ); // preventing save collisions
            }
        }
        try {
            // try to make the order history
            final OrderHistoryDto savedOrderHistoryDto = orderHistoryService.makeOrderHistory( usernameOrEmail,
                    orderDto );
            // checks for null
            if ( savedOrderHistoryDto == null ) {
            	log.error("Order creation failed: Returned OrderHistoryDto is null for user [{}]", usernameOrEmail);
                return new ResponseEntity<>( "Order history could not be created. Please check your input.",
                        HttpStatus.BAD_REQUEST );
            }
            log.info("Order history created successfully: id={}, pickedUp={}, username={}, total={}",
                    savedOrderHistoryDto.getId(),
                    savedOrderHistoryDto.getPickedUp(),
                    savedOrderHistoryDto.getUsername(),
                    savedOrderHistoryDto.getTotal());
            return new ResponseEntity<>( savedOrderHistoryDto, HttpStatus.OK );
        }
        catch ( final IllegalArgumentException e ) {
            // catch if unable to make the history dto (invalid orderDto or
            // invalid username)
        	log.error("Order creation failed: IllegalArgumentException for user [{}]: {}", usernameOrEmail, e.getMessage());
            return new ResponseEntity<>( orderDto, HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * updates an order status to picked up when user picks it up
     *
     * @param id
     *            the id of order being picked up
     * @param token
     *            string representation of logged in users token
     * @return true and success if status changed, else if it fails to find
     *         order then bad request and false
     */
    @PutMapping ( "/status/{id}" )
    public ResponseEntity<?> updateOrderHistoryStatus ( @PathVariable ( "id" ) final Long id,
            @RequestHeader ( "Authorization" ) final String token ) {
        final boolean updated = orderHistoryService.updateOrderHistoryStatus( id );
        if ( updated ) {
        	log.info("OrderHistory: Order status updated to picked up: orderId={}", id);
            return new ResponseEntity<>( updated, HttpStatus.OK );
        }
        else {
        	log.error("Failed to update order status: orderId={} may not exist or is already picked up", id);
            return new ResponseEntity<>( updated, HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * gets the history of all orders that are picked up
     *
     * @return list of all picked up order history
     */
    @GetMapping
    public ResponseEntity<List<OrderHistory>> getOrderHistory () {
        // no error since there are allowed to be 0 errors!
        return new ResponseEntity<>( orderHistoryService.getOrderHistory(), HttpStatus.OK );
    }

    /**
     * gets the history of an order by its id
     *
     * @param id
     *            represents the id of the user its getting the history of
     * @param token
     *            string representation of logged in users token
     * @return list of all picked up order history
     */
    @GetMapping ( "/user/{id}" )
    public ResponseEntity<?> getHistoryById ( @PathVariable ( "id" ) final Long id,
            @RequestHeader ( "Authorization" ) final String token ) {
        final OrderHistory orderHistory = orderHistoryService.getHistoryById( id );
        if ( orderHistory == null ) {
            // handles order not found
            return new ResponseEntity<>( id, HttpStatus.BAD_REQUEST );
        }
        else {
            // handles order found
            return new ResponseEntity<>( orderHistory, HttpStatus.OK );
        }

    }

    /**
     * Gets the history of all orders that are picked up and belong to a
     * specified user.
     *
     * @param usernameOrEmail
     *            represents logged in users email or username
     * @param token
     *            the authorization token from the request header
     * @return list of all picked up order history belonging to a specified user
     */
    @GetMapping ( "/{username}" )
    public ResponseEntity<?> getUserHistory ( @PathVariable ( "username" ) final String usernameOrEmail,
            @RequestHeader ( "Authorization" ) final String token ) {

        // fetch the user by username
        final Optional<User> user = userRepository.findByUsernameOrEmail( usernameOrEmail, usernameOrEmail );

        if ( user.isPresent() ) {
            // return their order history if the user exists (users can have 0
            // orders)
            final User userValid = user.get();
            final String username = userValid.getUsername();
            return new ResponseEntity<>( orderHistoryService.getUserHistory( username ), HttpStatus.OK );
        }
        else {
            // return BAD_REQUEST if the user does not exist
            return new ResponseEntity<>( "User not found: " + usernameOrEmail, HttpStatus.BAD_REQUEST );
        }
    }
}
