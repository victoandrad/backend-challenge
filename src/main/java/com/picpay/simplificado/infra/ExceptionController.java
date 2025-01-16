package com.picpay.simplificado.infra;

import com.picpay.simplificado.dtos.ExceptionDTO;
import com.picpay.simplificado.services.exceptions.DatabaseException;
import com.picpay.simplificado.services.exceptions.ResourceNotFoundException;
import com.picpay.simplificado.services.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionDTO dto = new ExceptionDTO(Instant.now(), status.value(), "Resource Not Found", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(dto);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ExceptionDTO> handleDatabaseException(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionDTO dto = new ExceptionDTO(Instant.now(), status.value(), "Database Error", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(dto);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionDTO> handleUnauthorizedException(UnauthorizedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ExceptionDTO dto = new ExceptionDTO(Instant.now(), status.value(), "Unauthorized", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(dto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionDTO dto = new ExceptionDTO(Instant.now(), status.value(), "Internal Server Error", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(dto);
    }
}
