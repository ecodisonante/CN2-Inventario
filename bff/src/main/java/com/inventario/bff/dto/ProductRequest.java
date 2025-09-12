package com.inventario.bff.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record ProductRequest(
        Long id,
        String sku,
        String name,
        String category,
        BigDecimal price,
        String enabled,
        Long warehouseId,
        Timestamp createdAt
) {}
