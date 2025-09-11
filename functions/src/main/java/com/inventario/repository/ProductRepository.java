package com.inventario.repository;

import com.inventario.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

  public long insert(Connection c, Product p) throws SQLException {
    String sql = """
        INSERT INTO PRODUCTS (SKU, NAME, CATEGORY, PRICE, ENABLED, WAREHOUSE_ID, CREATED_AT)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

    try (PreparedStatement ps = c.prepareStatement(sql, new String[] { "ID" })) {
      ps.setString(1, p.getSku());
      ps.setString(2, p.getName());
      ps.setString(3, p.getCategory());
      ps.setBigDecimal(4, p.getPrice());
      ps.setString(5, p.getEnabled());
      ps.setLong(6, p.getWarehouseId());
      ps.setTimestamp(7, p.getCreatedAt());

      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next())
          return rs.getLong(1);
        throw new SQLException("Campo ID no generado");
      }
    }
  }

  public Product findById(Connection c, long id) throws SQLException {
    String sql = """
        SELECT ID, SKU, NAME, CATEGORY, PRICE, ENABLED, WAREHOUSE_ID, CREATED_AT
        FROM PRODUCTS WHERE ID = ?
        """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next())
          return map(rs);
        return null;
      }
    }
  }

  public List<Product> findByIds(Connection conn, List<Long> ids) throws SQLException {
    if (ids == null || ids.isEmpty())
      return List.of();

    // generar query con placeholders
    String placeholders = String.join(", ", ids.stream().map(i -> "?").toList());
    String q = "SELECT ID, SKU, NAME, CATEGORY, PRICE, ENABLED, WAREHOUSE_ID, CREATED_AT FROM PRODUCTS WHERE ID IN ("
        + placeholders + ")";

    try (PreparedStatement ps = conn.prepareStatement(q)) {
      int idx = 1;

      // setear valores de los placeholders
      for (Long id : ids)
        ps.setLong(idx++, id);

      try (ResultSet rs = ps.executeQuery()) {
        List<Product> out = new ArrayList<>();
        while (rs.next())
          out.add(map(rs));
        return out;
      }
    }
  }

  public List<Product> findAll(Connection c) throws SQLException {
    String sql = """
        SELECT ID, SKU, NAME, CATEGORY, PRICE, ENABLED, WAREHOUSE_ID, CREATED_AT
        FROM PRODUCTS ORDER BY ID
        """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
      try (ResultSet rs = ps.executeQuery()) {

        List<Product> out = new ArrayList<>();

        while (rs.next()) {
          out.add(map(rs));
        }

        return out;
      }
    }
  }

  public Product update(Connection conn, Product p) throws SQLException {

    String query = """
        UPDATE PRODUCTS SET
        sku=?,
        name=?,
        category=?,
        price=?,
        enabled=?,
        warehouse_id=?
        WHERE id=?
        """;

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, p.getSku());
      ps.setString(2, p.getName());
      ps.setString(3, p.getCategory());
      ps.setBigDecimal(4, p.getPrice());
      ps.setString(5, p.getEnabled());
      ps.setLong(6, p.getWarehouseId());
      ps.setLong(7, p.getId());

      int rows = ps.executeUpdate();
      return rows > 0 ? p : null;
    }
  }

  public int delete(Connection conn, Long id) throws SQLException {

    String query = "DELETE FROM PRODUCTS WHERE id=?";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setLong(1, id);
      return ps.executeUpdate();
    }
  }

  private Product map(ResultSet rs) throws SQLException {
    Product p = new Product();
    p.setId(rs.getLong("ID"));
    p.setSku(rs.getString("SKU"));
    p.setName(rs.getString("NAME"));
    p.setCategory(rs.getString("CATEGORY"));
    p.setPrice(rs.getBigDecimal("PRICE"));
    p.setEnabled(rs.getString("ENABLED"));
    p.setWarehouseId(rs.getLong("WAREHOUSE_ID"));
    p.setCreatedAt(rs.getTimestamp("CREATED_AT"));
    return p;
  }
}
