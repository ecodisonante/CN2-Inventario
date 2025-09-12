package com.inventario.bff.dto;

public record WarehouseRequest(
        String name,
        String location,
        String enabled) {
}
