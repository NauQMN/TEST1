package com.e0bmanager.server_api.repositories;

import com.e0bmanager.server_api.models.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    // 1. Tìm kiếm nhân viên theo họ tên (Hỗ trợ tìm kiếm trên giao diện Swing)
    List<NhanVien> findByHoTenContainingIgnoreCase(String hoTen);
    // 2. Tìm kiếm nhân viên theo số điện thoại
    NhanVien findBySdt(String sdt);
    // 3. Đếm số lượng nhân viên đang hoạt động (nếu cột trang_thai của bạn lưu "Đang làm việc")
    long countByTrangThai(String trangThai);
    // 4. Truy vấn nâng cao: Tìm nhân viên có lương cao hơn một mức cụ thể
    @Query("SELECT n FROM NhanVien n WHERE n.luong > :mucLuong")
    List<NhanVien> findNhanVienLuongCao(@Param("mucLuong") Double mucLuong);
}