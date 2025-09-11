package com.inventario.service;

import com.inventario.dto.StockRequest;
import com.inventario.dto.StockResponse;
import com.inventario.mapper.StockMapper;
import com.inventario.model.Stock;
import com.inventario.repository.Db;
import com.inventario.repository.StockRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class StockService {
  private final StockRepository repo = new StockRepository();

  public StockResponse create(StockRequest req) throws SQLException {
    validate(req);

    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      Stock p = StockMapper.toModel(req);

      p.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
      repo.insert(c, p);

      c.commit();

      return StockMapper.toResponse(p);
    }
  }

  public List<StockResponse> getStock(Long productId, Long warehouseId, Integer limit, int offset) throws Exception {
    try (Connection conn = Db.open()) {
      List<Stock> result = repo.findPaged(conn, productId, warehouseId, limit, offset);
      return result.stream()
          .map(StockMapper::toResponse)
          .toList();
    }
  }

  public List<StockResponse> getAll() throws SQLException {
    try (Connection c = Db.open()) {
      List<Stock> result = repo.findAll(c);

      return result.stream()
          .map(StockMapper::toResponse)
          .toList();
    }
  }

  public List<StockResponse> getByWarehouse(long warehouseId, Integer limit, int offset) throws SQLException {
    try (Connection c = Db.open()) {
      List<Stock> result = repo.findByWarehouse(c, warehouseId, limit, offset);

      return result.stream()
          .map(StockMapper::toResponse)
          .toList();
    }
  }

  public StockResponse update(long productId, long warehouseId, StockRequest req) throws SQLException {
    validate(req);

    try (Connection conn = Db.open()) {
      conn.setAutoCommit(false);

      Stock s = StockMapper.toModel(req);
      s.setProductId(productId);
      s.setWarehouseId(warehouseId);

      repo.update(conn, productId, warehouseId, s);
      conn.commit();

      return StockMapper.toResponse(repo.findPaged(conn, productId, warehouseId, 1, 0).get(0));
    }
  }

  public void delete(long productId, long warehouseId) throws SQLException {
    try (Connection conn = Db.open()) {
      conn.setAutoCommit(false);

      repo.delete(conn, productId, warehouseId);

      conn.commit();
    }
  }

  private void validate(StockRequest req) {
    if (req.productId() == null || req.productId() <= 0) {
      throw new IllegalArgumentException("Product ID is required");
    }
    if (req.warehouseId() == null || req.warehouseId() <= 0) {
      throw new IllegalArgumentException("Warehouse ID is required");
    }
    if (req.onHand() == null || req.onHand() < 0) {
      throw new IllegalArgumentException("On hand quantity is required");
    }
    if (req.reserved() == null || req.reserved() < 0) {
      throw new IllegalArgumentException("Reserved quantity is required");
    }
  }
}
