package wolfcafe.mapper;

import wolfcafe.dto.OrderDto;
import wolfcafe.entity.Order;

/**
 * maps between an Order and OrderDto be warned: list parts are shallow copies
 */
public class OrderMapper {
    /**
     * Converts a Order entity to OrderDto.
     *
     * @param order
     *            Order to convert
     * @return OrderDto object
     */
    public static OrderDto mapToOrderDto ( final Order order ) {
        final OrderDto orderDto = new OrderDto( order.getId(), order.getFulfilled(), order.getRecipes() );
        return orderDto;
    }

    /**
     * Converts a OrderDto object to a Order entity.
     *
     * @param orderDto
     *            OrderDto to convert
     * @return Order entity
     */
    public static Order mapToOrder ( final OrderDto orderDto ) {
        final Order order = new Order( orderDto.getId(), orderDto.getFulfilled(), orderDto.getRecipes() );
        return order;
    }
}
