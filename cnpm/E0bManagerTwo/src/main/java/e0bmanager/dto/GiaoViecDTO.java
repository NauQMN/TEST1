package e0bmanager.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class GiaoViecDTO {
    private Integer id;
    private Integer nhanVienId;
    private String hoTenNhanVien;
    private String tenCongViec;
    private LocalDate ngayThucHien;
    private String caLamViec;
    private String trangThai;


}