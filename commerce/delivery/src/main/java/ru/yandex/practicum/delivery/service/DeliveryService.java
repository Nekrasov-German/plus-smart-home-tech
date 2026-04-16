package ru.yandex.practicum.delivery.service;

import ru.yandex.practicum.interaction.dto_delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto_order.OrderDto;

import java.util.UUID;

public interface DeliveryService {

    DeliveryDto createDelivery(DeliveryDto deliveryDto);

    void completeDelivery(UUID deliveryId);

    void pickedDelivery(UUID deliveryId);

    void failedDelivery(UUID deliveryId);

    Double costDelivery(OrderDto orderDto);

}
