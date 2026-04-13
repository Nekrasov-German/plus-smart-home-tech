package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderNotCreatedException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public OrderNotCreatedException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public OrderNotCreatedException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "Заказ не создан сервис не отвечает" + message;
    }
}
