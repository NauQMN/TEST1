package com.e0bmanager.server_api.controllers;
import com.e0bmanager.server_api.dto.GiaoViecDTO;
import com.e0bmanager.server_api.models.GiaoViec;
import com.e0bmanager.server_api.repositories.GiaoViecRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/giaoviec")
@CrossOrigin("*")
public class GiaoViecController {

    @Autowired
    private GiaoViecRepository giaoViecRepo;

    @GetMapping("/ngay/{date}")
    public List<GiaoViecDTO> getTasksByDate(@PathVariable String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatter);
            List<GiaoViec> tasks = giaoViecRepo.findByNgayThucHien(localDate);
            return tasks.stream().map(g -> {
                GiaoViecDTO dto = new GiaoViecDTO();
                dto.setId(g.getId());
                dto.setTenCongViec(g.getTenCongViec());

                // FIX LỖI NGÀY THÁNG: Chuyển LocalDate sang String để Gson ở Client đọc được
                if(g.getNgayThucHien() != null) {
                    dto.setNgayThucHien(g.getNgayThucHien()); // Trả về dạng "YYYY-MM-DD"
                }

                dto.setCaLamViec(g.getCaLamViec());
                dto.setTrangThai(g.getTrangThai());

                // Kiểm tra null nhân viên để tránh lỗi
                if (g.getNhanVien() != null) {
                    dto.setNhanVienId(g.getNhanVien().getId());
                    dto.setHoTenNhanVien(g.getNhanVien().getHoTen());
                } else {
                    dto.setHoTenNhanVien("N/A");
                }
                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            // RẤT QUAN TRỌNG: In lỗi ra console để biết server bị lỗi gì
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        return giaoViecRepo.findById(id).map(task -> {
            task.setTrangThai(status);
            giaoViecRepo.save(task);
            return ResponseEntity.ok("SUCCESS");
        }).orElse(ResponseEntity.notFound().build());
    }
    // Trong GiaoViecController.java
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer id) {
        try {
            if (giaoViecRepo.existsById(id)) {
                giaoViecRepo.deleteById(id);
                return ResponseEntity.ok("SUCCESS");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ERROR: " + e.getMessage());
        }
    }
}