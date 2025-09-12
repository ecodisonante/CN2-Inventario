package com.inventario.bff.controller;

import com.inventario.bff.dto.ProductRequest;
import com.inventario.bff.dto.ProductResponse;
import com.inventario.bff.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        log.info("Obteniendo todos los productos...");
        try{
            return ResponseEntity.ok(productService.getProducts());
        }catch (Exception e){
            log.error("Error al obtener los productos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable long id) {
        log.info("Obteniendo producto...");
        try{
            return ResponseEntity.ok(productService.getProductById(id));
        }catch (Exception e){
            log.error("Error al obtener el producto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest    product) {
        log.info("Creando producto...");
        try{
            return ResponseEntity.ok(productService.createProduct(product));
        }catch (Exception e){
            log.error("Error al crear el producto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable long id, @RequestBody ProductRequest product) {
        log.info("Actualizando producto...");
        try{
            return ResponseEntity.ok(productService.updateProduct(id, product));
        }catch (Exception e){
            log.error("Error al actualizar el producto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        log.info("Eliminando producto...");
        try{
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            log.error("Error al eliminar el producto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
