package com.inventario.bff.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inventario.bff.dto.GraphQLRequest;
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

@Log4j2
@Service
public class GraphQLServiceImpl implements GraphQLService {

    private final RestTemplate restTemplate;

    @Value("${azure.functions.url}")
    private String baseUrl;
    private static final String API_PATH = "/api/graphql";

    @Autowired
    public GraphQLServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Object callGraphQLService(GraphQLRequest request) {
        String url = baseUrl + API_PATH;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonRequest;
        try {
            jsonRequest = Json.write(request);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir el request a JSON: {}", e.getMessage(), e);
            return null;
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Object.class).getBody();
    }
}
