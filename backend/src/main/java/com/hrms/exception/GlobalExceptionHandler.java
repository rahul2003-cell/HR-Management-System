package com.hrms.exception;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
    record ApiError(boolean success, String message, Object data, LocalDateTime timestamp) {}
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handle404(ResourceNotFoundException e) {
        return ResponseEntity.status(404).body(new ApiError(false,e.getMessage(),null,LocalDateTime.now()));
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handle400(BadRequestException e) {
        return ResponseEntity.badRequest().body(new ApiError(false,e.getMessage(),null,LocalDateTime.now()));
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleAuth(BadCredentialsException e) {
        return ResponseEntity.status(401).body(new ApiError(false,"Invalid credentials",null,LocalDateTime.now()));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccess(AccessDeniedException e) {
        return ResponseEntity.status(403).body(new ApiError(false,"Access denied",null,LocalDateTime.now()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        Map<String,String> errs = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err->errs.put(((FieldError)err).getField(),err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ApiError(false,"Validation failed",errs,LocalDateTime.now()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception e) {
        return ResponseEntity.status(500).body(new ApiError(false,"Internal error: "+e.getMessage(),null,LocalDateTime.now()));
    }
}