package com.e0bmanager.server_api.repositories;

import com.e0bmanager.server_api.models.Luong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LuongRepository extends JpaRepository<Luong, Integer> {
    // Tìm bảng lương đã lưu của một nhân viên trong tháng/nam cụ thể
    Optional<Luong> findByNhanVienIdAndThangAndNam(Integer nvId, int thang, int nam);

    // Lấy danh sách lương toàn bộ nhân viên theo tháng để hiển thị lên bảng
    @Query("SELECT l FROM Luong l WHERE l.thang = :thang AND l.nam = :nam")
    List<Luong> findHistory(@Param("thang") int thang, @Param("nam") int nam);
    List<Luong> findByThangAndNam(int thang, int nam);

}