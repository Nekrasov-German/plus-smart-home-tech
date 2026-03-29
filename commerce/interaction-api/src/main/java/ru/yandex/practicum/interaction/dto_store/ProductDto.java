package ru.yandex.practicum.interaction.dto_store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductDto {
    private UUID productId;
    @NotBlank
    private String productName;
    @NotBlank
    private String description;
    @NotBlank
    private String imageSrc;
    @NotNull
    private QuantityState quantityState;
    @NotNull
    private ProductState productState;
    @NotNull
    private ProductCategory productCategory;
    @NotNull
    private Double price;
}
