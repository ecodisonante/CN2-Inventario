package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long warehouseId;
    private String warehouseName;
    private Long productId;
    private String productName;
    private String adminName;
    private String adminEmail;
    private Integer deletedContacts;
    private Integer deletedStock;
    private Integer disabledProducts;
}
