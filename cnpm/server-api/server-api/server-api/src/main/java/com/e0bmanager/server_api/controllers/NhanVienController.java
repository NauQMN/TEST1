package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.models.NhanVien;
import com.e0bmanager.server_api.repositories.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nhanvien")
public class NhanVienController {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    // Lấy toàn bộ danh sách
    @GetMapping("/all")
    public List<NhanVien> getAll() {
        return nhanVienRepository.findAll();
    }

    // Xóa nhân viên
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return nhanVienRepository.findById(id).map(nv -> {
            nhanVienRepository.delete(nv);
            return ResponseEntity.ok().body("Xóa thành công");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/add")
    public ResponseEntity<NhanVien> addEmployee(@RequestBody NhanVien nv) {
        // Mặc định trạng thái khi mới thêm là "Đang làm việc"
        if (nv.getTrangThai() == null) nv.setTrangThai("Đang làm việc");
        NhanVien savedNv = nhanVienRepository.save(nv);
        return ResponseEntity.ok(savedNv);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<NhanVien> updateEmployee(@PathVariable Integer id, @RequestBody NhanVien nvDetails) {
        return nhanVienRepository.findById(id).map(nv -> {
            nv.setHoTen(nvDetails.getHoTen());
            nv.setNgaySinh(nvDetails.getNgaySinh());
            nv.setLuong(nvDetails.getLuong());
            nv.setChucVu(nvDetails.getChucVu());
            nv.setSdt(nvDetails.getSdt());
            // Giữ nguyên các trường khác hoặc cập nhật thêm nếu cần
            NhanVien updatedNv = nhanVienRepository.save(nv);
            return ResponseEntity.ok(updatedNv);
        }).orElse(ResponseEntity.notFound().build());
    }
}
