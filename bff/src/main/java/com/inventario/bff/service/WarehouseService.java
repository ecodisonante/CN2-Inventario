package com.inventario.bff.service;

import java.util.List;

import com.inventario.bff.dto.WarehouseRequest;
import com.inventario.bff.dto.WarehouseResponse;

public interface WarehouseService {

    List<WarehouseResponse> getWarehouses();

    WarehouseResponse getWarehouseById(long id);

    WarehouseResponse createWarehouse(WarehouseRequest warehouse);

    WarehouseResponse updateWarehouse(long id, WarehouseRequest warehouse);

    void deleteWarehouse(long id);
}
