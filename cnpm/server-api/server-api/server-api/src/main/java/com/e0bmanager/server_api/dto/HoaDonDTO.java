package com.e0bmanager.server_api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HoaDonDTO {
    private Integer id;
    private int banId;
    private String maHoaDon; // Nếu bạn có mã này trong DB
    private Double tongTien;
    private String tenBan;    // Lấy thông qua JOIN từ bảng BanAn
    private LocalDateTime ngayTao;


    // Getters và Setters
}