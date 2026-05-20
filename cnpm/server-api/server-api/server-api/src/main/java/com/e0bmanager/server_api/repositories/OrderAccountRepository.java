package com.e0bmanager.server_api.repositories;

import com.e0bmanager.server_api.models.OrderAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderAccountRepository extends JpaRepository<OrderAccount, Long> {
    OrderAccount findByUsername(String username);
    List<OrderAccount> findByStatus(Integer status);
    long countByStatus(Integer status);
}