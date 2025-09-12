package com.inventario.dto;

public record StockRequest(
                Long productId,
                Long warehouseId,
                Integer onHand,
                Integer reserved) {
}
