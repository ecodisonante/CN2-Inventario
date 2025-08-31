package com.inventario.bff.dto;

public record WarehouseCreateRequest(
        String name,
        String location,
        String enabled) {
}
