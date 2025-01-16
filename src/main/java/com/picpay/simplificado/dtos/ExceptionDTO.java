package com.picpay.simplificado.dtos;

import java.time.Instant;

public record ExceptionDTO(
        Instant moment,
        Integer status,
        String error,
        String message,
        String path) {
}
