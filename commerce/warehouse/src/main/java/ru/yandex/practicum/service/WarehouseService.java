package ru.yandex.practicum.service;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto_warehouse.*;

import java.util.Map;
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

    //Обновить количество товара на складе
    void assembly(AssemblyProductsForOrderRequest request);

    void shipped(ShippedToDeliveryRequest request);

    void returnProduct(Map<UUID, Integer> products);
}
