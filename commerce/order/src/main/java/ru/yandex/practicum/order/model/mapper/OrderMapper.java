package ru.yandex.practicum.order.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction.dto_order.OrderDto;
import ru.yandex.practicum.interaction.dto_order.State;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.model.StateOrder;

@UtilityClass
public class OrderMapper {
    public OrderDto orderToOrderDto(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .paymentId(order.getPaymentId())
                .deliveryId(order.getDeliveryId())
                .products(order.getProducts())
                .deliveryVolume(order.getDeliveryVolume())
                .deliveryWeight(order.getDeliveryWeight())
                .fragile(order.getFragile())
                .deliveryPrice(order.getDeliveryPrice())
                .productPrice(order.getProductPrice())
                .shoppingCardId(order.getShoppingCardId())
                .state(State.valueOf(order.getState().toString()))
                .totalPrice(order.getTotalPrice())
                .build();
    }

    public Order orderDtoToOrder(OrderDto order) {
        return Order.builder()
                .orderId(order.getOrderId())
                .paymentId(order.getPaymentId())
                .deliveryId(order.getDeliveryId())
                .products(order.getProducts())
                .deliveryVolume(order.getDeliveryVolume())
                .deliveryWeight(order.getDeliveryWeight())
                .fragile(order.getFragile())
                .deliveryPrice(order.getDeliveryPrice())
                .productPrice(order.getProductPrice())
                .shoppingCardId(order.getShoppingCardId())
                .state(StateOrder.valueOf(order.getState().toString()))
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
