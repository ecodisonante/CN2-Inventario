package com.inventario.bff.service;

import java.util.List;

import com.inventario.bff.dto.WarehouseCreateRequest;
import com.inventario.bff.dto.WarehouseItem;

public interface WarehouseService {

    List<WarehouseItem> getWarehouses();

    WarehouseItem getWarehouseById(long id);

    WarehouseItem createWarehouse(WarehouseCreateRequest warehouse);

    WarehouseItem updateWarehouse(long id, WarehouseCreateRequest warehouse);

    void deleteWarehouse(long id);
}
