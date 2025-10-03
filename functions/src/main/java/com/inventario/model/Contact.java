package com.inventario.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    private long id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String enabled;
    private Timestamp createdAt;
}