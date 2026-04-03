package ru.yandex.practicum.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@UtilityClass
public class CartMapper {
    public ShoppingCart shoppingCartDtoToShoppingCart(ShoppingCartDto shoppingCartDto, String userName) {
        return ShoppingCart.builder()
                .shoppingCartId(shoppingCartDto.getShoppingCartId())
                .products(shoppingCartDto.getProducts())
                .userName(userName)
                .build();
    }

    public ShoppingCartDto shoppingCartToShoppingCartDto(ShoppingCart shoppingCart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(shoppingCart.getShoppingCartId())
                .products(shoppingCart.getProducts())
                .build();
    }
}
