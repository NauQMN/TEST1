package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Data
@NoArgsConstructor

public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Giữ trường này để thao tác ID đơn giản
    @Column(name = "ban_id")
    private int banId;

    // Đánh dấu insertable/updatable = false để tránh xung đột với banId ở trên
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_id", insertable = false, updatable = false)
    private BanAn banAn; // Đổi tên thành banAn cho tường minh

    @Column(name = "nhan_vien_id")
    private Integer nhanVienId;

    @Column(name = "tong_tien")
    private Double tongTien = 0.0;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "trang_thai")
    private Integer trangthai = 0;

    @Column(name = "thoi_gian_thanh_toan")
    private java.time.LocalDateTime thoiGianThanhToan;
    public String getTenBan() {
        return (banAn != null) ? banAn.getTenBan() : "Bàn lẻ";
}

}