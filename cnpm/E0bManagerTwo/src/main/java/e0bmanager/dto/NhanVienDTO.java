package e0bmanager.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NhanVienDTO {
    private Integer id;
    private String hoTen;
    private String ngaySinh;  // Nhận chuỗi "yyyy-MM-dd" từ server, tránh lỗi timezone của Date
    private Double luong;
    private String chucVu;
    private String sdt;
    private String trangThai;
    private int accountId;

    public NhanVienDTO() {}

    public NhanVienDTO(Integer id, String hoTen, String ngaySinh, Double luong, String chucVu, String sdt, String trangThai) {
        this.id = id;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.luong = luong;
        this.chucVu = chucVu;
        this.sdt = sdt;
        this.trangThai = trangThai;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }

    /** Tiện ích: chuyển String "yyyy-MM-dd" sang java.util.Date để dùng với JDateChooser */
    public Date getNgaySinhAsDate() {
        if (ngaySinh == null || ngaySinh.isEmpty()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(ngaySinh);
        } catch (Exception e) {
            return null;
        }
    }

    /** Tiện ích: set từ java.util.Date (khi gửi lên server từ JDateChooser) */
    public void setNgaySinhFromDate(Date date) {
        if (date == null) { this.ngaySinh = null; return; }
        this.ngaySinh = new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public Double getLuong() { return luong; }
    public void setLuong(Double luong) { this.luong = luong; }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public int getAccountID() { return accountId; }
    public void setAccountID(int accountID) { this.accountId = accountID; }

    @Override
    public String toString() { return hoTen; }
}