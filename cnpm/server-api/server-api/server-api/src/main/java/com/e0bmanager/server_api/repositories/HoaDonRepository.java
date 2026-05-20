package com.e0bmanager.server_api.repositories;

import com.e0bmanager.server_api.models.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    // Tìm hóa đơn theo ID Bàn và Trạng thái (Ví dụ: bàn 5 đang có khách - trạng thái 0)
    // Thay h.trangThai bằng h.trangthai (viết thường chữ t)
    // Đảm bảo tên biến trong @Query khớp 100% với tên biến trong Model HoaDon.java
    @Query("SELECT h FROM HoaDon h WHERE h.banId = :banId AND h.trangthai = :trangThai")
    Optional<HoaDon> findByBanIdAndTrangThai(@Param("banId") Integer banId, @Param("trangThai") Integer trangThai);
    @Query("SELECT h FROM HoaDon h JOIN FETCH h.banAn WHERE h.trangthai = 0")
    List<HoaDon> findActiveInvoices();
    // Tính tổng tiền theo trạng thái
     @Query("SELECT SUM(h.tongTien) FROM HoaDon h WHERE h.trangthai = :status")
        Double sumTongTienByTrangThai(@Param("status") int status);
        Long countByTrangthai(int status);
    @Query("SELECT SUM(h.tongTien) FROM HoaDon h WHERE h.trangthai = 0 AND DATE(h.ngayTao) = :date")
    Double sumTongTienDangMo(@Param("date") LocalDate date);
    // Đếm số lượng hóa đơn đang mở (trạng thái 0) trong ngày cụ thể
    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.trangthai = 0 AND DATE(h.ngayTao) = :date")
    Long countHoaDonDangMo(@Param("date") LocalDate date);

    }

