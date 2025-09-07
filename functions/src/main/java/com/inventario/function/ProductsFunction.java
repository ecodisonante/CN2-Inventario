package com.inventario.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.dto.ProductRequest;
import com.inventario.service.ProductService;
import com.inventario.util.HttpUtils;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.sql.SQLException;
import java.util.Optional;

public class ProductsFunction {
    private final ProductService srv = new ProductService();
    private static final ObjectMapper om = new ObjectMapper();

    @FunctionName("products")
    public HttpResponseMessage handle(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
                    HttpMethod.DELETE }, route = "products/{id?}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> req,
            final ExecutionContext ctx) {

        try {
            // Obtener ID desde path
            Long id = HttpUtils.getIdFromQueryString(req);
            HttpResponseMessage response;

            switch (req.getHttpMethod()) {
                case POST -> response = handlePost(req);
                case GET -> response = handleGet(id, req);
                case PUT -> response = handleUpdate(id, req);
                case DELETE -> response = handleDelete(id, req);
                default -> response = req.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED).build();
            }

            return response;

        } catch (Exception e) {
            ctx.getLogger().log(java.util.logging.Level.SEVERE, "Error al ejecutar ProductsFunction", e);
            return HttpUtils.internalErrorMessage(req, e.getMessage());
        }
    }

    private HttpResponseMessage handlePost(HttpRequestMessage<Optional<String>> req)
            throws JsonProcessingException, SQLException {
        var dto = om.readValue(req.getBody().orElseThrow(), ProductRequest.class);
        var saved = srv.create(dto);

        return HttpUtils.createMessage(req, saved, 201);
    }

    private HttpResponseMessage handleGet(Long id, HttpRequestMessage<Optional<String>> req) throws SQLException {
        if (id != null) {
            var result = srv.getById(id);

            if (result != null) {
                return HttpUtils.createMessage(req, result, 200);
            } else
                return HttpUtils.notFoundMessage(req, "Producto");

        } else {
            return HttpUtils.createMessage(req, srv.getAll(), 200);
        }
    }

    private HttpResponseMessage handleUpdate(Long id, HttpRequestMessage<Optional<String>> req)
            throws JsonProcessingException, SQLException {
        if (id == null) {
            return HttpUtils.badRequestMessage(req, "El ID es requerido para actualizar un producto.");
        }

        var dto = om.readValue(req.getBody().orElseThrow(), ProductRequest.class);
        var updated = srv.update(id, dto);

        if (updated != null) {
            return HttpUtils.createMessage(req, updated, 200);
        } else
            return HttpUtils.notFoundMessage(req, "Producto");
    }

    private HttpResponseMessage handleDelete(Long id, HttpRequestMessage<Optional<String>> req) throws SQLException {
        if (id == null) {
            return HttpUtils.badRequestMessage(req, "El ID es requerido para eliminar un producto.");
        }

        srv.delete(id);
        return HttpUtils.createMessage(req, "{\"eliminado\": " + id + "}", 200);
    }
}
