package ru.yandex.practicum.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotEnoughInfoInOrderToCalculateException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public NotEnoughInfoInOrderToCalculateException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public NotEnoughInfoInOrderToCalculateException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = "Недостаточно информации для расчета.";
    }
}
