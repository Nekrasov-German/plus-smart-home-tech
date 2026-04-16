package ru.yandex.practicum.interaction.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotAvailableServiceException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public NotAvailableServiceException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public NotAvailableServiceException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "Сервис не доступен";
    }
}
