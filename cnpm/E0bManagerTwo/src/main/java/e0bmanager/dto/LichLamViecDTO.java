package e0bmanager.dto;

import java.time.LocalDate;

public class LichLamViecDTO {
    private Integer id;
    private Integer nhanVienId;
    private String hoTen;
    private String chucVu;
    private String sdt;
    private LocalDate ngayLam;
    private String caLam;
    private String gioVao;    // "HH:mm"
    private String gioRa;     // "HH:mm"
    private Double soGio;     // Giờ làm thực tế
    private String nguonTao;  // "QUAN_LY" | "NHAN_VIEN"
    private String trangThai;

    public LichLamViecDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(Integer nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public LocalDate getNgayLam() { return ngayLam; }
    public void setNgayLam(LocalDate ngayLam) { this.ngayLam = ngayLam; }
    public String getCaLam() { return caLam; }
    public void setCaLam(String caLam) { this.caLam = caLam; }
    public String getGioVao() { return gioVao; }
    public void setGioVao(String gioVao) { this.gioVao = gioVao; }
    public String getGioRa() { return gioRa; }
    public void setGioRa(String gioRa) { this.gioRa = gioRa; }
    public Double getSoGio() { return soGio; }
    public void setSoGio(Double soGio) { this.soGio = soGio; }
    public String getNguonTao() { return nguonTao; }
    public void setNguonTao(String nguonTao) { this.nguonTao = nguonTao; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}