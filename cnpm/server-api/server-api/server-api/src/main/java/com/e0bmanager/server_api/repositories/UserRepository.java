package com.e0bmanager.server_api.repositories;

import com.e0bmanager.server_api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Tự động tạo hàm tìm kiếm theo username
    Optional<User> findByUsername(String username);
}