package com.inventario.repository;

import com.inventario.model.Warehouse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

  public Warehouse findById(Connection c, long id) throws Exception {
    String sql = """
        SELECT ID, NAME, LOCATION, ENABLED, CREATED_AT
        FROM WAREHOUSES WHERE ID = ?
        """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
          return map(rs);
        }

        return null;
      }
    }
  }

  public List<Warehouse> findAll(Connection c) throws Exception {
    String sql = """
        SELECT ID, NAME, LOCATION, ENABLED, CREATED_AT
        FROM WAREHOUSES ORDER BY ID
        """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
      try (ResultSet rs = ps.executeQuery()) {

        List<Warehouse> out = new ArrayList<>();

        while (rs.next()) {
          out.add(map(rs));
        }

        return out;
      }
    }
  }

  private Warehouse map(ResultSet rs) throws Exception {
    Warehouse w = new Warehouse();
    w.setId(rs.getLong("ID"));
    w.setName(rs.getString("NAME"));
    w.setLocation(rs.getString("LOCATION"));
    w.setEnabled(rs.getString("ENABLED"));
    w.setCreatedAt(rs.getTimestamp("CREATED_AT"));
    return w;
  }
}
