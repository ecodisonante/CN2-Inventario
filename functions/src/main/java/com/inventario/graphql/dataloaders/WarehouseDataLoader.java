package com.inventario.graphql.dataloaders;

import org.dataloader.*;

import com.inventario.dto.WarehouseResponse;
import com.inventario.service.WarehouseService;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WarehouseDataLoader {

    private final WarehouseService warehouseService;

    public WarehouseDataLoader(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public DataLoaderRegistry create() {
        DataLoaderRegistry reg = new DataLoaderRegistry();

        BatchLoaderWithContext<Long, WarehouseResponse> especieBatch = (keys, ctx) -> CompletableFuture
                .supplyAsync(() -> {
                    List<Long> ids = new ArrayList<>(keys);
                    List<WarehouseResponse> list;
                    try {
                        list = warehouseService.findByIds(ids);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        // en caso de error, retornar lista vacía
                        list = List.of();
                    }

                    return list.stream().collect(Collectors.toMap(WarehouseResponse::id, w -> w));
                }).thenApply(map -> keys.stream().map(k -> map.get(k)).toList());

        reg.register("warehouseLoader", DataLoaderFactory.newDataLoader(especieBatch));
        return reg;
    }
}
