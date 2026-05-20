package com.e0bmanager.server_api.controllers;

import com.e0bmanager.server_api.dto.DashboardStatsDTO;
import com.e0bmanager.server_api.dto.RevenuechartDTO;
import com.e0bmanager.server_api.models.DoanhThu;
import com.e0bmanager.server_api.repositories.DoanhThuRepository;
import com.e0bmanager.server_api.repositories.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private HoaDonRepository hoadonRepo;
    @Autowired
    private DoanhThuRepository doanhThuRepo;

    @GetMapping("/stats/by-date")
    public DashboardStatsDTO getStatsByDate(@RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        Optional<DoanhThu> data = doanhThuRepo.findByNgay(date);

        // Tính thêm doanh thu tháng từ ngày đã chọn
        Double monthRevenue = doanhThuRepo.sumByMonth(date.getMonthValue(), date.getYear());

        if (data.isPresent()) {
            DoanhThu dt = data.get();
            return new DashboardStatsDTO(
                    monthRevenue != null ? monthRevenue : 0.0, // Đưa vào trường tổng tháng
                    dt.getTongDoanhThu(),
                    0.0,
                    Long.valueOf(dt.getSoLuongDon()),
                    0L
            );
        }
        return new DashboardStatsDTO(monthRevenue != null ? monthRevenue : 0.0, 0.0, 0.0, 0L, 0L);
    }
    @GetMapping("/stats/today")
    public DashboardStatsDTO getTodayStats() {
        LocalDate today = LocalDate.now();

        // 1. Lấy dữ liệu đã chốt từ bảng doanh_thu
        Optional<DoanhThu> dailyRecord = doanhThuRepo.findByNgay(today);
        double daRut = dailyRecord.map(DoanhThu::getTongDoanhThu).orElse(0.0);
        long soHdDaRut = dailyRecord.map(DoanhThu::getSoLuongDon).orElse(0).longValue();

        // 2. Lấy dữ liệu đang mở từ bảng hoa_don
        Double chuaRut = hoadonRepo.sumTongTienDangMo(today);
        Long soHdChuaRut = hoadonRepo.countHoaDonDangMo(today);

        chuaRut = (chuaRut != null) ? chuaRut : 0.0;
        soHdChuaRut = (soHdChuaRut != null) ? soHdChuaRut : 0L;

        // 3. Trả về DTO tổng hợp
        return new DashboardStatsDTO(
                daRut + chuaRut, // Tổng = Đã rút + Đang mở
                daRut,
                chuaRut,
                soHdDaRut,
                soHdChuaRut
        );
    }
    @GetMapping("/chart/7days")
    public List<RevenuechartDTO> getChartData(@RequestParam("date") String dateStr) {
        LocalDate endDate = LocalDate.parse(dateStr);
        LocalDate startDate = endDate.minusDays(6); // Lấy 7 ngày: startDate -> endDate
        DateTimeFormatter displayFmt = DateTimeFormatter.ofPattern("dd/MM");

        // Lấy dữ liệu đã có trong DB cho khoảng ngày này
        List<DoanhThu> dbData = doanhThuRepo.findByNgayBetweenOrderByNgayAsc(startDate, endDate);

        // Tạo map để tra cứu nhanh theo ngày
        Map<LocalDate, DoanhThu> dataMap = dbData.stream()
                .collect(Collectors.toMap(DoanhThu::getNgay, d -> d));

        // Tạo danh sách đầy đủ 7 ngày, ngày nào không có dữ liệu thì để 0
        List<RevenuechartDTO> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = startDate.plusDays(i);
            DoanhThu dt = dataMap.get(day);
            result.add(new RevenuechartDTO(
                    day.format(displayFmt),
                    dt != null ? dt.getTongDoanhThu() : 0.0,
                    dt != null ? dt.getSoLuongDon() : 0
            ));
        }
        return result;
    }
}