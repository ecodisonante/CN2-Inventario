package com.inventario.repository;

import com.inventario.model.Warehouse;

import java.sql.*;

public class WarehouseRepository {

  public long insert(Connection c, Warehouse w) throws SQLException {
    String sql = """
        INSERT INTO WAREHOUSES (NAME, LOCATION, ENABLED, CREATED_AT)
        VALUES (?, ?, ?, ?)
        """;

    try (PreparedStatement ps = c.prepareStatement(sql, new String[] { "ID" })) {
      ps.setString(1, w.getName());
      ps.setString(2, w.getLocation());
      ps.setString(3, w.getEnabled());
      ps.setTimestamp(4, w.getCreatedAt());

      ps.executeUpdate();
      
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next())
          return rs.getLong(1);
        throw new SQLException("Campo ID no generado");
      }
    }
  }

  public Warehouse findById(Connection c, long id) throws SQLException {
    String sql = """
        SELECT ID, NAME, LOCATION, ENABLED, CREATED_AT
        FROM WAREHOUSES WHERE ID = ?
        """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
          Warehouse w = new Warehouse();
          w.setId(rs.getLong("ID"));
          w.setName(rs.getString("NAME"));
          w.setLocation(rs.getString("LOCATION"));
          w.setEnabled(rs.getString("ENABLED"));
          w.setCreatedAt(rs.getTimestamp("CREATED_AT"));

          return w;
        }

        return null;
      }
    }
  }
}
