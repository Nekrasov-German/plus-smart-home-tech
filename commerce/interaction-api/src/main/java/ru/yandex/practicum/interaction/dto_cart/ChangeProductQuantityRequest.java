package ru.yandex.practicum.interaction.dto_cart;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChangeProductQuantityRequest {
    private UUID productId;
    @NotNull
    private Integer newQuantity;
}
