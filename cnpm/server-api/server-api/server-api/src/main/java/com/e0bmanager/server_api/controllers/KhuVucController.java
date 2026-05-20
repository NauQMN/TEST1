package com.e0bmanager.server_api.controllers;


import com.e0bmanager.server_api.models.KhuVuc;
import com.e0bmanager.server_api.repositories.KhuVucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khuvuc")
@CrossOrigin("*") // Quan trọng để Swing có thể gọi được API
public class KhuVucController {

    @Autowired
    private KhuVucRepository repo;

    @GetMapping
    public List<KhuVuc> getAll() {
        return repo.findAll();
    }
}