package com.inventario.bff.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.bff.dto.WarehouseCreateRequest;
import com.inventario.bff.dto.WarehouseItem;
import com.inventario.bff.service.WarehouseService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/warehouses")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<WarehouseItem>> getAllWarehouses() {
        log.info("Obteniendo todos las bodegas...");
        try {
            List<WarehouseItem> warehouses = warehouseService.getWarehouses();
            log.info("Lista de bodegas obtenida exitosamente. Total: {} bodegas.", warehouses.size());
            return ResponseEntity.ok(warehouses);
        } catch (Exception e) {
            log.error("Error al obtener bodegas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<WarehouseItem> addWarehouse(@RequestBody WarehouseCreateRequest warehouseDTO) {
        log.info("Registrando bodega {}", warehouseDTO);
        try {
            WarehouseItem warehouse = warehouseService.createWarehouse(warehouseDTO);
            log.info("Bodega registrada exitosamente: {}", warehouse);
            return ResponseEntity.ok(warehouse);
        } catch (Exception e) {
            log.error("Error al registrar bodega: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
