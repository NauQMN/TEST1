package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ban_an")
@Getter
@Setter
public class BanAn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_ban") // Ánh xạ đúng với tên cột trong DB
    private String tenBan;    // Đổi tên biến để tuân thủ quy tắc CamelCase

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khu_vuc_id") // Ánh xạ cột khu_vuc_id
    private KhuVuc khuVuc;
    @Column(name = "trang_thai")
    private Integer trangthai;
    @Transient // Đánh dấu không lưu vào DB bảng ban_an
    private Integer currentHoaDonId;
    @Transient // Đánh dấu không lưu vào DB, chỉ dùng để tính toán tạm thời trên API
    private Long tongTien = 0L;
    public void setTongTien(Long tongTien) {
        this.tongTien = tongTien;
    }
    // Không cần hàm getTenBan() phức tạp, Lombok đã tạo getter cho tenBan rồi.
}

