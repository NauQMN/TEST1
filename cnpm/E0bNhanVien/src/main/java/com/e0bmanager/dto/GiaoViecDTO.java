package com.e0bmanager.dto;

public class GiaoViecDTO {
    private Integer id;
    private Integer nhanVienId;
    private String hoTenNhanVien;
    private String tenCongViec;
    private String ngayThucHien; // yyyy-MM-dd
    private String caLamViec;
    private String trangThai;

    public Integer getId() { return id; }
    public Integer getNhanVienId() { return nhanVienId; }
    public String getHoTenNhanVien() { return hoTenNhanVien; }
    public String getTenCongViec() { return tenCongViec; }
    public String getNgayThucHien() { return ngayThucHien; }
    public String getCaLamViec() { return caLamViec; }
    public String getTrangThai() { return trangThai; }

    public void setId(Integer id) { this.id = id; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}