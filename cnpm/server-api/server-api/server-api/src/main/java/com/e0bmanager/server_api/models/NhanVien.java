package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "nhanvien")
@Data
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Dùng Integer vì DB thường để int

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "ngay_sinh")
    @Temporal(TemporalType.DATE)
    private LocalDate ngaySinh;

    private Double luong;

    @Column(name = "chuc_vu")
    private String chucVu;

    private String sdt;

    @Column(name = "trang_thai")
    private String trangThai;
    @Column(name = "email")
    private String email;
}