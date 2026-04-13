package ru.yandex.practicum.interaction.dto_order;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto_warehouse.AddressDto;

@Data
@Builder
public class CreateNewOrderRequest {
    private ShoppingCartDto shoppingCart;
    private AddressDto deliveryAddress;
}
