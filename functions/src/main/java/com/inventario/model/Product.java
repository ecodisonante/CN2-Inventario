package com.inventario.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private long id;
    private String sku;
    private String name;
    private String category;
    private BigDecimal price;
    private String enabled;
    private long warehouseId;
    private Timestamp createdAt;
}
