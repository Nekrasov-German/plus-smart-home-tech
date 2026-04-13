package ru.yandex.practicum.interaction.dto_warehouse;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ShippedToDeliveryRequest {
    private UUID orderId;
    private UUID deliveryId;
}
