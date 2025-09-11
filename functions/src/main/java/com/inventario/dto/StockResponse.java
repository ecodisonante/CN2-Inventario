package com.inventario.dto;

public record StockResponse(
                long productId,
                long warehouseId,
                int onHand,
                int reserved,
                String updatedAt,
                int available) {
}
