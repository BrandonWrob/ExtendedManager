package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;

/**
 * interface for dealing with orders
 */
public interface OrderService {
    /**
     * makes an order, removing ingredients from inventory before the order is
     * actually made and stores an order with the user
     *
     * @param username
     *            the name of the person making the order
     * @param orderDto
     *            the order the person is trying to make
     * @return OrderDto the saved order
     */
    OrderDto makeOrder ( final String username, final OrderDto orderDto );

    /**
     * gets a list of order dtos in the system
     *
     * @return a list of order dtos in the system.
     */
    List<OrderDto> getOrders ();

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
    OrderDto fulfillOrder ( Long id );

    /**
     * gets an order with the given id
     *
     * @param id
     *            the id of the order
     * @return OrderDto order with the given id
     * @throws ResourceNotFoundException
     *             if order doesn't exist with the id
     */
    OrderDto getOrderById ( Long id );

    /**
     * picks up
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
    OrderDto pickupOrder ( String username, Long id );

    /**
     * gets all of the orders that a user currently has active
     *
     * @param username
     *            the name of the user
     * @return List(OrderDto) a list of all orders a customer has
     * @throws ResourceNotFoundException
     *             if user does not exist
     */
    List<OrderDto> getOrdersByCustomer ( String username );

}
