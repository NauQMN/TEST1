package e0bmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsDTO {
    private Double tongDoanhThu;      // Trang thai 0 + 2
    private Double doanhThuDaRut;    // Trang thai 2
    private Double doanhThuConLai;   // Trang thai 0
    private Long soHdDaRut;         // Count trang thai 2
    private Long soHdChuaRut;       // Count trang thai 0
}