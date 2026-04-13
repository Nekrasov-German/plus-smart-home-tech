package ru.yandex.practicum.interaction.dto_order;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ProductReturnRequest {
    private UUID orderId;
    private Map<UUID, Integer> products;
}
