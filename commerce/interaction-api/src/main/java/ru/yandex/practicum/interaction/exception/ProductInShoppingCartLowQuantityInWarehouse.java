package ru.yandex.practicum.interaction.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String userMessage;

    public ProductInShoppingCartLowQuantityInWarehouse(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public ProductInShoppingCartLowQuantityInWarehouse(UUID productId) {
        super("Ошибка, товар из корзины не находится в требуемом количестве на складе");
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "Товар с указанным ID не находится в требуемом количестве на складе";
    }
}
