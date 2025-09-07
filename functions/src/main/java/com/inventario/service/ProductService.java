package com.inventario.service;

import com.inventario.dto.ProductRequest;
import com.inventario.dto.ProductResponse;
import com.inventario.mapper.ProductMapper;
import com.inventario.model.Product;
import com.inventario.repository.Db;
import com.inventario.repository.ProductRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ProductService {
  private final ProductRepository repo = new ProductRepository();

  public ProductResponse create(ProductRequest req) throws SQLException {
    validate(req);

    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      Product p = ProductMapper.toModel(req);

      p.setCreatedAt(new Timestamp(System.currentTimeMillis()));
      p.setId(repo.insert(c, p));

      c.commit();

      return ProductMapper.toResponse(p);
    }
  }

  public ProductResponse getById(Long id) throws SQLException {
    try (Connection conn = Db.open()) {
      return ProductMapper.toResponse(repo.findById(conn, id));
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

      return ProductMapper.toResponse(repo.findById(conn, id));
    }
  }

  public void delete(long id) throws SQLException {
    try (Connection conn = Db.open()) {
      conn.setAutoCommit(false);

      repo.delete(conn, id);

      conn.commit();
    }
  }

  private void validate(ProductRequest req) {
    if (req == null || req.name() == null || req.name().isBlank())
      throw new IllegalArgumentException("name is required");
    if (req.price() == null || req.price().compareTo(BigDecimal.ZERO) < 0)
      throw new IllegalArgumentException("price must be >= 0");
    if (req.warehouseId() == null)
      throw new IllegalArgumentException("warehouseId is required");
    if (req.enabled() != null && !req.enabled().isBlank()
        && !("S".equalsIgnoreCase(req.enabled()) || "N".equalsIgnoreCase(req.enabled())))
      throw new IllegalArgumentException("enabled must be 'S' or 'N'");
  }
}
