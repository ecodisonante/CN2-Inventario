package com.inventario.bff.controller;

import com.inventario.bff.dto.GraphQLRequest;
import com.inventario.bff.service.GraphQLService;

import lombok.extern.log4j.Log4j2;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/graphql")
public class GraphQLController {

    private final GraphQLService graphQLService;

    @Autowired
    public GraphQLController(GraphQLService graphQLService) {
        this.graphQLService = graphQLService;
    }

    @PostMapping
    public ResponseEntity<Object> graphql(@RequestBody GraphQLRequest req) {
        log.info("Procesando peticion GraphQL: {}", req.query());
        try {
            Object result = graphQLService.callGraphQLService(req);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error al procesar la peticion GraphQL: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
