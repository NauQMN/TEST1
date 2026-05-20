package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sanpham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maSanPham;
    private String tenSanPham;
    private Double gia;
    private String hinhAnh;

    @ManyToOne
    @JoinColumn(name = "MaDanhMuc")
    private DanhMuc danhMuc;

    public Integer getId() { return maSanPham; }
    public void setId(Integer maSanPham) {this.maSanPham = maSanPham;}
    // Getters, Setters...
}