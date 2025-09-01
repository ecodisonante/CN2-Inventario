package com.inventario.bff.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.bff.dto.WarehouseCreateRequest;
import com.inventario.bff.dto.WarehouseItem;
import com.inventario.bff.util.Json;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final RestTemplate restTemplate;

    @Value("${api.warehouses:}")
    private String domain;

    @Autowired
    public WarehouseServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<WarehouseItem> getWarehouses() {
        String url = domain;
        ResponseEntity<List<WarehouseItem>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public WarehouseItem getWarehouseById(long id) {
        // Implementación del método para obtener un almacén por ID
        return null;
    }

    @Override
    public WarehouseItem createWarehouse(WarehouseCreateRequest warehouse) {
        String url = domain;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonRequest;
        try {
            jsonRequest = Json.write(warehouse);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir el objeto a JSON", e);
            return null;
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

        ResponseEntity<WarehouseItem> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                WarehouseItem.class);
        return response.getBody();
    }

    @Override
    public WarehouseItem updateWarehouse(long id, WarehouseCreateRequest warehouse) {
        // TODO: Implementación del método para actualizar un almacén existente
        return null;
    }

    @Override
    public void deleteWarehouse(long id) {
        // TODO: Implementación del método para eliminar un almacén por ID
    }
}
