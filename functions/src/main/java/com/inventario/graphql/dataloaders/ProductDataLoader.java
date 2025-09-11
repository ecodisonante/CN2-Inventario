package com.inventario.graphql.dataloaders;

import org.dataloader.*;

import com.inventario.dto.ProductResponse;
import com.inventario.service.ProductService;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ProductDataLoader {

    private final ProductService productService;

    public ProductDataLoader(ProductService productService) {
        this.productService = productService;
    }

    public DataLoaderRegistry create() {
        DataLoaderRegistry reg = new DataLoaderRegistry();

        BatchLoaderWithContext<Long, ProductResponse> productBatch = (keys, ctx) -> CompletableFuture
                .supplyAsync(() -> {
                    List<Long> ids = new ArrayList<>(keys);
                    List<ProductResponse> list;
                    try {
                        list = productService.findByIds(ids);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        // en caso de error, retornar lista vacía
                        list = List.of();
                    }

                    return list.stream().collect(Collectors.toMap(ProductResponse::id, p -> p));
                }).thenApply(map -> keys.stream().map(k -> map.get(k)).toList());

        reg.register("productLoader", DataLoaderFactory.newDataLoader(productBatch));
        return reg;
    }
}
