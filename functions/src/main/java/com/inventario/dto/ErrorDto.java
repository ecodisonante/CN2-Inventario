package com.inventario.dto;

public class ErrorDto {
    private String error;

    public ErrorDto(String message) {
        this.error = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String message) {
        this.error = message;
    }
}
