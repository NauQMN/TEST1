package com.e0bmanager.server_api.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Integer productId; // MaSanPham (ID món ăn)
    private Integer quantity;  // Số lượng khách gọi
}