package com.e0bmanager.server_api.repositories;


import com.e0bmanager.server_api.models.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
    // Các phương thức mặc định: findAll(), findById(), save(), delete()
}
