package com.e0bmanager.server_api.repositories;

import com.e0bmanager.server_api.models.LichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LichLamViecRepository extends JpaRepository<LichLamViec, Integer> {

    List<LichLamViec> findByNgayLam(LocalDate ngayLam);

    List<LichLamViec> findByTrangThaiAndNguonTao(String trangThai, String nguonTao);

    boolean existsByNhanVienIdAndNgayLamAndCaLam(Integer nhanVienId, LocalDate ngayLam, String caLam);

    long countByNhanVienIdAndNgayLamBetween(Integer nvId, LocalDate start, LocalDate end);

    /** Tổng số giờ thực tế của một nhân viên trong tháng (dùng cho tính lương) */
    @Query("SELECT COALESCE(SUM(l.soGio), 0) FROM LichLamViec l " +
            "WHERE l.nhanVien.id = :nvId AND l.ngayLam BETWEEN :start AND :end " +
            "AND l.trangThai != 'Từ chối'")
    Double sumSoGioByNhanVienAndPeriod(@Param("nvId") Integer nvId,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    @Query("SELECT COUNT(DISTINCT l.nhanVien.id) FROM LichLamViec l WHERE l.ngayLam = :ngayLam")
    long countDistinctNhanVienByNgayLam(@Param("ngayLam") LocalDate ngayLam);

    @Query(value = "SELECT COUNT(*) FROM lich_lam_viec WHERE MONTH(ngay_lam) = :month AND YEAR(ngay_lam) = :year", nativeQuery = true)
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    long countByNgayLam(LocalDate ngayLam);
}