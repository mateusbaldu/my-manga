package fatecipi.progweb.mymanga.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS dd/MM/yyyy");
    String timestamp = LocalDateTime.now().format(formatter);

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", timestamp);
        response.put("message", ex.getMessage());
        response.put("errors", ex.getConstraintViolations()
                .stream()
                .map(violation -> Map.of(
                        "field", violation.getPropertyPath().toString(),
                        "message", violation.getMessage()
                ))
                .toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", timestamp);
        response.put("message", ex.getMessage());
        response.put("errors", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of("field", Objects.requireNonNull(error.getField()), "message", error.getDefaultMessage()))
                .toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception e) {
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        HttpStatus status = (responseStatus != null) ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
        String errorTitle = (responseStatus != null) ? status.getReasonPhrase() : "Erro Interno do Servidor";

        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("timestamp", timestamp);
        body.put("message", e.getMessage());
        body.put("error", errorTitle);

        return new ResponseEntity<>(body, status);
    }
}
