package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.entity.MultiRecipe;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.mapper.OrderMapper;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import lombok.AllArgsConstructor;

/**
 * implementation of OrderService interface
 */
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    /** reference to UserReporitory */
    private final UserRepository      userRepository;

    /** reference to InventoryRepository */
    private final InventoryRepository inventoryRepository;

    /** reference to OrderRepository */
    private final OrderRepository     orderRepository;

    /**
     * reference to
     *
     * /** makes an order, removing ingredients from inventory before the order
     * is actually made and stores an order with the user
     *
     * @param username
     *            the name of the person making the order
     * @param orderDto
     *            the order the person is trying to make
     * @return OrderDto the saved order
     * @throws ResourceNotFoundException
     *             if the user does not exist
     * @throws IllegalStateException
     *             if the user already has an order in
     * @throws IllegalArgumentException
     *             if there are not enough ingredients
     */
    @Override
    public OrderDto makeOrder ( final String username, final OrderDto orderDto ) {
        // get the user
        final User user = getUser( username );

        // get the inventory. Should never give an array out of bounds exception
        // in practice, because
        // creating a recipe requires at least one ingredient. in inventory
        final Inventory inventory = inventoryRepository.findAll().get( 0 );

        // initializing spent ingredients map
        final Map<String, Integer> spentIngredients = new HashMap<String, Integer>();
        for ( final Ingredient ingredient : inventory.getIngredients() ) {
            spentIngredients.put( ingredient.getName(), 0 );
        }

        // populating spent ingredients map
        for ( final MultiRecipe multiRecipe : orderDto.getRecipes() ) {
            for ( final Ingredient ingredient : multiRecipe.getIngredients() ) {
                spentIngredients.put( ingredient.getName(), ingredient.getAmount() * multiRecipe.getAmount()
                        + spentIngredients.get( ingredient.getName() ) );
            }
        }

        // testing revealed that somehow the ingredients were being edited in
        // repository before save.
        // instead of moving between dto/entity, I just did it after checking
        // every amount independently before subtraction.
        for ( final Ingredient ingredient : inventory.getIngredients() ) {
            if ( ingredient.getAmount() < spentIngredients.get( ingredient.getName() ) ) {
                throw new IllegalArgumentException( "Not enough ingredients in inventory." );
            }
        }
        for ( final Ingredient ingredient : inventory.getIngredients() ) {
            ingredient.setAmount( ingredient.getAmount() - spentIngredients.get( ingredient.getName() ) );

        }

        // save the order
        final Order order = OrderMapper.mapToOrder( orderDto );
        user.getOrders().add( order );

        final OrderDto savedOrderDto = OrderMapper.mapToOrderDto( userRepository.save( user ).getOrders().getLast() );

        // save the inventory
        inventoryRepository.save( inventory );

        return savedOrderDto;
    }

    /**
     * gets a list of order dtos in the system
     *
     * @return a list of order dtos in the system.
     */
    @Override
    public List<OrderDto> getOrders () {
        final List<Order> orders = orderRepository.findAll();
        return orders.stream().map( ( order ) -> OrderMapper.mapToOrderDto( order ) ).collect( Collectors.toList() );
    }

    /**
     * fulfills an order with the given id
     *
     * @param id
     *            the id of the order
     * @return OrderDto fulfilled order
     * @throws ResourceNotFoundException
     *             if order doesn't exist with the id
     * @throws IllegalStateException
     *             if order with id is already fulfilled
     */
    @Override
    public OrderDto fulfillOrder ( final Long id ) {
        final Order order = orderRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + id ) );
        if ( order.getFulfilled() ) {
            throw new IllegalStateException();
        }
        order.setFulfilled( true );
        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    /**
     * gets an order with the given id
     *
     * @param id
     *            the id of the order
     * @return OrderDto order with the given id
     * @throws ResourceNotFoundException
     *             if order doesn't exist with the id
     */
    @Override
    public OrderDto getOrderById ( final Long id ) {
        final Order order = orderRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + id ) );
        return OrderMapper.mapToOrderDto( order );
    }

    /**
     * Gets a user. Makes sure that the user does not have a null list of orders
     *
     * @param username
     *            the username or email of the user to get
     *
     * @return User the user with the given username with a valid list of orders
     * @throws ResourceNotFoundException
     *             if no user exists with the given username
     */
    private User getUser ( final String username ) {
        final User user = userRepository.findByUsernameOrEmail( username, username ).orElseThrow(
                () -> new ResourceNotFoundException( "User does not exist with username: " + username + "." ) );

        // since we are dealing with orders, we ant orders to be valid.
        if ( null == user.getOrders() ) {
            user.setOrders( new ArrayList<Order>() );
        }

        return user;
    }

    /**
     * picks up an order, deleting it from the database
     *
     * @param username
     *            the username of the person trying to pickup an order
     * @param id
     *            the id of the person trying to pickup the order
     * @return OrderDto the order that was picked up
     * @throws ResourceNotFoundException
     *             if user does not exist
     * @throws WolfCafeAPIException
     *             if the user does not have an order with the id (410/Gone), if
     *             the order is not fulfilled (400/bad request)
     */
    @Override
    public OrderDto pickupOrder ( final String username, final Long id ) {

        final User user = getUser( username );

        final List<Order> orders = user.getOrders();

        OrderDto foundOrder = null;
        for ( int i = 0; i < orders.size(); i++ ) {
            if ( orders.get( i ).getId().equals( id ) ) {
                if ( !orders.get( i ).getFulfilled() ) {
                    throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Order is no longer able to be fulfilled" );
                }
                foundOrder = OrderMapper.mapToOrderDto( orders.remove( i ) );
                break;
            }
        }

        if ( null == foundOrder ) {
            throw new WolfCafeAPIException( HttpStatus.GONE, "User does not have an order with an id of " + id );
        }

        userRepository.save( user );
        orderRepository.deleteById( id );
        return foundOrder;
    }

    /**
     * gets all of the orders that a user currently has active
     *
     * @param username
     *            the name of the user
     * @return List(OrderDto) a list of all orders a customer has
     * @throws ResourceNotFoundException
     *             if user does not exist
     */
    @Override
    public List<OrderDto> getOrdersByCustomer ( final String username ) {
        final User user = getUser( username );
        return user.getOrders().stream().map( ( order ) -> OrderMapper.mapToOrderDto( order ) )
                .collect( Collectors.toList() );
    }
}
