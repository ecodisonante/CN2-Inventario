package com.inventario.service;

import com.inventario.dto.WarehouseRequest;
import com.inventario.dto.WarehouseResponse;
import com.inventario.mapper.WarehouseMapper;
import com.inventario.model.Warehouse;
import com.inventario.repository.Db;
import com.inventario.repository.WarehouseRepository;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

public class WarehouseService {
  private final WarehouseRepository repo = new WarehouseRepository();

  public WarehouseResponse create(WarehouseRequest req) throws Exception {
    validate(req);

    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      Warehouse w = WarehouseMapper.toModel(req);

      w.setCreatedAt(new Timestamp(System.currentTimeMillis()));
      w.setId(repo.insert(c, w));

      c.commit();

      return WarehouseMapper.toResponse(w);
    }
  }

  public List<WarehouseResponse> getAll() throws Exception {
    try (Connection c = Db.open()) {
      List<Warehouse> result = repo.findAll(c);

      return result.stream()
          .map(WarehouseMapper::toResponse)
          .toList();
    }
  }

  public WarehouseResponse getById(long id) throws Exception {
    try (Connection c = Db.open()) {
      return WarehouseMapper.toResponse(repo.findById(c, id));
    }
  }

  public void delete(long id) throws Exception {
    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      repo.delete(c, id);

      c.commit();
    }
  }

  public WarehouseResponse update(long id, WarehouseRequest req) throws Exception {
    validate(req);

    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      Warehouse w = WarehouseMapper.toModel(req);
      w.setId(id);

      repo.update(c, id, w);

      c.commit();

      // Obtener el registro actualizado para retornar
      return WarehouseMapper.toResponse(repo.findById(c, id));
    }
  }

  private void validate(WarehouseRequest req) {
    if (req == null || req.name() == null || req.name().isBlank())
      throw new IllegalArgumentException("name is required");
    if (req.enabled() != null && !req.enabled().isBlank()
        && !("S".equalsIgnoreCase(req.enabled()) || "N".equalsIgnoreCase(req.enabled())))
      throw new IllegalArgumentException("enabled must be 'S' or 'N'");
  }
}
