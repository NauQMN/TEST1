package com.e0bmanager.server_api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SanPhamDTO {
    private int maSanPham;
    private String tenSanPham;
    private BigDecimal gia;
    private int maDanhMuc;
    private boolean trangThai;
    private String hinhAnh;
}
