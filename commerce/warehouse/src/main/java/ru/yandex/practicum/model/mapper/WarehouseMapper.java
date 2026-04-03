package ru.yandex.practicum.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction.dto_warehouse.DimensionDto;
import ru.yandex.practicum.interaction.dto_warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.ProductWarehouse;

@UtilityClass
public class WarehouseMapper {
    public NewProductInWarehouseRequest toDto(ProductWarehouse product) {
        return NewProductInWarehouseRequest.builder()
                .productId(product.getProductId())
                .fragile(product.getFragile())
                .dimension(DimensionDto.builder()
                        .width(product.getDimension().getWidth())
                        .height(product.getDimension().getHeight())
                        .depth(product.getDimension().getDepth())
                        .build())
                .weight(product.getWeight())
                .build();
    }
}
