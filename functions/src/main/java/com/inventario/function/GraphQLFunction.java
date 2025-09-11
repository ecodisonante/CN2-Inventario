package com.inventario.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.inventario.graphql.util.GraphQLRequest;
import com.inventario.graphql.config.GraphQLProvider;
import com.inventario.graphql.dataloaders.ProductDataLoader;
import com.inventario.graphql.dataloaders.WarehouseDataLoader;

import graphql.ExecutionInput;

import java.util.Map;
import java.util.Optional;

import org.dataloader.DataLoaderRegistry;

import com.inventario.service.*;

public class GraphQLFunction {

  private final GraphQLProvider provider;
  private final ProductDataLoader productDataLoader;
  private final WarehouseDataLoader warehouseDataLoader;

  public GraphQLFunction() {
    // O usa inyección propia si ya la tienes
    var productoSrv = new ProductService();
    var warehouseSrv = new WarehouseService();
    var stockSrv = new StockService();

    this.provider = new GraphQLProvider(productoSrv, stockSrv, warehouseSrv);
    this.productDataLoader = new ProductDataLoader(productoSrv);
    this.warehouseDataLoader = new WarehouseDataLoader(warehouseSrv);
  }

  @FunctionName("GraphQL")
  public HttpResponseMessage run(
      @HttpTrigger(name = "req", methods = {
          HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS, route = "graphql") HttpRequestMessage<GraphQLRequest> req,
      final ExecutionContext ctx) {

    try {
      var body = Optional.ofNullable(req.getBody()).orElseGet(GraphQLRequest::new);
      var vars = body.getVariables() == null ? Map.<String, Object>of() : body.getVariables();

      DataLoaderRegistry registry = new DataLoaderRegistry();

      DataLoaderRegistry pr = productDataLoader.create();
      DataLoaderRegistry wr = warehouseDataLoader.create();

      registry.register("productLoader", pr.getDataLoader("productLoader"));
      registry.register("warehouseLoader", wr.getDataLoader("warehouseLoader"));

      var exec = ExecutionInput.newExecutionInput()
          .query(body.getQuery())
          .operationName(body.getOperationName())
          .variables(vars)
          .dataLoaderRegistry(registry)
          .build();

      var result = provider.graphQL().execute(exec);

      return req.createResponseBuilder(HttpStatus.OK)
          .header("Content-Type", "application/json")
          .body(result.toSpecification())
          .build();

    } catch (Exception e) {
      ctx.getLogger().severe("GraphQL error: " + e.getMessage());
      return req.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", e.getMessage()))
          .build();
    }
  }
}
