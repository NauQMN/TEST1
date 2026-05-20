    package com.e0bmanager.server_api.services;

    import com.e0bmanager.server_api.dto.OrderRequestDTO;
    import com.e0bmanager.server_api.dto.SplitOrMergeRequest;
    import com.e0bmanager.server_api.models.*;
    import com.e0bmanager.server_api.repositories.*;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.LocalDateTime;

    @Service
    public class OrderService {

        @Autowired private HoaDonRepository hoaDonRepo;
        @Autowired private ChiTietHoaDonRepository ctRepo;
        @Autowired private BanAnRepository banRepo;
        @Autowired private SanPhamRepository sanPhamRepo;
        @Autowired
        private HoaDonRepository hoaDonRepository;
        @Autowired
        private BanAnRepository banAnRepository;
        @Autowired
        private DoanhThuRepository doanhThuRepository;

        @Transactional
        public void processOrder(OrderRequestDTO request) {
            // 1. Tìm hoặc tạo hóa đơn (Trạng thái 0 = Đang phục vụ)
            HoaDon hoadon = hoaDonRepo.findByBanIdAndTrangThai(request.getTableId(), 0)
                    .orElseGet(() -> {
                        HoaDon newHD = new HoaDon();
                        newHD.setBanId(request.getTableId());
                        newHD.setNhanVienId(request.getStaffId());
                        newHD.setTrangthai(0);
                        newHD.setNgayTao(LocalDateTime.now());
                        newHD.setTongTien(0.0);
                        return hoaDonRepo.save(newHD);
                    });

            double addAmount = 0;

            // 2. Duyệt danh sách món ăn từ DTO
            for (var itemDTO : request.getItems()) {
                SanPham sp = sanPhamRepo.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Món không tồn tại: " + itemDTO.getProductId()));

                // Tạo mới chi tiết hóa đơn
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setHoaDonId(hoadon.getId()); // Gắn vào hóa đơn cha
                ct.setSanPham(sp);             // Gắn sản phẩm
                ct.setSoLuong(itemDTO.getQuantity());

                // Chuyển BigDecimal sang Double để tính toán tổng tiền (hoặc ngược lại tùy Entity của bạn)
                double giaBan = sp.getGia().doubleValue();
                ct.setGiaLucBan(BigDecimal.valueOf(giaBan));

                ctRepo.save(ct);
                addAmount += (giaBan * itemDTO.getQuantity());
            }

            // 3. Cập nhật tổng tiền hóa đơn
            hoadon.setTongTien(hoadon.getTongTien() + addAmount);
            hoaDonRepo.save(hoadon);

            // 4. Cập nhật trạng thái bàn ăn (1 = Đã có khách)
            BanAn ban = banRepo.findById(request.getTableId())
                    .orElseThrow(() -> new RuntimeException("Bàn không tồn tại"));
            ban.setTrangthai(1);
            banRepo.save(ban);
        }
        // Trong OrderService.java
        @Transactional
        public void handleSplitOrMerge(SplitOrMergeRequest request) {
            // 1. Lấy Hóa đơn nguồn
            HoaDon sourceHoaDon = hoaDonRepo.findById(request.getSourceHoaDonId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn nguồn"));

            // 2. Lấy Hóa đơn đích (tìm theo TableId và trạng thái active)
            HoaDon targetHoaDon = hoaDonRepo.findByBanIdAndTrangThai(request.getTargetTableId(), 1)
                    .orElse(new HoaDon()); // Tạo mới nếu chưa có

            // 3. Logic chuyển món
            for (SplitOrMergeRequest.ItemDetail detail : request.getItemsToProcess()) {
                // - Trừ số lượng ở sourceHoaDon
                // - Cộng số lượng vào targetHoaDon
                // - Nếu số lượng ở source = 0, xóa item đó khỏi source
            }

            hoaDonRepo.save(sourceHoaDon);
            hoaDonRepo.save(targetHoaDon);
        }
        @Transactional
        public void handleCheckout(int hoaDonId) {
            // 1. Tìm hóa đơn
            HoaDon hd = hoaDonRepository.findById(hoaDonId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn!"));

            // 2. Lấy số tiền thực tế từ hóa đơn (Giả sử bạn đã tính tổng ở các bước trước)
            double totalAmount = hd.getTongTien();

            // 3. Cập nhật trạng thái hóa đơn sang "Đã thanh toán" (thường dùng mã 2)
            hd.setTrangthai(2);
            hd.setThoiGianThanhToan(java.time.LocalDateTime.now());
            hoaDonRepository.save(hd);

            // 4. RESET TRẠNG THÁI BÀN VỀ 0 (TRỐNG)
            BanAn ban = banAnRepository.findById(hd.getBanId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn!"));
            ban.setTrangthai(0); // Về trạng thái trống
            banAnRepository.save(ban);
            // 5. CẬP NHẬT DOANH THU THEO NGÀY
            LocalDate today = LocalDate.now();
            DoanhThu dt = doanhThuRepository.findByNgay(today)
                    .orElse(new DoanhThu(today, 0.0, 0));

            dt.setTongDoanhThu(dt.getTongDoanhThu() + totalAmount);
            dt.setSoLuongDon(dt.getSoLuongDon() + 1);
            doanhThuRepository.save(dt);
        }
    }