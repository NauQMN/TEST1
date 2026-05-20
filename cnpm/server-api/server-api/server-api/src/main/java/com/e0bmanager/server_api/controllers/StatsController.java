package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.dto.DashboardDTO;
import com.e0bmanager.server_api.repositories.LichLamViecRepository;
import com.e0bmanager.server_api.repositories.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private LichLamViecRepository lichLamViecRepository;

    @GetMapping("/summary")
    public ResponseEntity<DashboardDTO> getSummary() {
        LocalDate today = LocalDate.now();

        // Lấy dữ liệu thật từ DB
        int totalNV = (int) nhanVienRepository.count();
        int caHomNay = (int) lichLamViecRepository.countByNgayLam(today);
        int diLam = (int) lichLamViecRepository.countDistinctNhanVienByNgayLam(today);
        int caThang = (int) lichLamViecRepository.countByMonthAndYear(today.getMonthValue(), today.getYear());

        DashboardDTO dto = new DashboardDTO(totalNV, caHomNay, diLam, caThang);
        return ResponseEntity.ok(dto);
    }
}