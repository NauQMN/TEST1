package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.dto.HoaDonDTO;
import com.e0bmanager.server_api.models.HoaDon;
import com.e0bmanager.server_api.repositories.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hoadon") // Đây chính là đường dẫn bạn cần
public class HoaDonController {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @GetMapping
    public List<HoaDonDTO> getActiveInvoices() {
        // Logic tìm hóa đơn có trạng thái đang mở (ví dụ: trạng thái 0)
        return hoaDonRepository.findActiveInvoices()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private HoaDonDTO convertToDto(HoaDon hd) {
        HoaDonDTO dto = new HoaDonDTO();
        dto.setId(hd.getId());
        // Kiểm tra xem BanAn có tồn tại không trước khi gọi getTenBan
        dto.setTenBan(hd.getTenBan());
        dto.setBanId(hd.getBanId()); // Đảm bảo gán đúng ID
        dto.setNgayTao(hd.getNgayTao());
        // Thêm trường tongTien nếu cần
        dto.setTongTien(hd.getTongTien());
        return dto;
    }
}