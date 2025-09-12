package com.inventario.bff.service;

import com.inventario.bff.dto.ProductRequest;
import com.inventario.bff.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts();

    ProductResponse getProductById(long id);

    ProductResponse createProduct(ProductRequest product);

    ProductResponse updateProduct(long id, ProductRequest product);

    void deleteProduct(long id);
}
