package com.inventario.bff.dto;

public record WarehouseResponse(
        long id,
        String name,
        String location,
        String enabled,
        String createdAt) {
}
