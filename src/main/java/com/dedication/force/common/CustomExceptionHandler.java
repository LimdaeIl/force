package com.dedication.force.common;

import com.dedication.force.common.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class CustomExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(CustomAPIException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<HttpResponse<Void>> apiException(CustomAPIException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new HttpResponse<>(-1, e.getMessage(), null), BAD_REQUEST);
    }

    @ExceptionHandler(CustomDataNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<HttpResponse<Void>> dataNotFoundException(CustomDataNotFoundException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new HttpResponse<>(-1, e.getMessage(), null), NOT_FOUND);
    }

    @ExceptionHandler(CustomForbiddenException.class)
    @ResponseStatus(FORBIDDEN)
    public ResponseEntity<HttpResponse<Void>> forbiddenException(CustomForbiddenException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new HttpResponse<>(-1, e.getMessage(), null), FORBIDDEN);
    }

    @ExceptionHandler(CustomValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> validationApiException(CustomValidationException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new HttpResponse<>(-1, e.getMessage(), e.getErrorMap()), BAD_REQUEST);
    }

    @ExceptionHandler(CustomJwtException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ResponseEntity<HttpResponse<Void>> JWTException(CustomAPIException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new HttpResponse<>(-1, e.getMessage(), null), UNAUTHORIZED);
    }

}
