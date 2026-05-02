package com.example.cinema.exception;

import com.example.cinema.dto.Dto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exceptions.ResourceNotFoundException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleNotFound(Exceptions.ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Dto.ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.ResourceAlreadyExistsException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleConflict(Exceptions.ResourceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Dto.ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.BadRequestException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleBadRequest(Exceptions.BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Dto.ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.UnauthorizedException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleUnauthorized(Exceptions.UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Dto.ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Dto.ApiResponse.error("Sai username hoặc password"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Dto.ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Dto.ApiResponse.<Map<String, String>>builder()
                        .success(false).message("Dữ liệu không hợp lệ").data(errors).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Dto.ApiResponse.error("Lỗi server: " + ex.getMessage()));
    }
}
