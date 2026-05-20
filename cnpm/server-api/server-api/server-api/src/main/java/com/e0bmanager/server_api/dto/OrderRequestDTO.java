package com.e0bmanager.server_api.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {
    private Integer tableId;    // ID của bàn
    private Integer staffId;    // ID nhân viên đang thao tác (Lấy từ Session PDA)
    private List<OrderItemDTO> items; // Danh sách các món ăn khách gọi
}