package com.inventario.bff.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.bff.dto.WarehouseRequest;
import com.inventario.bff.dto.WarehouseResponse;
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

    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAll() {
        log.info("Obteniendo todas las bodegas...");
        try{
            return ResponseEntity.ok(warehouseService.getWarehouses());
        }catch (Exception e){
            log.error("Error al obtener las bodegas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getById(@PathVariable long id) {
        log.info("Obteniendo bodega...");
        try{
            return ResponseEntity.ok(warehouseService.getWarehouseById(id));
        }catch (Exception e){
            log.error("Error al obtener la bodega: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<WarehouseResponse> add(@RequestBody WarehouseRequest warehouseDTO) {
        log.info("Creando bodega...");
        try{
            return ResponseEntity.ok(warehouseService.createWarehouse(warehouseDTO));
        }catch (Exception e){
            log.error("Error al crear la bodega: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> update(@PathVariable long id, @RequestBody WarehouseRequest warehouseDTO) {
        log.info("Actualizando bodega...");
        try{
            return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseDTO));
        }catch (Exception e){
            log.error("Error al actualizar la bodega: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.info("Eliminando bodega...");
        try{
            warehouseService.deleteWarehouse(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            log.error("Error al eliminar la bodega: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
