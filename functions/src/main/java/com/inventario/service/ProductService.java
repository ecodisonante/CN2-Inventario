package com.inventario.service;

import com.inventario.dto.ProductRequest;
import com.inventario.dto.ProductResponse;
import com.inventario.dto.WarehouseResponse;
import com.inventario.mapper.ProductMapper;
import com.inventario.model.Product;
import com.inventario.repository.Db;
import com.inventario.repository.ProductRepository;
import com.inventario.events.CrudAction;
import com.inventario.events.EventGridPublisherFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ProductService {
  private final ProductRepository repo = new ProductRepository();
  private final WarehouseService warehouseService = new WarehouseService();
  private final StockService stockService = new StockService();
  private static final String ENTITY = "Product";

  public ProductResponse create(ProductRequest req) throws SQLException {
    validate(req);

    try (Connection c = Db.open()) {
      c.setAutoCommit(false);
      
      Product p = ProductMapper.toModel(req);

      p.setCreatedAt(new Timestamp(System.currentTimeMillis()));
      p.setId(repo.insert(c, p));
      c.commit();

      // Asignar bodega y stock
      WarehouseResponse warehouse = null;
      if (req.warehouseId() != null) {
        warehouse = warehouseService.getById(req.warehouseId());
      } 
      
      if (warehouse == null || req.warehouseId() == null) {
        warehouse = warehouseService.findPrimary();
      }

      int qty = req.onHand() != null ? req.onHand() : 0;

      stockService.receive(p.getId(), warehouse.id(), qty, "Stock inicial");

      var response = ProductMapper.toResponse(p);

      // Enviar notificacion
      EventGridPublisherFactory.publishCrud(ENTITY, CrudAction.CREATED, String.valueOf(response.id()), response);

      return response;
    }
  }

  public ProductResponse getById(Long id) throws SQLException {
    try (Connection conn = Db.open()) {
      return ProductMapper.toResponse(repo.findById(conn, id));
    }
  }

  public List<ProductResponse> findByIds(List<Long> ids) throws SQLException {
    try (Connection c = Db.open()) {
      return repo.findByIds(c, ids).stream()
          .map(ProductMapper::toResponse)
          .toList();
    }
  }

  public void disableNonStockProducts() throws SQLException {
    try (Connection c = Db.open()) {
      repo.disableNonStockProducts(c);
    }
  }

  public List<ProductResponse> getAll() throws SQLException {
    try (Connection c = Db.open()) {
      List<Product> result = repo.findAll(c);

      return result.stream()
          .map(ProductMapper::toResponse)
          .toList();
    }
  }

  public ProductResponse update(long id, ProductRequest req) throws SQLException {
    validate(req);

    try (Connection conn = Db.open()) {
      conn.setAutoCommit(false);

      Product p = ProductMapper.toModel(req);
      p.setId(id);
      repo.update(conn, p);
      conn.commit();

      var response = ProductMapper.toResponse(p);

      // Enviar notificacion
      EventGridPublisherFactory.publishCrud(ENTITY, CrudAction.UPDATED, String.valueOf(response.id()), response);

      return response;
    }
  }

  public void delete(long id) throws SQLException {
    try (Connection conn = Db.open()) {
      conn.setAutoCommit(false);
      var toDelete = ProductMapper.toResponse(repo.findById(conn, id));
      stockService.deleteProductStock(id);
      repo.delete(conn, id);
      conn.commit();

      // Enviar notificacion
      EventGridPublisherFactory.publishCrud(ENTITY, CrudAction.DELETED, String.valueOf(toDelete.id()), toDelete);
    }
  }

  private void validate(ProductRequest req) {
    if (req == null || req.name() == null || req.name().isBlank())
      throw new IllegalArgumentException("name is required");
    if (req.price() == null || req.price().compareTo(BigDecimal.ZERO) < 0)
      throw new IllegalArgumentException("price must be >= 0");
    if (req.enabled() != null && !req.enabled().isBlank()
        && !("S".equalsIgnoreCase(req.enabled()) || "N".equalsIgnoreCase(req.enabled())))
      throw new IllegalArgumentException("enabled must be 'S' or 'N'");
  }
}
