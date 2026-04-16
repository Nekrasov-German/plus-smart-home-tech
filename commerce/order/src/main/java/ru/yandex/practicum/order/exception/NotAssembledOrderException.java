package ru.yandex.practicum.order.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotAssembledOrderException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public NotAssembledOrderException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public NotAssembledOrderException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "Товар не собран на складе.";
    }
}
