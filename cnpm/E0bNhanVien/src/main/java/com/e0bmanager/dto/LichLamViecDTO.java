package com.e0bmanager.dto;

public class LichLamViecDTO {
    private Integer id;
    private Integer nhanVienId;
    private String hoTen;
    private String chucVu;
    private String sdt;
    private String ngayLam;   // yyyy-MM-dd (Gson sẽ deserialize từ LocalDate)
    private String caLam;
    private String gioVao;
    private String gioRa;
    private Double soGio;
    private String nguonTao;
    private String trangThai;

    public Integer getId() { return id; }
    public Integer getNhanVienId() { return nhanVienId; }
    public String getHoTen() { return hoTen; }
    public String getChucVu() { return chucVu; }
    public String getNgayLam() { return ngayLam; }
    public String getCaLam() { return caLam; }
    public String getGioVao() { return gioVao; }
    public String getGioRa() { return gioRa; }
    public Double getSoGio() { return soGio; }
    public String getNguonTao() { return nguonTao; }
    public String getTrangThai() { return trangThai; }

    public void setId(Integer id) { this.id = id; }
    public void setNhanVienId(Integer nhanVienId) { this.nhanVienId = nhanVienId; }
    public void setNgayLam(String ngayLam) { this.ngayLam = ngayLam; }
    public void setCaLam(String caLam) { this.caLam = caLam; }
    public void setGioVao(String gioVao) { this.gioVao = gioVao; }
    public void setGioRa(String gioRa) { this.gioRa = gioRa; }
    public void setSoGio(Double soGio) { this.soGio = soGio; }
    public void setNguonTao(String nguonTao) { this.nguonTao = nguonTao; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}