package com.inventario.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.List;

import com.fasterxml.jackson.databind.*;
import com.inventario.dto.WarehouseResponse;
import com.inventario.service.WarehouseService;
import com.inventario.util.Json;

public class EventSunscriptionFunction {
  WarehouseService warehouseService = new WarehouseService();
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @FunctionName("EventSunscription")
  public void run(@EventGridTrigger(name = "event") String eventJson, final ExecutionContext ctx) {
    try {
      JsonNode root = MAPPER.readTree(eventJson);
      String type = root.has("eventType") ? root.get("eventType").asText()
          : root.has("type") ? root.get("type").asText() : null;
      JsonNode data = root.get("data");
      ctx.getLogger().info("Evento: " + type + " data=" + data);

      if (isValid(type)) {
        var wh = Json.readNode(data, WarehouseResponse.class);
        warehouseService.complexDelete(wh.id());
      } else {
        ctx.getLogger().info("Evento no soportado");
      }

    } catch (Exception e) {
      ctx.getLogger().severe("Error Notificaciones: " + e.getMessage());
      throw new RuntimeException(e); // permite reintentos de Event Grid
    }
  }

  private boolean isValid(String type) {
    List<String> validTypes = List.of(
        // procesar borrado complejo de bodega
        "com.inventario.warehouse.deleting");

    return validTypes.contains(type.toLowerCase());
  }
}
