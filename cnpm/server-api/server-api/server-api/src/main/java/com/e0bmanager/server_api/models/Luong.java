package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "luong")
@Data
public class Luong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_nv", nullable = false)
    private NhanVien nhanVien;

    private int thang;
    private int nam;

    @Column(name = "so_ca")
    private int soCa;

    @Column(name = "so_gio_thuc_te")
    private double soGioThucTe;   // Tổng giờ thực tế từ tất cả ca

    @Column(name = "phu_cap")
    private double phuCap;

    private String thuong;        // Hệ số: "+10%", "0%"...

    @Column(name = "tong_luong")
    private double tongLuong;
}