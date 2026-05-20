package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.dto.LichLamViecDTO;
import com.e0bmanager.server_api.models.LichLamViec;
import com.e0bmanager.server_api.models.NhanVien;
import com.e0bmanager.server_api.repositories.LichLamViecRepository;
import com.e0bmanager.server_api.repositories.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/lich")
@CrossOrigin("*")
public class LichLamViecController {

    @Autowired private LichLamViecRepository lichRepo;
    @Autowired private NhanVienRepository nhanVienRepo;

    // ── Lấy lịch theo ngày (dùng cho tab Quản lý) ──────────────────────────
    @GetMapping("/ngay/{date}")
    public List<LichLamViecDTO> getByDate(@PathVariable String date) {
        try {
            return lichRepo.findByNgayLam(LocalDate.parse(date))
                    .stream().map(this::toDTO).toList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ── Lấy lịch chờ duyệt (dùng cho tab Duyệt lịch nhân viên) ───────────
    @GetMapping("/cho-duyet")
    public List<LichLamViecDTO> getPending() {
        try {
            return lichRepo.findByTrangThaiAndNguonTao("Chờ duyệt", "NHAN_VIEN")
                    .stream().map(this::toDTO).toList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ── Thêm lịch mới (quản lý phân ca) ───────────────────────────────────
    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@RequestBody LichLamViecDTO dto) {
        NhanVien nv = nhanVienRepo.findById(dto.getNhanVienId()).orElse(null);
        if (nv == null) return ResponseEntity.badRequest().body("NV_NOT_FOUND");

        LichLamViec entity = new LichLamViec();
        entity.setNhanVien(nv);
        entity.setNgayLam(dto.getNgayLam());
        entity.setGioVao(dto.getGioVao());
        entity.setGioRa(dto.getGioRa());
        entity.setSoGio(tinhSoGio(dto.getGioVao(), dto.getGioRa()));
        entity.setCaLam(buildTenCa(dto.getCaLam(), dto.getGioVao(), dto.getGioRa()));
        entity.setNguonTao("QUAN_LY");
        entity.setTrangThai("Bình thường");

        lichRepo.save(entity);
        return ResponseEntity.ok("SUCCESS");
    }

    // ── Duyệt lịch nhân viên đăng ký ──────────────────────────────────────
    @PutMapping("/duyet/{id}")
    public ResponseEntity<?> duyetLich(@PathVariable Integer id) {
        return lichRepo.findById(id).map(l -> {
            l.setTrangThai("Đã duyệt");
            lichRepo.save(l);
            return ResponseEntity.ok("SUCCESS");
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Từ chối lịch nhân viên đăng ký ────────────────────────────────────
    @PutMapping("/tu-choi/{id}")
    public ResponseEntity<?> tuChoiLich(@PathVariable Integer id) {
        return lichRepo.findById(id).map(l -> {
            l.setTrangThai("Từ chối");
            lichRepo.save(l);
            return ResponseEntity.ok("SUCCESS");
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Xóa ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        lichRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── Helper ────────────────────────────────────────────────────────────
    private LichLamViecDTO toDTO(LichLamViec l) {
        NhanVien nv = l.getNhanVien();
        LichLamViecDTO dto = new LichLamViecDTO();
        dto.setId(l.getId());
        dto.setNhanVienId(nv != null ? nv.getId() : 0);
        dto.setHoTen(nv != null ? nv.getHoTen() : "Không xác định");
        dto.setChucVu(nv != null ? nv.getChucVu() : "N/A");
        dto.setSdt(nv != null ? nv.getSdt() : "N/A");
        dto.setNgayLam(l.getNgayLam());
        dto.setCaLam(l.getCaLam());
        dto.setGioVao(l.getGioVao());
        dto.setGioRa(l.getGioRa());
        dto.setSoGio(l.getSoGio());
        dto.setNguonTao(l.getNguonTao());
        dto.setTrangThai(l.getTrangThai());
        return dto;
    }

    /** Tính số giờ từ "HH:mm" -> "HH:mm" */
    private double tinhSoGio(String gioVao, String gioRa) {
        try {
            String[] v = gioVao.split(":");
            String[] r = gioRa.split(":");
            int phutVao = Integer.parseInt(v[0]) * 60 + Integer.parseInt(v[1]);
            int phutRa  = Integer.parseInt(r[0]) * 60 + Integer.parseInt(r[1]);
            double gio = (phutRa - phutVao) / 60.0;
            return Math.max(0, gio);
        } catch (Exception e) {
            return 0;
        }
    }

    /** Tạo tên ca hiển thị */
    private String buildTenCa(String tenCa, String gioVao, String gioRa) {
        if (tenCa != null && !tenCa.isBlank()) return tenCa + " (" + gioVao + " - " + gioRa + ")";
        return gioVao + " - " + gioRa;
    }
}