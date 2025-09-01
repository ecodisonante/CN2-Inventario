package com.inventario.mapper;

import java.time.format.DateTimeFormatter;

import com.inventario.dto.WarehouseRequest;
import com.inventario.dto.WarehouseResponse;
import com.inventario.model.Warehouse;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WarehouseMapper {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static WarehouseResponse toResponse(Warehouse w) {

        String stringTimestamp = "";
        if (w.getCreatedAt() != null)
            stringTimestamp = w.getCreatedAt().toLocalDateTime().format(dtf);

        return new WarehouseResponse(
                w.getId(),
                w.getName(),
                w.getLocation(),
                w.getEnabled(),
                stringTimestamp);
    }

    public static Warehouse toModel(WarehouseRequest req) {
        var w = new Warehouse();
        w.setName(req.name());
        w.setLocation(req.location());
        w.setEnabled((req.enabled() == null || req.enabled().isBlank()) ? "S" : req.enabled());
        return w;
    }
}
