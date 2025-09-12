package com.inventario.bff.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inventario.bff.dto.ProductRequest;
import com.inventario.bff.dto.ProductResponse;
import com.inventario.bff.util.Json;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService {

    private final RestTemplate restTemplate;

    @Value("${azure.functions.url}")
    private String baseUrl;
    private static final String API_PATH = "/api/products";
    
    @Autowired
    public ProductServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ProductResponse> getProducts() {
        String url = baseUrl + API_PATH;
        ProductResponse[] response = restTemplate.getForObject(url, ProductResponse[].class);
        return Arrays.asList(response);
    }

    @Override
    public ProductResponse   getProductById(long id) {
        String url = baseUrl + API_PATH + "/" + id;
        return restTemplate.getForObject(url, ProductResponse.class);
    }

    @Override
    public ProductResponse createProduct(ProductRequest product) {
        String url = baseUrl + API_PATH;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonRequest;
        try {
            jsonRequest = Json.write(product);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir el request a JSON: {}", e.getMessage(), e);
            return null;
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                ProductResponse.class).getBody();
    }

    @Override
    public ProductResponse updateProduct(long id, ProductRequest product) {
        String url = baseUrl + API_PATH + "/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonRequest;
        try {
            jsonRequest = Json.write(product);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir el request a JSON: {}", e.getMessage(), e);
            return null;
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

        return restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                ProductResponse.class).getBody();
    }

    @Override
    public void deleteProduct(long id) {
        String url = baseUrl + API_PATH + "/" + id;
        restTemplate.delete(url);
    }
}
