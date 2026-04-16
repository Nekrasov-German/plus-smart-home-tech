package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TestAddWarehouseException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public TestAddWarehouseException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public TestAddWarehouseException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "Товар с указанным ID не найден на складе";
    }
}
