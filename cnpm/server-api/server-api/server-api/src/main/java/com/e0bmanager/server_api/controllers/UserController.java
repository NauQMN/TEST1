package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.dto.LoginRequest;
import com.e0bmanager.server_api.dto.UserDTO;
import com.e0bmanager.server_api.models.User; // Đảm bảo import đúng model
import com.e0bmanager.server_api.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*") // Cho phép Client kết nối
public class UserController {

    @Autowired
    private UserRepository userRepo; // Chỉ cần khai báo 1 lần duy nhất

    // 1. API Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return userRepo.findByUsername(loginRequest.getUsername())
                .map(user -> {
                    // Trong thực tế nên dùng PasswordEncoder.matches()
                    if (user.getPassword().equals(loginRequest.getPassword())) {
                        UserDTO dto = new UserDTO();
                        BeanUtils.copyProperties(user, dto);
                        return ResponseEntity.ok(dto); // Trả về DTO để bảo mật mật khẩu
                    }
                    return ResponseEntity.status(401).body("Sai mật khẩu!");
                })
                .orElse(ResponseEntity.status(404).body("Không tìm thấy người dùng!"));
    }

    // 2. API Lấy thông tin cá nhân (Profile) theo ID
    // Sửa path thành /profile/{id} để không bị trùng với các GetMapping khác
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        System.out.println("===> Client đang tìm ID: " + id);

        // Lấy thử danh sách tất cả ID đang có trong DB mà Server thấy
        java.util.List<Long> allIds = userRepo.findAll().stream()
                .map(u -> u.getId())
                .collect(java.util.stream.Collectors.toList());

        System.out.println("===> Danh sách ID thực tế trong DB Server đang kết nối: " + allIds);

        return userRepo.findById(id)
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    org.springframework.beans.BeanUtils.copyProperties(user, dto);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    System.out.println("===> KẾT QUẢ: Không tìm thấy ID " + id + " trong danh sách trên!");
                    return ResponseEntity.notFound().build();
                });
    }

    // API Cập nhật thông tin
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UserDTO dto) {
        return userRepo.findById(dto.getId()).map(user -> {
            // Cập nhật ảnh nếu có gửi lên
            if (dto.getAvatar() != null) {
                user.setAvatar(dto.getAvatar());
            }

            // Cập nhật mật khẩu nếu có gửi lên (Nên mã hóa nếu có dùng BCrypt)
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                user.setPassword(dto.getPassword());
            }

            userRepo.save(user);
            return ResponseEntity.ok("SUCCESS");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO dto) {
        if(userRepo.findByUsername(dto.getUsername()).isPresent()) {
            return ResponseEntity.status(409).body("Username existed");
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        userRepo.save(user);
        return ResponseEntity.ok("SUCCESS");
    }
}
