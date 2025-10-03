package com.inventario.repository;

import com.inventario.model.Contact;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactRepository {

  public long insert(Connection c, Contact ct) throws SQLException {
    String sql = """
        INSERT INTO CONTACTS (NAME, EMAIL, ADDRESS, PHONE, ENABLED, CREATED_AT)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    try (PreparedStatement ps = c.prepareStatement(sql, new String[] { "ID" })) {
      ps.setString(1, ct.getName());
      ps.setString(2, ct.getEmail());
      ps.setString(3, ct.getAddress());
      ps.setString(4, ct.getPhone());
      ps.setString(5, ct.getEnabled());
      ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next())
          return rs.getLong(1);
        throw new SQLException("Campo ID no generado");
      }
    }
  }

  public long addContactToWarehouse(Connection c, long warehouseId, long contactId) throws SQLException {
    String sql = """
        INSERT INTO WAREHOUSE_CONTACTS (WAREHOUSE_ID, CONTACT_ID, ISADMIN, CREATED_AT)
        VALUES (?, ?, 'N', ?)
        """;

    try (PreparedStatement ps = c.prepareStatement(sql, new String[] { "ID" })) {
      ps.setLong(1, warehouseId);
      ps.setLong(2, contactId);
      ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next())
          return rs.getLong(1);
        throw new SQLException("Campo ID no generado");
      }
    }
  }

  public Contact findById(Connection c, long id) throws SQLException {
    String sql = """
        SELECT ID, NAME, EMAIL, ADDRESS, PHONE, ENABLED, CREATED_AT
        FROM CONTACTS WHERE ID = ?
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

  public Contact findWarehouseAdmin(Connection c, long warehouseId) throws SQLException {
    String sql = """
        SELECT C.ID, C.NAME, C.EMAIL, C.ADDRESS, C.PHONE, C.ENABLED, C.CREATED_AT
        FROM CONTACTS C
        JOIN WAREHOUSE_CONTACTS WC ON C.ID = WC.CONTACT_ID
        WHERE WC.WAREHOUSE_ID = ? AND WC.ISADMIN = 'S'
        """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, warehouseId);
      try (ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
          return map(rs);
        }

        return null;
      }
    }
  }

  public List<Contact> findByWarehouse(Connection c, long warehouseId) throws SQLException {
    String sql = """
        SELECT C.ID, C.NAME, C.EMAIL, C.ADDRESS, C.PHONE, C.ENABLED, C.CREATED_AT
        FROM CONTACTS C
        JOIN WAREHOUSE_CONTACTS WC ON C.ID = WC.CONTACT_ID
        WHERE WAREHOUSE_ID = ?
        """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, warehouseId);
      try (ResultSet rs = ps.executeQuery()) {

        List<Contact> out = new ArrayList<>();

        while (rs.next()) {
          out.add(map(rs));
        }

        return out;
      }
    }
  }


  public List<Contact> findAll(Connection c) throws SQLException {
    String sql = """
        SELECT ID, NAME, EMAIL, ADDRESS, PHONE, ENABLED, CREATED_AT
        FROM CONTACTS ORDER BY ID
        """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
      try (ResultSet rs = ps.executeQuery()) {

        List<Contact> out = new ArrayList<>();

        while (rs.next()) {
          out.add(map(rs));
        }

        return out;
      }
    }
  }

  public void update(Connection c, long id, Contact ct) throws SQLException {
    String sql = """
        UPDATE CONTACTS
        SET NAME = ?, EMAIL = ?, ADDRESS = ?, PHONE = ?, ENABLED = ?
        WHERE ID = ?
        """;
    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, ct.getName());
      ps.setString(2, ct.getEmail());
      ps.setString(3, ct.getAddress());
      ps.setString(4, ct.getPhone());
      ps.setString(5, ct.getEnabled());
      ps.setLong(6, id);
      ps.executeUpdate();
    }
  }

  /**
   * Elimina los contactos de una bodega
   * Si contactId es null, elimina todos los contactos de la bodega
   * Si contactId no es null, elimina solo el contacto especificado
   */
  public void deleteWarehouseContacts(Connection c, long warehouseId, Long contactId) throws SQLException {
    String sql = "DELETE FROM WAREHOUSE_CONTACTS WHERE WAREHOUSE_ID = ?";
    
    if (contactId != null) {
      sql += " AND CONTACT_ID = ?";
    }

    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, warehouseId);
      if (contactId != null) {
        ps.setLong(2, contactId);
      }
      ps.executeUpdate();
    }
  }
  
  public void delete(Connection c, long id) throws SQLException {
    String sql = "DELETE FROM CONTACTS WHERE ID = ?";
    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.executeUpdate();
    }
  }

  private Contact map(ResultSet rs) throws SQLException {
    Contact ct = new Contact();
    ct.setId(rs.getLong("ID"));
    ct.setName(rs.getString("NAME"));
    ct.setEmail(rs.getString("EMAIL"));
    ct.setAddress(rs.getString("ADDRESS"));
    ct.setPhone(rs.getString("PHONE"));
    ct.setEnabled(rs.getString("ENABLED"));
    ct.setCreatedAt(rs.getTimestamp("CREATED_AT"));
    return ct;
  }
}
