package com.inventario.function.product;

import com.inventario.service.ProductService;
import com.inventario.util.Json;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class ProductsGetAll {
  private final ProductService service = new ProductService();

  @FunctionName("products-get-all")
  public HttpResponseMessage handle(
      @HttpTrigger(name = "req", methods = {
          HttpMethod.GET }, route = "products", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
      final ExecutionContext ctx) {

    try {
      var result = service.getAll();

      return request.createResponseBuilder(HttpStatus.OK)
          .header("Content-Type", "application/json")
          .body(Json.write(result))
          .build();

    } catch (Exception e) {
      ctx.getLogger().severe("Error al listar productos: " + e.getMessage() + "\n" + e.getStackTrace());
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .header("Content-Type", "application/json")
          .body("{\"error\":\"" + e.getMessage() + "\"}")
          .build();
    }
  }
}
