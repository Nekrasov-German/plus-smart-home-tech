package ru.yandex.practicum.interaction.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class NoSpecifiedProductInWarehouseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String userMessage;

    public NoSpecifiedProductInWarehouseException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    // Упрощённый конструктор
    public NoSpecifiedProductInWarehouseException(UUID productId) {
        super("Product with ID " + productId + " not found in warehouse");
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "Товар с указанным ID не найден на складе";
    }
}
