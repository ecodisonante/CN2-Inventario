package com.inventario.dto;

import java.sql.Timestamp;

public record WarehouseRequest(
        Long id,
        String name,
        String location,
        String enabled,
        Timestamp createdAt) {
}
