package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotAuthorizedUserException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;

    public NotAuthorizedUserException(String message, HttpStatus httpStatus, String userMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public NotAuthorizedUserException(String userName) {
        super(userName);
        this.httpStatus = HttpStatus.UNAUTHORIZED;
        this.userMessage = "Имя пользователя не должно быть пустым : " + userName ;
    }
}
