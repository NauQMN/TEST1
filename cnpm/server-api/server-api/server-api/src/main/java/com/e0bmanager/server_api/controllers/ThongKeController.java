package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.repositories.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
public class ThongKeController {

    @Autowired
    private HoaDonRepository hoaDonRepo;

    // API lấy doanh thu hôm nay
}