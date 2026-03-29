package ru.yandex.practicum.interaction.dto_warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NewProductInWarehouseRequest {
    private UUID productId;
    @NotNull
    private Boolean fragile;
    @NotNull
    private DimensionDto dimension;
    @NotNull
    private Double weight;
}
