package com.inventario.bff.dto;

public record ErrorResponse(
        String error,
        String message) {
}
