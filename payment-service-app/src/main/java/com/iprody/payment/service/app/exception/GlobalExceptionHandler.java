package com.iprody.payment.service.app.exception;

import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFoundException(NotFoundException e) {
        return new ErrorDto(
                HttpStatus.NOT_FOUND.value(),
                e.getLocalizedMessage(),
                Instant.now()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleOther(Exception e) {
        return new ErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getLocalizedMessage(),
                Instant.now()
        );
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleMismatch(TypeMismatchException e) {
        return new ErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                e.getLocalizedMessage(),
                Instant.now()
        );
    }
}
