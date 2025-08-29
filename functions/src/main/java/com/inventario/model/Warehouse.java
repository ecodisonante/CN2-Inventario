package com.inventario.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class Warehouse {
    private long id;
    private String name;
    private String location;
    private String enabled;
    private Timestamp createdAt;
}
