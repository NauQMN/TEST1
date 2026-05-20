package com.e0bmanager.server_api.repositories;

import com.e0bmanager.server_api.models.DoanhThu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoanhThuRepository extends JpaRepository<DoanhThu, Integer> {

    // Tìm kiếm doanh thu theo ngày cụ thể
    Optional<DoanhThu> findByNgay(LocalDate ngay);
    List<DoanhThu> findByNgayBetweenOrderByNgayAsc(LocalDate start, LocalDate end);
    // Tính tổng doanh thu theo một tháng và năm cụ thể
    @Query("SELECT SUM(d.tongDoanhThu) FROM DoanhThu d WHERE MONTH(d.ngay) = :month AND YEAR(d.ngay) = :year")
    Double sumByMonth(@Param("month") int month, @Param("year") int year);

    // Đếm tổng đơn hàng theo tháng
    @Query("SELECT SUM(d.soLuongDon) FROM DoanhThu d WHERE MONTH(d.ngay) = :month AND YEAR(d.ngay) = :year")
    Long countByMonth(@Param("month") int month, @Param("year") int year);
}