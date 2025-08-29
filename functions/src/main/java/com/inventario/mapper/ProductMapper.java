package com.inventario.mapper;

import java.time.format.DateTimeFormatter;

import com.inventario.dto.ProductRequest;
import com.inventario.dto.ProductResponse;
import com.inventario.model.Product;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductMapper {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ProductResponse toResponse(Product p) {
        String created = "";
        if (p.getCreatedAt() != null)
            created = p.getCreatedAt().toLocalDateTime().format(dtf);

        return new ProductResponse(
            p.getId(),
            p.getSku(),
            p.getName(),
            p.getCategory(),
            p.getPrice(),
            p.getEnabled(),
            p.getWarehouseId(),
            created
        );
    }

    public static Product toModel(ProductRequest req) {
        var p = new Product();
        if (req.id() != null) p.setId(req.id());
        p.setSku(req.sku());
        p.setName(req.name());
        p.setCategory(req.category());
        p.setPrice(req.price());
        p.setEnabled((req.enabled() == null || req.enabled().isBlank()) ? "S" : req.enabled());
        if (req.warehouseId() != null) p.setWarehouseId(req.warehouseId());
        return p;
    }
}
