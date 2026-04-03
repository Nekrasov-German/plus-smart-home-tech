package ru.yandex.practicum.interaction.dto_warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddProductToWarehouseRequest {
    @NotNull
    private UUID productId;
    @NotNull
    private Integer quantity;
}
