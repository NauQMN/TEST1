package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "lich_lam_viec")
@Data
public class LichLamViec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "ngay_lam")
    private LocalDate ngayLam;

    /** Tên ca hiển thị, ví dụ "Ca Sáng", "Ca Tùy Chỉnh 15:00-18:00" */
    @Column(name = "ca_lam")
    private String caLam;

    /** Giờ vào dạng "HH:mm", ví dụ "08:00" */
    @Column(name = "gio_vao")
    private String gioVao;

    /** Giờ ra dạng "HH:mm", ví dụ "14:00" */
    @Column(name = "gio_ra")
    private String gioRa;

    /** Số giờ làm thực tế = gioRa - gioVao (tính tự động khi lưu) */
    @Column(name = "so_gio")
    private Double soGio;

    /** Nguồn tạo: "QUAN_LY" hoặc "NHAN_VIEN" */
    @Column(name = "nguon_tao")
    private String nguonTao = "QUAN_LY";

    /** Trạng thái: "Bình thường", "Chờ duyệt", "Đã duyệt", "Từ chối" */
    @Column(name = "trang_thai")
    private String trangThai = "Bình thường";
}