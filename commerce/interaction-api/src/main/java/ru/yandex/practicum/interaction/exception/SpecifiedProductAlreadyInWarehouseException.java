package ru.yandex.practicum.interaction.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String userMessage;

    public SpecifiedProductAlreadyInWarehouseException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    // Упрощённый конструктор для случая, когда товар уже есть на складе
    public SpecifiedProductAlreadyInWarehouseException(UUID productId) {
        super("Product with ID " + productId + " already exists in warehouse");
        this.httpStatus = HttpStatus.CONFLICT; // 409 Conflict
        this.userMessage = "Товар с указанным ID уже существует на складе";
    }
}
