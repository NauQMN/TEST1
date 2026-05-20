package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.dto.RegisterRequest;
import com.e0bmanager.server_api.models.OrderAccount;
import com.e0bmanager.server_api.repositories.OrderAccountRepository;
import com.e0bmanager.server_api.services.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pda/auth")
@CrossOrigin("*")
public class PDAAuthController {

    @Autowired
    private OrderAccountRepository accountRepo;
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String user = credentials.get("username");
        String pass = credentials.get("password");

        OrderAccount account = accountRepo.findByUsername(user);

        // Lưu ý: Trong thực tế nên dùng BCrypt để so sánh password
        if (account != null && account.getPassword().equals(pass)) {
            if (account.getStatus() == 0) {
                return ResponseEntity.status(403).body("Tài khoản đã bị khóa!");
            }
            return ResponseEntity.ok(account);
        }

        return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu!");
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            authService.registerStaff(req);
            return ResponseEntity.ok("Đăng ký thành công, vui lòng chờ quản lý duyệt!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingAccounts() {
        return ResponseEntity.ok(authService.getPendingRequests());
    }

    @GetMapping("/pending-count")
    public ResponseEntity<?> getPendingCount() {
        // Đếm số lượng account có status = 0
        return ResponseEntity.ok(accountRepo.countByStatus(0));
    }

    @PutMapping("/update-status/{id}/{status}")
    @Transactional
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        // 1. Tìm bản ghi mới nhất từ DB
        OrderAccount acc = accountRepo.findById(Long.valueOf(id)).orElse(null);

        if (acc == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy tài khoản!");
        }

        // 2. Cập nhật trạng thái
        acc.setStatus(status);

        // 3. Lưu lại (Hibernate sẽ tự động xử lý transaction)
        try {
            authService.approveAccount(id); // Gọi service mới
            return ResponseEntity.ok("Đã duyệt và gửi email thông báo!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi cập nhật: " + e.getMessage());
        }
    }
    @DeleteMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable Integer id) {
        try {
            authService.rejectRegister(id);
            return ResponseEntity.ok("Đã xóa yêu cầu đăng ký thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}