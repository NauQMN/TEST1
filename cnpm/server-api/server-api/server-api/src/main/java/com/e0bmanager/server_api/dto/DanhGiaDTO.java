package com.e0bmanager.server_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DanhGiaDTO {
    private Integer nhanVienId;
    private String hoTen;
    private String chucVu;
    private Double diemTong;
    private String heSoLuong;
    private int thang;
    private int nam;
}