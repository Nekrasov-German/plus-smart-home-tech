package ru.yandex.practicum.config;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.yandex.practicum.exception.ProductNotFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Data
    private static class ApiErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String userMessage;
        private String path;

        public ApiErrorResponse(int status, String error, String message, String userMessage, String path) {
            this.timestamp = LocalDateTime.now();
            this.status = status;
            this.error = error;
            this.message = message;
            this.userMessage = userMessage;
            this.path = path;
        }
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFoundException(
            ProductNotFoundException ex,
            WebRequest request) {

        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ApiErrorResponse(
                        ex.getHttpStatus().value(),
                        ex.getHttpStatus().getReasonPhrase(),
                        ex.getMessage(),
                        ex.getUserMessage(),
                        request.getContextPath() + request.getDescription(false)
                ));
    }
}
