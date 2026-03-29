package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductNotEnoughWarehouseException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public ProductNotEnoughWarehouseException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public ProductNotEnoughWarehouseException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "На складе не достаточно товара" + message;
    }
}
