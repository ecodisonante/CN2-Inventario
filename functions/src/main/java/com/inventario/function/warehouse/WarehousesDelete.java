package com.inventario.function.warehouse;

import com.inventario.service.WarehouseService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class WarehousesDelete {
  private final WarehouseService service = new WarehouseService();

  @FunctionName("warehouses-delete")
  public HttpResponseMessage handle(
      @HttpTrigger(name = "req", methods = {
          HttpMethod.DELETE }, route = "warehouses/{id}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
      @BindingName("id") String idStr,
      final ExecutionContext ctx) {

    try {
      long id = Long.parseLong(idStr);
      service.delete(id);

      return request.createResponseBuilder(HttpStatus.NO_CONTENT)
          .header("Content-Type", "application/json")
          .build();

    } catch (Exception e) {
      ctx.getLogger().severe("Error al eliminar bodega: " + e.getMessage() + "\n" + e.getStackTrace());
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .header("Content-Type", "application/json")
          .body("{\"error\":\"" + e.getMessage() + "\"}")
          .build();
    }
  }
}
