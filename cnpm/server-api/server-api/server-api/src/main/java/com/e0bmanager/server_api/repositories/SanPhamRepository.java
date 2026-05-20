package com.e0bmanager.server_api.repositories;

import com.e0bmanager.server_api.models.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    // Spring Data JPA sẽ tự hiểu: Tìm Sản phẩm dựa trên MaDanhMuc của đối tượng DanhMuc
    List<SanPham> findByDanhMucMaDanhMuc(Integer maDanhMuc);

    // Bạn cũng có thể thêm tìm kiếm theo tên món ăn nếu sau này cần
    List<SanPham> findByTenSanPhamContainingIgnoreCase(String ten);
}
