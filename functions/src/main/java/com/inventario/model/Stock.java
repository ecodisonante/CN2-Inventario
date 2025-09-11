package com.inventario.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    private long productId;
    private long warehouseId;
    private int onHand;
    private int reserved;
    private Timestamp updatedAt;    
}
