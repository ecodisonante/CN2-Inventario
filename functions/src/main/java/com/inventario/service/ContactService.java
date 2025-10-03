package com.inventario.service;

import com.inventario.model.Contact;
import com.inventario.repository.Db;
import com.inventario.repository.ContactRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ContactService {
  private final ContactRepository repo = new ContactRepository();

  public Contact create(Contact req) throws SQLException {
    validate(req);

    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      req.setCreatedAt(new Timestamp(System.currentTimeMillis()));
      req.setId(repo.insert(c, req));

      c.commit();

      return req;
    }
  }

  public Contact addContactToWarehouse(long warehouseId, long contactId) throws SQLException {
    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      repo.addContactToWarehouse(c, warehouseId, contactId);

      c.commit();

      return repo.findById(c, contactId);
    }
  }


  
  public Contact getById(long id) throws SQLException {
    try (Connection c = Db.open()) {
      return repo.findById(c, id);
    }
  }

  public Contact findWarehouseAdmin(long warehouseId) throws SQLException {
    try (Connection c = Db.open()) {
      return repo.findWarehouseAdmin(c, warehouseId);
    }
  }

  public List<Contact> getAll() throws SQLException {
    try (Connection c = Db.open()) {
      return repo.findAll(c);
    }
  }

  public List<Contact> findByWarehouse(long warehouseId) throws SQLException {
    try (Connection c = Db.open()) {
      return repo.findByWarehouse(c, warehouseId);
    }
  }


  public Contact update(long id, Contact req) throws SQLException {
    validate(req);

    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      req.setId(id);

      repo.update(c, id, req);

      c.commit();

      // Obtener el registro actualizado para retornar
      return repo.findById(c, id);
    }
  }

  public void deleteWarehouseContacts(long warehouseId, Long contactId) throws SQLException {
    try (Connection c = Db.open()) {
      c.setAutoCommit(false);
      repo.deleteWarehouseContacts(c, warehouseId, contactId);
      c.commit();
    }
  }

  public void delete(long id) throws SQLException {
    try (Connection c = Db.open()) {
      c.setAutoCommit(false);
      repo.delete(c, id);
      c.commit();
    }
  }

  private void validate(Contact req) {
    if (req == null || req.getName() == null || req.getName().isBlank())
      throw new IllegalArgumentException("name is required");
    if (req.getEnabled() != null && !req.getEnabled().isBlank()
        && !("S".equalsIgnoreCase(req.getEnabled()) || "N".equalsIgnoreCase(req.getEnabled())))
      throw new IllegalArgumentException("enabled must be 'S' or 'N'");
    if (req.getEmail() == null || req.getEmail().isBlank())
      throw new IllegalArgumentException("email is required");
  }
}
