package com.inventario.mapper;

import java.time.format.DateTimeFormatter;

import com.inventario.dto.StockRequest;
import com.inventario.dto.StockResponse;
import com.inventario.model.Stock;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StockMapper {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StockResponse toResponse(Stock s) {
        String created = "";
        if (s.getUpdatedAt() != null)
            created = s.getUpdatedAt().toLocalDateTime().format(dtf);

        return new StockResponse(
            s.getProductId(),
            s.getWarehouseId(),
            s.getOnHand(),
            s.getReserved(),
            created,
            s.getOnHand() - s.getReserved()
        );
    }

    public static Stock toModel(StockRequest req) {
        var s = new Stock();
        s.setProductId(req.productId());
        s.setWarehouseId(req.warehouseId());
        s.setOnHand(req.onHand());
        s.setReserved(req.reserved());
        return s;
    }
}
