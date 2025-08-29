package com.inventario.function.product;

import com.inventario.dto.ProductRequest;
import com.inventario.service.ProductService;
import com.inventario.util.Json;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class ProductsCreate {
  private final ProductService service = new ProductService();

  @FunctionName("products-create")
  public HttpResponseMessage handle(
      @HttpTrigger(name = "req", methods = {
          HttpMethod.POST }, route = "products", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
      final ExecutionContext ctx) {

    try {
      String body = request.getBody().orElse("");
      ProductRequest dto = Json.read(body, ProductRequest.class);

      var created = service.create(dto);

      return request.createResponseBuilder(HttpStatus.CREATED)
          .header("Content-Type", "application/json")
          .body(Json.write(created))
          .build();

    } catch (Exception e) {
      ctx.getLogger().severe("Error al crear producto: " + e.getMessage() + "\n" + e.getStackTrace());
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .header("Content-Type", "application/json")
          .body("{\"error\":\"" + e.getMessage() + "\"}")
          .build();
    }
  }
}
