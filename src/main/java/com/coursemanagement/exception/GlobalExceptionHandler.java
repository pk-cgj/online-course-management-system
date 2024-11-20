package com.coursemanagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    private HttpServletRequest request;

    @ExceptionHandler(CourseEntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(CourseEntityNotFoundException ex) {
        log.error("Entity not found: {}", ex.getMessage());
        return ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "Resource not found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalException(RuntimeException ex) {
        log.error("Invalid request: {}", ex.getMessage());
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Invalid request", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Database error: {}", ex.getMessage());
        return ErrorResponse.of(HttpStatus.CONFLICT.value(), "Database conflict", "The operation could not be completed due to a data conflict", request.getRequestURI());
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleHttpClientErrorExceptionUnauthorized(HttpClientErrorException.Unauthorized ex) {
        log.error("Unexpected error occurred", ex);
        return ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), "Http Client Request Unauthorised",
                "An unexpected error occurred", request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Unexpected error occurred", ex);
        return ErrorResponse.of(HttpStatus.FORBIDDEN.value(), "Access Denied Exception", "An unexpected error occurred", request.getRequestURI());
    }
}


