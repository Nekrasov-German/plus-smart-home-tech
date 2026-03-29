package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public ProductNotFoundException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public ProductNotFoundException(UUID productId) {
        super("Product with ID " + productId + " not found in warehouse");
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.userMessage = "Ошибка, товар по идентификатору в БД не найден : ID " + productId;
    }
}
