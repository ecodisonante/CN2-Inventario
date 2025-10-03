package com.inventario.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.List;

import com.fasterxml.jackson.databind.*;
import com.inventario.service.EmailNotificationService;

public class NotificacionesFunction {
  EmailNotificationService emailService = new EmailNotificationService();
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @FunctionName("NotificationSubscription")
  public void run(@EventGridTrigger(name = "event") String eventJson, final ExecutionContext ctx) {
    try {
      JsonNode root = MAPPER.readTree(eventJson);
      String type = root.has("eventType") ? root.get("eventType").asText()
          : root.has("type") ? root.get("type").asText() : null;
      JsonNode data = root.get("data");
      ctx.getLogger().info("Evento: " + type + " data=" + data);

      if (isValid(type)) {
        emailService.process(type, data);
      } else {
        ctx.getLogger().warning("Evento no soportado");
      }

    } catch (Exception e) {
      ctx.getLogger().severe("Error Notificaciones: " + e.getMessage());
      throw new RuntimeException(e); // permite reintentos de Event Grid
    }
  }

  private boolean isValid(String type) {
    List<String> validTypes = List.of(
        "com.inventario.product.created",
        "com.inventario.product.updated",
        "com.inventario.product.deleted",
        "com.inventario.warehouse.created",
        "com.inventario.warehouse.updated",
        "com.inventario.warehouse.deleted"); // no se procesa DELETING warehouse

    return validTypes.contains(type.toLowerCase());
  }
}
