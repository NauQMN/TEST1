package e0bmanager.dto;

public class RevenuechartDTO {
    private String ngay;
    private Double doanhThu;
    private Integer soLuongDon;

    public String getNgay() { return ngay; }
    public Double getDoanhThu() { return doanhThu != null ? doanhThu : 0.0; }
    public Integer getSoLuongDon() { return soLuongDon != null ? soLuongDon : 0; }
}

