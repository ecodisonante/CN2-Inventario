// function/WarehousesReadAll.java
package com.inventario.function.warehouse;

import com.inventario.service.WarehouseService;
import com.inventario.util.Json;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class WarehousesGetbyId {
  private final WarehouseService service = new WarehouseService();

  @FunctionName("warehouses-get-by-id")
  public HttpResponseMessage handle(
      @HttpTrigger(name = "req", methods = {
          HttpMethod.GET }, route = "warehouses/{id}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
      @BindingName("id") String idStr,
      final ExecutionContext ctx) {

    try {
      long id = Long.parseLong(idStr);
      var result = service.getById(id);

      return request.createResponseBuilder(HttpStatus.OK)
          .header("Content-Type", "application/json")
          .body(Json.write(result))
          .build();

    } catch (Exception e) {
      ctx.getLogger().severe("Error al obtener bodega: " + e.getMessage() + "\n" + e.getStackTrace());
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .header("Content-Type", "application/json")
          .body("{\"error\":\"" + e.getMessage() + "\"}")
          .build();
    }
  }
}
