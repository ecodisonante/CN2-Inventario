package com.inventario.graphql.config;

import graphql.GraphQL;
import graphql.schema.idl.*;
import graphql.schema.DataFetcher;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.inventario.service.*;
import com.inventario.dto.ProductResponse;
import com.inventario.dto.StockResponse;
import com.inventario.dto.WarehouseResponse;

public class GraphQLProvider {
  private final GraphQL graphQL;

  public GraphQLProvider(
      ProductService productSrv,
      StockService stockSrv,
      WarehouseService warehouseSrv) {

    // Cargar schema
    InputStream s = getClass().getResourceAsStream("/schema.graphqls");
    var typeRegistry = new SchemaParser().parse(new InputStreamReader(s, StandardCharsets.UTF_8));

    // -- STOCK --
    DataFetcher<?> stock = env -> {
      // stock(productId: ID, warehouseId: ID, limit: Int = 20, offset: Int = 0):
      // [Stock!]!
      Long productId = env.containsArgument("productId") ? Long.valueOf(env.getArgument("productId")) : null;
      Long warehouseId = env.containsArgument("warehouseId") ? Long.valueOf(env.getArgument("warehouseId")) : null;
      Integer limit = env.getArgumentOrDefault("limit", 20);
      Integer offset = env.getArgumentOrDefault("offset", 0);

      return stockSrv.getStock(productId, warehouseId, limit, offset);
    };

    // stock.producto
    DataFetcher<?> productFetcher = env -> {
      StockResponse sr = env.getSource();
      System.out.println(sr);
      var loader = env.<Long, ProductResponse>getDataLoader("productLoader");
      return loader.load(sr.productId());
    };

    // stock.warehouse
    DataFetcher<?> warehouseFetcher = env -> {
      StockResponse sr = env.getSource();
      System.out.println(sr);
      var loader = env.<Long, WarehouseResponse>getDataLoader("warehouseLoader");
      return loader.load(sr.warehouseId());
    };

    // --- MUTACIONES ---

    // receiveStock(productId: ID!, warehouseId: ID!, qty: Int!, reference
    DataFetcher<?> receiveStock = env -> {
      Long productId = env.containsArgument("productId") ? Long.valueOf(env.getArgument("productId")) : null;
      Long warehouseId = env.containsArgument("warehouseId") ? Long.valueOf(env.getArgument("warehouseId")) : null;
      Integer qty = env.getArgument("qty");
      String reference = env.getArgument("reference");

      return stockSrv.receive(productId, warehouseId, qty, reference);
    };

    // Wiring
    var wiring = RuntimeWiring.newRuntimeWiring()
        .type("Query", b -> b.dataFetcher("stock", stock))
        .type("Mutation", b -> b.dataFetcher("receiveStock", receiveStock))
        .type("Stock", b -> b
            .dataFetcher("product", productFetcher)
            .dataFetcher("warehouse", warehouseFetcher))
        .build();

    var schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
    this.graphQL = GraphQL.newGraphQL(schema).build();
  }

  public GraphQL graphQL() {
    return graphQL;
  }

}
