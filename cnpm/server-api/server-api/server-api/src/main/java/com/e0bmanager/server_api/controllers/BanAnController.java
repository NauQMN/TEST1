package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.dto.BanAnDTO;
import com.e0bmanager.server_api.models.BanAn;
import com.e0bmanager.server_api.models.HoaDon;
import com.e0bmanager.server_api.repositories.BanAnRepository;
import com.e0bmanager.server_api.repositories.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ban")
@CrossOrigin("*")
public class BanAnController {

    @Autowired
    private BanAnRepository banAnRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    // SỬA TẠI ĐÂY: Thêm @PathVariable và đổi tên biến cho khớp với {idKhuVuc}
    @GetMapping("/khuvuc/{idKhuVuc}")
    public List<BanAnDTO> getTablesByKhuVuc(@PathVariable("idKhuVuc") Integer idKhuVuc) {
        // Sử dụng biến idKhuVuc đã nhận từ URL
        List<BanAn> listBan = banAnRepository.findByKhuVucId(idKhuVuc);
        List<BanAnDTO> dtos = new ArrayList<>();

        for (BanAn ban : listBan) {
            // Sử dụng hàm convertToDto đã có ở dưới
            dtos.add(convertToDto(ban));
        }
        return dtos;
    }
    @GetMapping("/{id}")
    public BanAnDTO getTableById(@PathVariable Integer id) {
        return banAnRepository.findById(id)
                .map(this::convertToDto) // Dùng lại hàm convert có sẵn của bạn
                .orElse(null);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        return banAnRepository.findById(id).map(ban -> {
            ban.setTrangthai(status); // Đảm bảo khớp với biến 'trangthai' trong model BanAn của bạn
            banAnRepository.save(ban);
            return ResponseEntity.ok("Cập nhật trạng thái bàn " + id + " thành " + status);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Hàm ConvertToDto chuẩn hóa
    private BanAnDTO convertToDto(BanAn ban) {
        BanAnDTO dto = new BanAnDTO();
        dto.setId(ban.getId());
        dto.setTen_ban(ban.getTenBan());
        dto.setTrang_thai(ban.getTrangthai());
        dto.setKhu_vuc_id(ban.getKhuVuc() != null ? ban.getKhuVuc().getId() : 0);

        // LOGIC MỚI: Tính tổng tiền nếu bàn đang có khách (trạng thái 1)
        if (ban.getTrangthai() != null && ban.getTrangthai() == 1) {
            hoaDonRepository.findByBanIdAndTrangThai(ban.getId(), 0) // Lưu ý: dùng 0 theo dữ liệu DB của bạn
                    .ifPresent(hd -> {
                        dto.setHoaDonId(hd.getId());

                        // Lấy tong_tien từ bảng hóa đơn
                        dto.setTongTien(hd.getTongTien());
                    });
        } else {
            dto.setTongTien((0.0));
        }

        return dto;
    }
}