package com.inventario.bff.dto;

public record WarehouseItem(
        long id,
        String name,
        String location,
        String enabled,
        String createdAt) {
}
