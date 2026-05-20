package com.e0bmanager.server_api.repositories;



import com.e0bmanager.server_api.models.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {

    /**
     * Tìm đánh giá của một nhân viên cụ thể trong một tháng và năm nhất định.
     * Đây là hàm quan trọng nhất để tránh việc tạo trùng bản ghi đánh giá
     * cho cùng một người trong một tháng.
     */
    Optional<DanhGia> findByNhanVienIdAndThangAndNam(Integer nhanVienId, int thang, int nam);

    /**
     * Kiểm tra xem nhân viên đã được đánh giá trong tháng đó chưa
     */
    boolean existsByNhanVienIdAndThangAndNam(Integer nhanVienId, int thang, int nam);
}