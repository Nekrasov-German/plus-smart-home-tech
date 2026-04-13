package ru.yandex.practicum.interaction.dto_warehouse;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class AssemblyProductsForOrderRequest {
    private UUID orderId;
    private Map<UUID, Integer> products;
}
