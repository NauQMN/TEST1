package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.dto.DanhGiaDTO;
import com.e0bmanager.server_api.models.DanhGia;
import com.e0bmanager.server_api.models.NhanVien;
import com.e0bmanager.server_api.repositories.DanhGiaRepository;
import com.e0bmanager.server_api.repositories.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/danhgia")
@CrossOrigin("*")
public class DanhGiaController {
    @Autowired
    private DanhGiaRepository danhGiaRepo;
    @Autowired private NhanVienRepository nvRepo;

    @GetMapping("/{thang}/{nam}")
    public List<DanhGiaDTO> getDanhSach(@PathVariable int thang, @PathVariable int nam) {
        // Lấy tất cả nhân viên và LEFT JOIN với bảng đánh giá
        List<NhanVien> allNv = nvRepo.findAll();
        return allNv.stream().map(nv -> {
            DanhGiaDTO dto = new DanhGiaDTO();
            dto.setNhanVienId(nv.getId());
            dto.setHoTen(nv.getHoTen());
            dto.setChucVu(nv.getChucVu());

            danhGiaRepo.findByNhanVienIdAndThangAndNam(nv.getId(), thang, nam)
                    .ifPresent(dg -> {
                        dto.setDiemTong(dg.getDiemTong());
                        dto.setHeSoLuong(dg.getHeSoLuong());
                    });
            return dto;
        }).collect(Collectors.toList());
    }

    @PostMapping("/cap-nhat")
    public ResponseEntity<?> update(@RequestBody DanhGiaDTO dto) {
        DanhGia dg = danhGiaRepo.findByNhanVienIdAndThangAndNam(dto.getNhanVienId(), dto.getThang(), dto.getNam())
                .orElse(new DanhGia());

        dg.setNhanVien(nvRepo.findById(dto.getNhanVienId()).get());
        dg.setThang(dto.getThang());
        dg.setNam(dto.getNam());
        dg.setDiemTong(dto.getDiemTong());
        dg.setHeSoLuong(dto.getHeSoLuong());

        danhGiaRepo.save(dg);
        return ResponseEntity.ok("SUCCESS");
    }
}
