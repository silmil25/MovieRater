package com.web.movierater.exceptions;

import com.web.movierater.models.dtos.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            UsernameNotFoundException ex,
            HttpServletRequest request) {
        ErrorResponse er = buildError(HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(er);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {
        ErrorResponse er = buildError(HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(er);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorization(
            AccessDeniedException ex,
            HttpServletRequest request) {
        ErrorResponse er = buildError(HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(er);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorization(
            AuthorizationException ex,
            HttpServletRequest request) {
        ErrorResponse er = buildError(HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(er);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {
        ErrorResponse er = buildError(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            DuplicateEntityException ex,
            HttpServletRequest request) {
        ErrorResponse er = buildError(HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(er);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        ErrorResponse er = buildError(HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.badRequest().body(er);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        BindingResult br = ex.getBindingResult();
        List<ErrorResponse.FieldError> fieldErrors = br.getFieldErrors().stream()
                .map(fe -> {
                    return new ErrorResponse.FieldError(
                            fe.getField(),
                            fe.getRejectedValue() == null ? null : fe.getRejectedValue().toString(),
                            fe.getDefaultMessage()
                    );
                }).collect(Collectors.toList());

        ErrorResponse er = buildError(HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI());

        er.setFieldErrors(fieldErrors);
        return ResponseEntity.badRequest().body(er);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        ErrorResponse er = buildError(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON request",
                request.getRequestURI());
        return ResponseEntity.badRequest().body(er);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(
            Exception ex, HttpServletRequest request) {

        ex.printStackTrace();

        ErrorResponse er = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
    }

    private ErrorResponse buildError(HttpStatus status, String message,
                                     String path) {
        ErrorResponse er = new ErrorResponse();
        er.setStatus(status.value());
        er.setError(status.getReasonPhrase());
        er.setMessage(message);
        er.setTimestamp(OffsetDateTime.now());
        er.setPath(path);
        return er;
    }
}
