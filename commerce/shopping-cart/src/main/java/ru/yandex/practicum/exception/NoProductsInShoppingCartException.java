package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class NoProductsInShoppingCartException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public NoProductsInShoppingCartException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public NoProductsInShoppingCartException(UUID productId) {
        super("Нет искомых товаров в корзине");
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "Нет искомых товаров в корзине : " + productId;
    }
}
