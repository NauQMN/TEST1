package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "giao_viec")
@Data
public class GiaoViec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien") // Tên cột trong DB của bạn
    private NhanVien nhanVien;
    @Column(name = "ten_cong_viec")
    private String tenCongViec;
    @Column( name = "ngay_thuc_hien")
    private LocalDate ngayThucHien;
    @Column (name = "ca_lam_viec")
    private String caLamViec;
    @Column (name = "trang_thai")
    private String trangThai;
}