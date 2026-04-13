package ru.yandex.practicum.interaction.dto_order;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class OrderDto {
    private UUID orderId;
    private UUID shoppingCardId;
    private Map<UUID, Integer> products;
    private UUID paymentId;
    private UUID deliveryId;
    private State state;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private Double totalPrice;
    private Double deliveryPrice;
    private Double productPrice;
}
