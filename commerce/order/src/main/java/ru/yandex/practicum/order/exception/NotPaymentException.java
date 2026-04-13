package ru.yandex.practicum.order.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotPaymentException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public NotPaymentException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public NotPaymentException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "Товар не оплачен.";
    }
}
