package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.models.SanPham;
import com.e0bmanager.server_api.repositories.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sanpham")
@CrossOrigin("*")
public class SanPhamController {

    @Autowired
    private SanPhamRepository repo;

    // Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<List<SanPham>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    // Lấy sản phẩm theo Danh mục (Dùng khi click chọn loại món bên trái)
    @GetMapping("/filter/{maDM}")
    public ResponseEntity<List<SanPham>> getByDanhMuc(@PathVariable Integer maDM) {
        List<SanPham> list = repo.findByDanhMucMaDanhMuc(maDM);
        return ResponseEntity.ok(list);
    }

    // Lấy chi tiết 1 sản phẩm
    @GetMapping("/{id}")
    public ResponseEntity<SanPham> getOne(@PathVariable Integer id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}