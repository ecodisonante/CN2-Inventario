package com.inventario.function;

import com.inventario.dto.WarehouseRequest;
import com.inventario.service.WarehouseService;
import com.inventario.util.Json;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class WarehousesCreate {
  private final WarehouseService service = new WarehouseService();

  @FunctionName("warehouses-create")
  public HttpResponseMessage handle(
      @HttpTrigger(name = "req", methods = {
          HttpMethod.POST }, authLevel = AuthorizationLevel.FUNCTION, route = "warehouses") HttpRequestMessage<Optional<String>> request,
      final ExecutionContext ctx) {

    try {
      String body = request.getBody().orElse("");
      WarehouseRequest dto = Json.read(body, WarehouseRequest.class);

      var created = service.create(dto);
      
      return request.createResponseBuilder(HttpStatus.CREATED)
          .header("Content-Type", "application/json")
          .body(Json.write(created))
          .build();

    } catch (Exception e) {
      ctx.getLogger().severe("Error al crear almacén: " + e.getMessage() + "\n" + e.getStackTrace());
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .header("Content-Type", "application/json")
          .body("{\"error\":\"" + e.getMessage() + "\"}")
          .build();
    }
  }
}
