package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.models.DanhMuc;
import com.e0bmanager.server_api.repositories.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/danhmuc")
@CrossOrigin("*") // Cho phép Swing Client truy cập không bị chặn CORS
public class DanhMucController {

    @Autowired
    private DanhMucRepository repo;

    @GetMapping
    public ResponseEntity<List<DanhMuc>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }
}