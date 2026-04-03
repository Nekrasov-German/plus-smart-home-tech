package ru.yandex.practicum.service;

import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto_warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto_warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto_warehouse.NewProductInWarehouseRequest;

import java.util.UUID;

public interface WarehouseService {
    //Добавить новый товар на склад
    void addProduct(NewProductInWarehouseRequest request);

    //Предварительно проверить что количество товаров на складе достаточно для данной корзиный продуктов
    BookedProductsDto checkedQuantity(ShoppingCartDto shoppingCartDto);

    //Принять товар на склад.
    void addProductToWarehouse(UUID productId, Integer quantityToAdd);

    //Предоставить адрес склада для расчёта доставки
    AddressDto getAddress();
}
