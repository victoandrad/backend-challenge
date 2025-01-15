package com.picpay.simplificado.dtos;

import com.picpay.simplificado.domain.user.UserType;

import java.math.BigDecimal;

public record UserDTO(
        String firstName,
        String lastName,
        String document,
        String email,
        String password,
        BigDecimal balance,
        UserType userType) {
}
