package com.inventario.dto;

import java.math.BigDecimal;

public record ProductResponse(
    long id,
    String sku,
    String name,
    String category,
    BigDecimal price,
    String enabled,
    long warehouseId,
    String createdAt
) {}
