package com.e0bmanager.dto;

import lombok.Data;

@Data // Tự động tạo Getter/Setter nhờ Lombok
public class RegisterRequestDTO {
    // Thông tin tài khoản
    private String username;
    private String password;

    // Thông tin nhân viên
    private String hoTen;
    private String ngaySinh; // Format: yyyy-MM-dd hoặc dd/MM/yyyy tùy backend
    private Double luong;    // Mức lương đề xuất/mong muốn
    private String chucVu;
    private String sdt;
    private String email;
}
