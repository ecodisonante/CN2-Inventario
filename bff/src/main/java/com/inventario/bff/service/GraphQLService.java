package com.inventario.bff.service;

import com.inventario.bff.dto.GraphQLRequest;

public interface GraphQLService {
    Object callGraphQLService(GraphQLRequest request);
}
