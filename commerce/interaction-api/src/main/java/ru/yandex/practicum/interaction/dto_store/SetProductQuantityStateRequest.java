package ru.yandex.practicum.interaction.dto_store;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SetProductQuantityStateRequest {
    private UUID productId;
    private QuantityState quantityState;
}
