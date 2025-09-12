package com.inventario.repository;

import com.inventario.model.Stock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockRepository {

  public Stock receive(Connection conn, long productId, long warehouseId, int qty, String reference)
      throws SQLException {

    // Insertar movimiento
    String query = """
        INSERT INTO STOCK_MOVEMENTS (PRODUCT_ID, WAREHOUSE_ID, MOV_TYPE_ID, QTY, REFERENCE, CREATED_AT)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    try (PreparedStatement ps = conn.prepareStatement(query)) {

      ps.setLong(1, productId);
      ps.setLong(2, warehouseId);
      ps.setInt(3, 1); // MOVEMENT_TYPE = RECEIVE
      ps.setInt(4, qty);
      ps.setString(5, reference);
      ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

      ps.executeUpdate();
    }

    // Actualizar stock (UPSERT)
    String sql = """
          MERGE INTO STOCKS s
          USING (SELECT ? AS PRODUCT_ID, ? AS WAREHOUSE_ID FROM dual) i
             ON (s.PRODUCT_ID = i.PRODUCT_ID AND s.WAREHOUSE_ID = i.WAREHOUSE_ID)
          WHEN MATCHED THEN
            UPDATE SET s.ON_HAND = s.ON_HAND + ?, s.UPDATED_AT = SYSTIMESTAMP
          WHEN NOT MATCHED THEN
            INSERT (PRODUCT_ID, WAREHOUSE_ID, ON_HAND, RESERVED, UPDATED_AT)
            VALUES (i.PRODUCT_ID, i.WAREHOUSE_ID, ?, 0, SYSTIMESTAMP)
        """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, productId);
      ps.setLong(2, warehouseId);
      ps.setInt(3, qty); // suma qty en caso de UPDATE
      ps.setInt(4, qty); // valor unico en caso de INSERT
      ps.executeUpdate();
    }

    // Devolver stock actualizado
    var result = findPaged(conn, productId, warehouseId, 1, 0).get(0);

    return result;
  }

  // Filtrado opcional por producto y/o bodega con paginación
  public List<Stock> findPaged(Connection c, Long productId, Long warehouseId, Integer limit, int offset)
      throws SQLException {
    StringBuilder sql = new StringBuilder("""
        SELECT PRODUCT_ID, WAREHOUSE_ID, ON_HAND, RESERVED, UPDATED_AT
        FROM STOCKS
        WHERE 1=1
        """);

    List<Object> params = new ArrayList<>();

    if (productId != null) {
      sql.append(" AND PRODUCT_ID = ?");
      params.add(productId);
    }

    if (warehouseId != null) {
      sql.append(" AND WAREHOUSE_ID = ?");
      params.add(warehouseId);
    }

    String orderBy = " ORDER BY PRODUCT_ID, WAREHOUSE_ID ";
    // Orden estable para paginar
    sql.append(orderBy);

    // Paginación
    if (limit != null) {
      sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
    }

    try (PreparedStatement ps = c.prepareStatement(sql.toString())) {
      int idx = 1;
      for (Object p : params)
        ps.setObject(idx++, p);

      if (limit != null) {
        ps.setInt(idx++, offset);
        ps.setInt(idx, limit);
      }

      try (ResultSet rs = ps.executeQuery()) {
        List<Stock> out = new ArrayList<>();
        while (rs.next())
          out.add(map(rs));
        return out;
      }
    }
  }

  public List<Stock> findAll(Connection c) throws SQLException {
    return findPaged(c, null, null, null, 0);
  }

  public List<Stock> findByWarehouse(Connection c, long warehouseId, Integer limit, int offset) throws SQLException {
    return findPaged(c, null, warehouseId, limit, offset);
  }

  public List<Stock> findByProduct(Connection c, long productId) throws SQLException {
    return findPaged(c, productId, null, null, 0);
  }

  public void update(Connection c, long productId, long warehouseId, Stock s) throws SQLException {
    String sql = """
        UPDATE STOCKS SET
          ON_HAND = ?,
          RESERVED = ?,
          UPDATED_AT = ?
        WHERE
          PRODUCT_ID = ? AND
          WAREHOUSE_ID = ?
        """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, s.getOnHand());
      ps.setInt(2, s.getReserved());
      ps.setTimestamp(3, s.getUpdatedAt());
      ps.setLong(4, productId);
      ps.setLong(5, warehouseId);

      ps.executeUpdate();
    }
  }

  public void delete(Connection c, long productId, long warehouseId) throws SQLException {
    String sql = "DELETE FROM STOCKS WHERE PRODUCT_ID = ? AND WAREHOUSE_ID = ?";
    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, productId);
      ps.setLong(2, warehouseId);
      ps.executeUpdate();
    }
  }

  private Stock map(ResultSet rs) throws SQLException {
    Stock s = new Stock();
    s.setProductId(rs.getLong("PRODUCT_ID"));
    s.setWarehouseId(rs.getLong("WAREHOUSE_ID"));
    s.setOnHand(rs.getInt("ON_HAND"));
    s.setReserved(rs.getInt("RESERVED"));
    s.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

    return s;
  }
}
