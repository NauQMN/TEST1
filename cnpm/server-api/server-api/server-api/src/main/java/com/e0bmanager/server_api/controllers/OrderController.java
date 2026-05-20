package com.e0bmanager.server_api.controllers;


import com.e0bmanager.server_api.dto.*;
import com.e0bmanager.server_api.models.BanAn;
import com.e0bmanager.server_api.models.ChiTietHoaDon;
import com.e0bmanager.server_api.models.HoaDon;
import com.e0bmanager.server_api.repositories.BanAnRepository;
import com.e0bmanager.server_api.repositories.ChiTietHoaDonRepository;
import com.e0bmanager.server_api.repositories.HoaDonRepository;
import com.e0bmanager.server_api.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    HoaDonRepository hoaDonRepo;
    ChiTietHoaDonRepository ctRepo;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ChiTietHoaDonRepository chiTietHoaDonRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    BanAnRepository bananRepo;
    @PostMapping("/send-order")
    public ResponseEntity<?> sendOrder(@RequestBody OrderRequestDTO request) {
        try {
            orderService.processOrder(request);
            return ResponseEntity.ok("Xác nhận đơn hàng thành công!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi xử lý đơn hàng: " + e.getMessage());
        }
    }
    @GetMapping("/active-detail/{tableId}")
    public ResponseEntity<List<CartItemDTO>> getActiveOrderDetail(@PathVariable Integer tableId) {

        // Gọi qua biến instance đã inject, không gọi qua tên Class
        List<ChiTietHoaDon> details = chiTietHoaDonRepository.findActiveDetailsByTableId(tableId);

        List<CartItemDTO> dtoList = details.stream().map(ct -> {
            CartItemDTO dto = new CartItemDTO();
            SanPhamDTO spDto = new SanPhamDTO();

            // Đảm bảo ct.getSanPham() không bị null
            if(ct.getSanPham() != null) {
                spDto.setMaSanPham(ct.getSanPham().getMaSanPham());
                spDto.setTenSanPham(ct.getSanPham().getTenSanPham());
                spDto.setHinhAnh(ct.getSanPham().getHinhAnh());
            }

            // Xử lý chuyển đổi kiểu dữ liệu BigDecimal (nếu Entity là Double)
            if (ct.getGiaLucBan() != null) {

                spDto.setGia(ct.getGiaLucBan());
            }

            dto.setSanPham(spDto);
            dto.setSoLuong(ct.getSoLuong());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
    @GetMapping("/active")
    public ResponseEntity<List<HoaDonDTO>> getActiveInvoices() {
        List<HoaDon> invoices = hoaDonRepository.findActiveInvoices();

        List<HoaDonDTO> dtoList = invoices.stream().map(hd -> {
            HoaDonDTO dto = new HoaDonDTO();
            dto.setId(hd.getId());
            dto.setTongTien(hd.getTongTien());
            dto.setNgayTao(hd.getNgayTao());
            if (hd.getBanAn() != null) {
                String tenTang = (hd.getBanAn().getKhuVuc() != null)
                        ? hd.getBanAn().getKhuVuc().getTenKhuVuc() : "Tầng lạ";
                dto.setTenBan(tenTang + " - " + hd.getBanAn().getTenBan());
            } else {
                //dto.setTenBan("Bàn lạ");
                System.out.println("HoaDon ID " + hd.getId() + " có BanAn bị NULL");
                dto.setTenBan("Lỗi cấu hình bàn");
            }
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Integer id) {
        HoaDon hd = hoaDonRepository.findById(id).orElse(null);
        if (hd != null) {
            // Cập nhật trạng thái thành 2 (Hủy đơn)
            hd.setTrangthai(0);
            hoaDonRepository.save(hd);
            BanAn ban = bananRepo.findById(hd.getBanId()).orElseThrow();
            ban.setTrangthai(0); // Trả bàn về trạng thái trống
            bananRepo.save(ban);
            // Ghi log hoặc gửi thông báo sang E0bManager tại đây sau
            return ResponseEntity.ok("Đơn hàng đã hủy thành công");
        }
        return ResponseEntity.status(404).body("Không tìm thấy đơn hàng");
    }
        @PostMapping("/process-split")
        public ResponseEntity<String> processSplit(@RequestBody SplitOrMergeRequest request) {
            try {
                orderService.handleSplitOrMerge(request);
                return ResponseEntity.ok("Thành công");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    @PostMapping("/checkout/{hoaDonId}") // Đường dẫn phải khớp với Client gọi
    public ResponseEntity<?> checkout(@PathVariable("hoaDonId") int hoaDonId) {
        try {
            orderService.handleCheckout(hoaDonId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace(); // Xem log lỗi tại đây nếu Service bị crash
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    }
