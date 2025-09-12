package com.inventario.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.inventario.dto.ErrorDto;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HttpUtils {

    /**
     * Get id from path, if exists and is numeric.
     * 
     * @param req
     * @return
     */
    public static Long getIdFromQueryString(HttpRequestMessage<?> req) {
        String path = req.getUri().getPath();
        if (path != null) {
            String[] parts = path.split("/");
            if (parts.length > 0) {
                String last = parts[parts.length - 1];
                if (!last.isBlank() && last.chars().allMatch(Character::isDigit)) {
                    return Long.parseLong(last);
                }
            }
        }
        return null;
    }

    public static HttpResponseMessage createMessage(HttpRequestMessage<?> req, Object content, int status) {
        String message;
        try {
            // Intentar serializar para verificar que es JSON serializable
            message = Json.write(content);
        } catch (JsonProcessingException e) {
            // Si no se puede serializar, devolver error 500
            return req.createResponseBuilder(com.microsoft.azure.functions.HttpStatus.valueOf(500))
                    .header("Content-Type", "application/json")
                    .body(" {\"error\":\"Error interno del servidor: " + e.getMessage().replace("\"", "'") + "\"} ")
                    .build();
        }

        return req.createResponseBuilder(com.microsoft.azure.functions.HttpStatus.valueOf(status))
                .header("Content-Type", "application/json")
                .body(message)
                .build();
    }

    public static HttpResponseMessage notFoundMessage(HttpRequestMessage<?> req, String entity) {
        return createMessage(req, new ErrorDto(entity + " no encontrado"), 404);
    }

    public static HttpResponseMessage badRequestMessage(HttpRequestMessage<?> req, String message) {
        return createMessage(req, new ErrorDto(message), 400);
    }

    public static HttpResponseMessage internalErrorMessage(HttpRequestMessage<?> req, String message) {
        return createMessage(req, new ErrorDto(message), 500);
    }
}
