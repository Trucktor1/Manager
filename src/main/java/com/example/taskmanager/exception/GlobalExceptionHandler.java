package com.example.taskmanager.exception;

import com.example.taskmanager.dto.ErrorResponse;
import com.example.taskmanager.dto.FieldErrorDto;
import com.example.taskmanager.dto.ValidationErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleValidationException(MethodArgumentNotValidException ex) {

        List<FieldErrorDto> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldErrorDto(err.getField(), defaultMessage(err)))
                .toList();

        ValidationErrorDto response = new ValidationErrorDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Task not found",
                "/task/" + ex.getTaskId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    private String defaultMessage(FieldError err) {
        String msg = err.getDefaultMessage();
        return (msg == null || msg.isBlank()) ? "Invalid value" : msg;
    }
}