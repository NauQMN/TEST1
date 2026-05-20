package com.e0bmanager.server_api.dto;


import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String hoTen;
    private String ngaySinh;
    private Double luong;
    private String chucVu;
    private String sdt;
    private String email;
}