package com.e0bmanager.server_api.repositories;


import com.e0bmanager.server_api.models.KhuVuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhuVucRepository extends JpaRepository<KhuVuc, Integer> {
    // JpaRepository đã có sẵn hàm findAll(), save(), delete() nên bạn không cần viết gì thêm ở đây.
}