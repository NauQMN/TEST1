package e0bmanager.dto;

public class DanhGiaDTO {
    private Integer nhanVienId;
    private String hoTen;
    private String chucVu;
    private Double diemTong;
    private String heSoLuong;
    private int thang;
    private int nam;

    public Integer getNhanVienId() {return nhanVienId;}
    public void setNhanVienId(Integer nhanVienId) {this.nhanVienId = nhanVienId;}
    public String getHoTen() {return hoTen;}
    public void setHoTen(String hoTen) {this.hoTen = hoTen;}
    public String getChucVu() {return chucVu;}
    public void setChucVu(String chucVu) {this.chucVu = chucVu;}
    public Double getDiemTong() {return diemTong;}
    public void setDiemTong(Double diemTong) {}
    public String getHeSoLuong() {return heSoLuong;}
    public void setHeSoLuong(String heSoLuong) {this.heSoLuong = heSoLuong;}
    public int getThang() {return thang;}
    public void setThang(int thang) {this.thang = thang;}
    public int getNam() {return nam;}
    public void setNam(int nam) {this.nam = nam;}
}