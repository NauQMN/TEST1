package com.e0bmanager.server_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BanAnDTO {
    private int id;
    private String ten_ban;
    private int khu_vuc_id;
    private int trang_thai;
    private double tongTien;
    // Trong file BanAnDTO.java
    private Integer hoaDonId; // Hoặc currentHoaDonId tùy bạn đặt tên

    // Getters và Setters
}