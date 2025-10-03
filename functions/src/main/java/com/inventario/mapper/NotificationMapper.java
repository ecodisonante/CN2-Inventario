package com.inventario.mapper;

import com.inventario.dto.NotificationDto;
import com.inventario.dto.ProductResponse;
import com.inventario.dto.WarehouseResponse;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationMapper {

    public static NotificationDto toDto(ProductResponse pr) {
        if (pr == null)
            return null;

        var dto = new NotificationDto();
        // dto.setWarehouseId(pr.warehouseId());
        // TODO: get warehouseid by stock
        dto.setProductId(pr.id());
        dto.setProductName(pr.name());

        return dto;
    }

    public static NotificationDto toDto(WarehouseResponse wr) {
        if (wr == null)
            return null;

        var dto = new NotificationDto();
        dto.setWarehouseId(wr.id());
        dto.setWarehouseName(wr.name());

        return dto;
    }
}
