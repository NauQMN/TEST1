package com.e0bmanager.server_api.repositories;

import com.e0bmanager.server_api.models.GiaoViec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GiaoViecRepository extends JpaRepository<GiaoViec, Integer> {
    List<GiaoViec> findByNgayThucHien(LocalDate ngayThucHien);
}