package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_account")
@Data
public class OrderAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullname;
    private String role;
    private int status;
    @Column(name = "nhan_vien_id")
    private Integer nhanVienId;
}