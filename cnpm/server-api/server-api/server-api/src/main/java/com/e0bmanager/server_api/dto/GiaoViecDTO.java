package com.e0bmanager.server_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiaoViecDTO {
    private Integer id;
    private Integer nhanVienId;
    private String hoTenNhanVien;
    private String tenCongViec;
    private LocalDate ngayThucHien;
    private String caLamViec;
    private String trangThai;
}
