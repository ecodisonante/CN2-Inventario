package com.inventario.bff.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inventario.bff.dto.WarehouseRequest;
import com.inventario.bff.dto.WarehouseResponse;
import com.inventario.bff.util.Json;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final RestTemplate restTemplate;

    @Value("${azure.functions.url}")
    private String baseUrl;
    private static final String API_PATH = "/api/warehouses";

    public WarehouseServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<WarehouseResponse> getWarehouses() {
        String url = baseUrl + API_PATH;
        WarehouseResponse[] response = restTemplate.getForObject(url, WarehouseResponse[].class);
        return Arrays.asList(response);
    }

    @Override
    public WarehouseResponse getWarehouseById(long id) {
        String url = baseUrl + API_PATH + "/" + id;
        return restTemplate.getForObject(url, WarehouseResponse.class);
    }

    @Override
    public WarehouseResponse createWarehouse(WarehouseRequest warehouse) {
        String url = baseUrl + API_PATH;
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

        ResponseEntity<WarehouseResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                WarehouseResponse.class);
        return response.getBody();
    }

    @Override
    public WarehouseResponse updateWarehouse(long id, WarehouseRequest warehouse) {
        String url = baseUrl + API_PATH + "/" + id;
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

        return restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                WarehouseResponse.class).getBody();
    }

    @Override
    public void deleteWarehouse(long id) {
        String url = baseUrl + API_PATH + "/" + id;
        restTemplate.delete(url);
    }
}
