package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.dto.LuongDTO;
import com.e0bmanager.server_api.models.*;
import com.e0bmanager.server_api.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/luong")
@CrossOrigin("*")
public class LuongController {

    @Autowired private LuongRepository luongRepo;
    @Autowired private NhanVienRepository nvRepo;
    @Autowired private LichLamViecRepository lichRepo;
    @Autowired private DanhGiaRepository danhGiaRepo;

    /**
     * Tính lương: tongLuong = luongTheoGio × soGioThucTe × (1 + heSo%) + phuCap
     * soGioThucTe = SUM(soGio) của tất cả ca trong tháng (ca custom, không phân loại nữa)
     */
    @GetMapping("/calculate/{nvId}/{thang}/{nam}")
    public ResponseEntity<LuongDTO> calculate(
            @PathVariable Integer nvId,
            @PathVariable int thang,
            @PathVariable int nam) {

        NhanVien nv = nvRepo.findById(nvId).orElse(null);
        if (nv == null) return ResponseEntity.notFound().build();

        LocalDate startDate = LocalDate.of(nam, thang, 1);
        LocalDate endDate   = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // Tổng giờ thực tế từ tất cả ca (kể cả ca custom)
        Double soGioThucTe = lichRepo.sumSoGioByNhanVienAndPeriod(nvId, startDate, endDate);
        if (soGioThucTe == null) soGioThucTe = 0.0;

        // Số ca tổng (để hiển thị tham khảo)
        int tongSoCa = (int) lichRepo.countByNhanVienIdAndNgayLamBetween(nvId, startDate, endDate);

        // Hệ số đánh giá
        String heSo = "0%";
        Optional<DanhGia> dg = danhGiaRepo.findByNhanVienIdAndThangAndNam(nvId, thang, nam);
        if (dg.isPresent()) heSo = dg.get().getHeSoLuong();

        LuongDTO dto = new LuongDTO();
        dto.setIdNv(nvId);
        dto.setTenNv(nv.getHoTen());
        dto.setLuongCoBan(nv.getLuong());
        dto.setSoCa(tongSoCa);
        dto.setSoGioThucTe(soGioThucTe);
        dto.setHeSo(heSo);
        dto.setThang(thang);
        dto.setNam(nam);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/history/{thang}/{nam}")
    public List<LuongDTO> getHistory(@PathVariable int thang, @PathVariable int nam) {
        return luongRepo.findByThangAndNam(thang, nam).stream().map(l -> {
            LuongDTO dto = new LuongDTO();
            dto.setIdNv(l.getNhanVien().getId());
            dto.setTenNv(l.getNhanVien().getHoTen());
            dto.setLuongCoBan(l.getNhanVien().getLuong());
            dto.setSoCa(l.getSoCa());
            dto.setSoGioThucTe((double) l.getSoGioThucTe());
            dto.setPhuCap(l.getPhuCap());
            dto.setHeSo(l.getThuong());
            dto.setTongLuong(l.getTongLuong());
            dto.setThang(l.getThang());
            dto.setNam(l.getNam());
            return dto;
        }).collect(Collectors.toList());
    }

    @PostMapping("/chot-luong")
    public ResponseEntity<?> saveLuong(@RequestBody LuongDTO dto) {
        try {
            Luong entity = luongRepo
                    .findByNhanVienIdAndThangAndNam(dto.getIdNv(), dto.getThang(), dto.getNam())
                    .orElse(new Luong());

            NhanVien nv = nvRepo.findById(dto.getIdNv()).orElse(null);
            if (nv == null) return ResponseEntity.badRequest().body("NHAN_VIEN_NOT_FOUND");

            entity.setNhanVien(nv);
            entity.setThang(dto.getThang());
            entity.setNam(dto.getNam());
            entity.setSoCa(dto.getSoCa() != null ? dto.getSoCa() : 0);
            entity.setSoGioThucTe(dto.getSoGioThucTe() != null ? dto.getSoGioThucTe().intValue() : 0);
            entity.setPhuCap(dto.getPhuCap() != null ? dto.getPhuCap() : 0);
            entity.setThuong(dto.getHeSo());
            entity.setTongLuong(dto.getTongLuong() != null ? dto.getTongLuong() : 0);
            luongRepo.save(entity);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ERROR: " + e.getMessage());
        }
    }
}