package ru.yandex.practicum.service;

import ru.yandex.practicum.interaction.dto_cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {
    //Получить актуальную корзину для авторизованного пользователя
    ShoppingCartDto getCart(String userName);

    //Добавить товар в корзину.
    ShoppingCartDto addProductToCart(String userName, Map<UUID, Integer> products);

    //Деактивация корзины товаров для пользователя.
    void detectedCart(String userName);

    //Удалить указанные товары из корзины пользователя
    ShoppingCartDto deleteProduct(String userName, List<String> product);

    //Изменить количество товаров в корзине
    ShoppingCartDto changeQuantity(String userName, ChangeProductQuantityRequest request);
}
