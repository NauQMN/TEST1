package com.e0bmanager.server_api.repositories;




import com.e0bmanager.server_api.models.ChiTietHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ChiTietHoaDonRepository extends JpaRepository<ChiTietHoaDon, Integer> {

    @Query("SELECT ct FROM ChiTietHoaDon ct " +
            "JOIN FETCH ct.sanPham " + // Lấy kèm thông tin sản phẩm
            "JOIN ct.hoaDon hd " +
            "WHERE hd.banId = :tableId AND hd.trangthai = 0")
    List<ChiTietHoaDon> findActiveDetailsByTableId(@Param("tableId") Integer tableId);
}