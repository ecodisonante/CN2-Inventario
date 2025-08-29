package com.inventario.function;

import com.inventario.dto.WarehouseRequest;
import com.inventario.service.WarehouseService;
import com.inventario.util.Json;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class WarehousesUpdate {
  private final WarehouseService service = new WarehouseService();

  @FunctionName("warehouses-update")
  public HttpResponseMessage handle(
      @HttpTrigger(name = "req", methods = {
          HttpMethod.PUT }, route = "warehouses/{id}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
      @BindingName("id") String idStr,
      final ExecutionContext ctx) {

    try {
      long id = Long.parseLong(idStr);
      String body = request.getBody().orElse("");
      WarehouseRequest dto = Json.read(body, WarehouseRequest.class);

      var updated = service.update(id, dto);

      return request.createResponseBuilder(HttpStatus.OK)
          .header("Content-Type", "application/json")
          .body(Json.write(updated))
          .build();

    } catch (Exception e) {
      ctx.getLogger().severe("Error al actualizar bodega: " + e.getMessage() + "\n" + e.getStackTrace());
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .header("Content-Type", "application/json")
          .body("{\"error\":\"" + e.getMessage() + "\"}")
          .build();
    }
  }
}
