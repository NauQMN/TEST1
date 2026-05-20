package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "danh_gia_thang")
@Data
public class DanhGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nhan_vien_id")
    private NhanVien nhanVien;

    private int thang;
    private int nam;

    @Column(name = "diem_tong")
    private Double diemTong;

    @Column(name = "he_so_luong")
    private String heSoLuong; // Ví dụ: "+10%", "-5%"
}