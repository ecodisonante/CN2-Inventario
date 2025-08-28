package com.inventario.mapper;

import com.inventario.dto.WarehouseRequest;
import com.inventario.dto.WarehouseResponse;
import com.inventario.model.Warehouse;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WarehouseMapper {

    public static WarehouseResponse toResponse(Warehouse w) {
        return new WarehouseResponse(
                w.getId(),
                w.getName(),
                w.getLocation(),
                w.getEnabled(),
                w.getCreatedAt());
    }


    public static Warehouse toModel(WarehouseRequest req) {
        var w = new Warehouse();
        w.setId(req.id());
        w.setName(req.name());
        w.setLocation(req.location());
        w.setEnabled((req.enabled() == null || req.enabled().isBlank()) ? "S" : req.enabled());
        return w;
    }
}
