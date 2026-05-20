package com.e0bmanager.server_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class RevenuechartDTO {
        private String ngay;         // Ngày dạng "dd/MM" để hiển thị trên trục X
        private Double doanhThu;     // Doanh thu của ngày đó
        private Integer soLuongDon;  // Số đơn hàng của ngày đó
    }

