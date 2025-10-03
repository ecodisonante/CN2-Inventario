package com.inventario.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.inventario.dto.WarehouseResponse;
import com.inventario.dto.NotificationDto;
import com.inventario.dto.ProductResponse;
import com.inventario.events.CrudAction;
import com.inventario.mapper.NotificationMapper;
import com.inventario.notifications.EmailSender;
import com.inventario.util.Json;

public class EmailNotificationService {
    private static final Logger log = LogManager.getLogger(EmailNotificationService.class);
    private static final ContactService contactService = new ContactService();
    private static final WarehouseService warehouseService = new WarehouseService();

    public void process(String type, JsonNode data) throws IOException, SQLException {

        String[] parts = type.split("\\.");
        CrudAction action = CrudAction.valueOf(parts[3].toUpperCase());

        switch (type) {
            case "com.inventario.product.created",
                    "com.inventario.product.updated",
                    "com.inventario.product.deleted" -> {
                var prod = Json.readNode(data, ProductResponse.class);
                var dto = NotificationMapper.toDto(prod);
                sendProductEmail(dto, action);
            }
            case "com.inventario.warehouse.created",
                    "com.inventario.warehouse.updated" -> {
                var dto = Json.readNode(data, NotificationDto.class);
                sendWarehouseEmail(dto, action);
            }
            case "com.inventario.warehouse.deleted" -> {
                var wh = Json.readNode(data, WarehouseResponse.class);
                var dto = NotificationMapper.toDto(wh);
                sendWarehouseEmail(dto, action);
            }
            default -> {
                log.warn("Evento no soportado: {}", type);
            }
        }
    }

    private void sendProductEmail(NotificationDto data, CrudAction action) throws IOException, SQLException {

        if (data.getWarehouseName() == null || data.getWarehouseName().isBlank()) {
            var wh = warehouseService.getById(data.getWarehouseId());
            if (wh == null || wh.name() == null || wh.name().isBlank()) {
                log.error("Bodega no encontrada para id: {}", data.getWarehouseId());
                return;
            }
            data.setWarehouseName(wh.name());
        }

        if (data.getAdminEmail() == null || data.getAdminEmail().isBlank()) {
            var admin = contactService.findWarehouseAdmin(data.getWarehouseId());
            if (admin == null || admin.getEmail() == null || admin.getEmail().isBlank()) {
                log.error("Administrador de bodega no encontrado o sin email para bodega id: {}",
                        data.getWarehouseId());
                return;
            }
            data.setAdminEmail(admin.getEmail());
            data.setAdminName(admin.getName());
        }

        log.info("Email Data: admin_mane={}, admin_email={}, warehouse_name={}, product_name={}",
                data.getAdminName(), data.getAdminEmail(), data.getWarehouseName(), data.getProductName());

        String subject = switch (action) {
            case CREATED -> "Nuevo producto registrado";
            case UPDATED -> "Producto actualizado";
            case DELETED -> "Producto eliminado";
            case DELETING -> "Producto en proceso de eliminación";
        };

        Map<String, String> placeholders = Map.of(
                "--admin_name--", data.getAdminName(),
                "--warehouse_name--", data.getWarehouseName(),
                "--product_name--", data.getProductName());

        sendEmail("Product", action, data.getAdminEmail(), subject, placeholders);
    }

    private void sendWarehouseEmail(NotificationDto data, CrudAction action) throws IOException, SQLException {
        if (data.getAdminEmail() == null || data.getAdminEmail().isBlank()) {
            var admin = contactService.findWarehouseAdmin(data.getWarehouseId());
            if (admin == null || admin.getEmail() == null || admin.getEmail().isBlank()) {
                log.error("Administrador de bodega no encontrado o sin email para bodega id: {}",
                        data.getWarehouseId());
                return;
            }
            data.setAdminEmail(admin.getEmail());
            data.setAdminName(admin.getName());
        }

        log.info(
                "Email Data: warehouse_name={}, admin_name={}, admin_email={}, deleted_contacts={}, deleted_stock={}, disabled_products={}",
                data.getWarehouseName(), data.getAdminName(), data.getAdminEmail(),
                data.getDeletedContacts(), data.getDeletedStock(), data.getDisabledProducts());

        String subject = switch (action) {
            case CREATED -> "Nueva bodega registrada";
            case UPDATED -> "Bodega actualizada";
            case DELETED -> "Bodega eliminada";
            case DELETING -> "Bodega en proceso de eliminación";
        };

        Map<String, String> placeholders = Map.of(
                "--warehouse_name--", data.getWarehouseName(),
                "--admin_name--", data.getAdminName(),
                "--deleted_contacts--", String.valueOf(data.getDeletedContacts()),
                "--deleted_stock--", String.valueOf(data.getDeletedStock()),
                "--disabled_products--", String.valueOf(data.getDisabledProducts()));

        sendEmail("Warehouse", action, data.getAdminEmail(), subject, placeholders);
    }

    private void sendEmail(String entity, CrudAction action, String toEmail, String subject,
            Map<String, String> placeholders) throws IOException {
        String templatePath = "/EmailTemplates/" + entity
                + action.name().substring(0, 1).toUpperCase()
                + action.name().substring(1).toLowerCase();

        try (InputStream htmlStream = getClass().getResourceAsStream(templatePath + ".html");
                InputStream txtStream = getClass().getResourceAsStream(templatePath + ".txt")) {

            if (htmlStream == null || txtStream == null) {
                log.error("No se encontraron las plantillas para: {}", templatePath);
                return;
            }

            String htmlTemplate = new String(htmlStream.readAllBytes(), StandardCharsets.UTF_8);
            String txtTemplate = new String(txtStream.readAllBytes(), StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                htmlTemplate = htmlTemplate.replace(entry.getKey(), entry.getValue());
                txtTemplate = txtTemplate.replace(entry.getKey(), entry.getValue());
            }

            EmailSender.send(toEmail, subject, txtTemplate, htmlTemplate);
        }
    }

}
