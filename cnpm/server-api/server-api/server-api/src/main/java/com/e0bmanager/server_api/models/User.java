package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // Tên bảng chính xác trong MySQL của bạn
@Data // Lombok: Tự động tạo Getter, Setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: Tạo Constructor không tham số (bắt buộc cho JPA)
@AllArgsConstructor // Lombok: Tạo Constructor đầy đủ tham số
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng ID (Auto Increment)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(name ="fullname")
    private String fullname; // Tên đầy đủ để hiển thị trên MainForm
    private String phone;
    private String cccd;
    @Column(columnDefinition = "LONGTEXT") // Dùng LONGTEXT để chứa chuỗi ảnh Base64 dung lượng lớn
    private String avatar;
    private String email;

}