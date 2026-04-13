package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceUnavailableException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public ServiceUnavailableException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public ServiceUnavailableException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "Сервис склада не доступен" + message;
    }
}
