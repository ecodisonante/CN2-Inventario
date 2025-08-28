package com.inventario.dto;

import java.sql.Timestamp;

public record WarehouseResponse(
    long id,
    String name,
    String location,
    String enabled,
    Timestamp createdAt
) {}
