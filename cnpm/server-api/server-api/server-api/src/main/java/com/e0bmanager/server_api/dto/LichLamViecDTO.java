package com.e0bmanager.server_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LichLamViecDTO {
    private Integer id;
    private Integer nhanVienId;
    private String hoTen;
    private String chucVu;
    private String sdt;
    private LocalDate ngayLam;
    private String caLam;
    private String gioVao;       // "HH:mm"
    private String gioRa;        // "HH:mm"
    private Double soGio;        // Giờ làm thực tế
    private String nguonTao;     // "QUAN_LY" | "NHAN_VIEN"
    private String trangThai;
}