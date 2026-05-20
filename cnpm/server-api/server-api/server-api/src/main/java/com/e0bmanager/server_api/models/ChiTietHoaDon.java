package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "chi_tiet_hoa_don")
public class ChiTietHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "hoa_don_id")
    private Integer hoaDonId;

    // THÊM ĐOẠN NÀY ĐỂ JPA HIỂU MỐI QUAN HỆ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoa_don_id", insertable = false, updatable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mon_an_id")
    private SanPham sanPham;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "gia_luc_ban")
    private BigDecimal giaLucBan;
}