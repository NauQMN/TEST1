package com.e0bmanager.server_api.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    // Đối tượng sản phẩm chứa đầy đủ: tên, giá, hình ảnh
    private SanPhamDTO sanPham;
    private int soLuong;

    // Các hàm Getter/Setter
    public SanPhamDTO getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPhamDTO sanPham) {
        this.sanPham = sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}