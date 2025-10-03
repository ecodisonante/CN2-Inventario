package com.inventario.service;

import com.inventario.dto.NotificationDto;
import com.inventario.dto.StockResponse;
import com.inventario.dto.WarehouseRequest;
import com.inventario.dto.WarehouseResponse;
import com.inventario.events.CrudAction;
import com.inventario.events.EventGridPublisherFactory;
import com.inventario.mapper.WarehouseMapper;
import com.inventario.model.Warehouse;
import com.inventario.repository.Db;
import com.inventario.repository.WarehouseRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WarehouseService {
  private static final Logger log = LogManager.getLogger(WarehouseService.class);

  private final WarehouseRepository repo = new WarehouseRepository();
  private static final String ENTITY = "Warehouse";

  private static final ContactService contactService = new ContactService();
  private static final ProductService productService = new ProductService();
  private static final StockService stockService = new StockService();

  public WarehouseResponse create(WarehouseRequest req) throws SQLException {
    validate(req);

    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      Warehouse w = WarehouseMapper.toModel(req);

      w.setCreatedAt(new Timestamp(System.currentTimeMillis()));
      w.setId(repo.insert(c, w));
      c.commit();

      var response = WarehouseMapper.toResponse(w);

      // Enviar notificacion
      EventGridPublisherFactory.publishCrud(ENTITY, CrudAction.CREATED, String.valueOf(response.id()), response);

      return response;
    }
  }

  public WarehouseResponse getById(long id) throws SQLException {
    try (Connection c = Db.open()) {
      return WarehouseMapper.toResponse(repo.findById(c, id));
    }
  }

  public List<WarehouseResponse> findByIds(List<Long> ids) throws SQLException {
    try (Connection c = Db.open()) {
      return repo.findByIds(c, ids).stream()
          .map(WarehouseMapper::toResponse)
          .toList();
    }
  }

  public WarehouseResponse findPrimary() throws SQLException {
    try (Connection c = Db.open()) {
      return WarehouseMapper.toResponse(repo.findPrimary(c));
    }
  }

  public List<WarehouseResponse> getAll() throws SQLException {
    try (Connection c = Db.open()) {
      List<Warehouse> result = repo.findAll(c);

      return result.stream()
          .map(WarehouseMapper::toResponse)
          .toList();
    }
  }

  public WarehouseResponse update(long id, WarehouseRequest req) throws SQLException {
    validate(req);

    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      Warehouse w = WarehouseMapper.toModel(req);
      w.setId(id);

      repo.update(c, id, w);
      c.commit();

      var response = WarehouseMapper.toResponse(w);

      // Enviar notificacion
      EventGridPublisherFactory.publishCrud(ENTITY, CrudAction.UPDATED, String.valueOf(response.id()), response);

      return response;
    }
  }

  public void prepareToDelete(long id) throws SQLException {
    try (Connection c = Db.open()) {

      // Obtener datos antes de eliminar
      var toDelete = WarehouseMapper.toResponse(repo.findById(c, id));

      // Generar evento
      EventGridPublisherFactory.publishCrud(ENTITY, CrudAction.DELETING, String.valueOf(toDelete.id()), toDelete);
    }
  }

  public void delete(long id) throws SQLException {
    try (Connection c = Db.open()) {
      c.setAutoCommit(false);

      repo.delete(c, id);

      c.commit();
    }
  }

  public void complexDelete(long warehouseId) throws SQLException {

    var warehouse = getById(warehouseId);
    if (warehouse == null) {
      log.error("Bodega no encontrada para id: {}", warehouseId);
      throw new IllegalArgumentException("Bodega no encontrada");
    }

    // obtener contactos de la bodega
    var admin = contactService.findWarehouseAdmin(warehouseId);
    if (admin == null) {
      admin = contactService.findWarehouseAdmin(findPrimary().id());
    }

    var contacts = contactService.findByWarehouse(warehouseId);

    log.info("Eliminando contactos de la bodega id: {}", warehouseId);
    contactService.deleteWarehouseContacts(warehouseId, null);

    // obtener stocks de la bodega
    var stocks = stockService.getByWarehouse(warehouseId, null, 0);

    log.info("Eliminando stock de la bodega id: {}", warehouseId);
    stockService.deleteWarehouseStock(warehouseId);

    // obtener productos de la bodega
    List<Long> productIds = stocks.stream().map(StockResponse::productId).toList();
    var products = productService.findByIds(productIds);

    log.info("Deshabilitando productos sin stock de la bodega id: {}", warehouseId);
    productService.disableNonStockProducts();

    // eliminar bodega
    log.info("Eliminando bodega id: {}", warehouseId);
    delete(warehouseId);

    if (admin == null) {
      log.warn("No se encontro administrador para la bodega id: {}", warehouseId);
      return;
    }

    var dto = new NotificationDto();
    dto.setWarehouseId(warehouseId);
    dto.setWarehouseName(warehouse.name());
    dto.setAdminName(admin.getName());
    dto.setAdminEmail(admin.getEmail());
    dto.setDeletedContacts(contacts.size());
    dto.setDeletedStock(stocks.size());
    dto.setDisabledProducts(products.size());

    // Enviar notificacion
    EventGridPublisherFactory.publishCrud(ENTITY, CrudAction.DELETED, String.valueOf(dto.getWarehouseId()), dto);
  }

  private void validate(WarehouseRequest req) {
    if (req == null || req.name() == null || req.name().isBlank())
      throw new IllegalArgumentException("name is required");
    if (req.enabled() != null && !req.enabled().isBlank()
        && !("S".equalsIgnoreCase(req.enabled()) || "N".equalsIgnoreCase(req.enabled())))
      throw new IllegalArgumentException("enabled must be 'S' or 'N'");
  }
}
