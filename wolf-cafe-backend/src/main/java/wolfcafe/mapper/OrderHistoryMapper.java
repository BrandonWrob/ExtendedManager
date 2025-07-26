package wolfcafe.mapper;

import wolfcafe.dto.OrderHistoryDto;
import wolfcafe.entity.OrderHistory;

/**
 * maps between an Order and OrderDto be warned: list parts are shallow copies
 */
public class OrderHistoryMapper {
    /**
     * Converts a OrderHistory entity to OrderHistoryDto.
     *
     * @param orderHistory
     *            OrderHistory to convert
     * @return OrderHistoryDto object
     */
    public static OrderHistoryDto mapToOrderHistoryDto ( final OrderHistory orderHistory ) {
        final OrderHistoryDto orderHistoryDto = new OrderHistoryDto( orderHistory.getId(), orderHistory.getPickedUp(),
                orderHistory.getRecipesInOrder(), orderHistory.getIngredientsUsed(), orderHistory.getTotal(),
                orderHistory.getUsername() );
        return orderHistoryDto;
    }

    /**
     * Converts a OrderHistoryDto object to a OrderHistory entity.
     *
     * @param orderHistoryDto
     *            OrderHistoryDto to convert
     * @return OrderHistory entity
     */
    public static OrderHistory mapToOrderHistory ( final OrderHistoryDto orderHistoryDto ) {
        final OrderHistory orderHistory = new OrderHistory( orderHistoryDto.getId(), orderHistoryDto.getPickedUp(),
                orderHistoryDto.getRecipesInOrder(), orderHistoryDto.getIngredientsUsed(), orderHistoryDto.getTotal(),
                orderHistoryDto.getUsername() );
        return orderHistory;
    }
}
