package e0bmanager.dto;

public class LuongDTO {
    private Integer idNv;
    private String tenNv;
    private Double luongCoBan;    // Lương theo giờ (VNĐ/giờ)
    private Integer soCa;         // Tổng số ca tham khảo
    private Double soGioThucTe;   // Tổng giờ thực tế (SUM từ các ca)
    private String heSo;
    private Double phuCap;
    private Double tongLuong;
    private Integer thang;
    private Integer nam;

    public Integer getIdNv() { return idNv; }
    public void setIdNv(Integer idNv) { this.idNv = idNv; }
    public String getTenNv() { return tenNv; }
    public void setTenNv(String tenNv) { this.tenNv = tenNv; }
    public Double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(Double luongCoBan) { this.luongCoBan = luongCoBan; }
    public Integer getSoCa() { return soCa; }
    public void setSoCa(Integer soCa) { this.soCa = soCa; }
    public Double getSoGioThucTe() { return soGioThucTe; }
    public void setSoGioThucTe(Double soGioThucTe) { this.soGioThucTe = soGioThucTe; }
    public String getHeSo() { return heSo; }
    public void setHeSo(String heSo) { this.heSo = heSo; }
    public Double getPhuCap() { return phuCap; }
    public void setPhuCap(Double phuCap) { this.phuCap = phuCap; }
    public Double getTongLuong() { return tongLuong; }
    public void setTongLuong(Double tongLuong) { this.tongLuong = tongLuong; }
    public Integer getThang() { return thang; }
    public void setThang(Integer thang) { this.thang = thang; }
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
}