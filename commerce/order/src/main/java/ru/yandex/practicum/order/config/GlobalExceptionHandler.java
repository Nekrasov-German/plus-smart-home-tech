package ru.yandex.practicum.order.config;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.yandex.practicum.interaction.exception.NotAvailableServiceException;
import ru.yandex.practicum.order.exception.*;

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

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ApiErrorResponse> handleNotAuthorizedUserException(
            NotAuthorizedUserException ex,
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

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ApiErrorResponse> handleNoSpecifiedProductInWarehouseException(
            NoSpecifiedProductInWarehouseException ex,
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

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoOrderFoundException(
            NoOrderFoundException ex,
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

    @ExceptionHandler(NotPaymentException.class)
    public ResponseEntity<ApiErrorResponse> handleNotPaymentException(
            NotPaymentException ex,
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

    @ExceptionHandler(NotAvailableServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleNotAvailableServiceException(
            NotAvailableServiceException ex,
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

    @ExceptionHandler(NotAssembledOrderException.class)
    public ResponseEntity<ApiErrorResponse> handleNotAssembledOrderException(
            NotAssembledOrderException ex,
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
