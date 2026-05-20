package com.e0bmanager.server_api.services;

import com.e0bmanager.server_api.dto.AccountRequestDTO;
import com.e0bmanager.server_api.dto.RegisterRequest;
import com.e0bmanager.server_api.models.NhanVien;
import com.e0bmanager.server_api.models.OrderAccount;
import com.e0bmanager.server_api.repositories.NhanVienRepository;
import com.e0bmanager.server_api.repositories.OrderAccountRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private NhanVienRepository nhanVienRepo;
    @Autowired
    private OrderAccountRepository accountRepo;
    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public void registerStaff(RegisterRequest req) throws Exception {
        // 1. Lưu nhân viên mới
        NhanVien nv = new NhanVien();
        nv.setHoTen(req.getHoTen());
        nv.setSdt(req.getSdt());
        nv.setChucVu(req.getChucVu());
        nv.setLuong(req.getLuong());
        nv.setNgaySinh(LocalDate.parse(req.getNgaySinh()));
        nv.setEmail(req.getEmail());

        // saveAndFlush để ID của NV được sinh ra ngay lập tức
        NhanVien savedNv = nhanVienRepo.saveAndFlush(nv);

        // 2. Tạo tài khoản và gán nhan_vien_id của nhân viên vừa tạo
        OrderAccount acc = new OrderAccount();
        acc.setUsername(req.getUsername());
        acc.setPassword(req.getPassword());
        acc.setFullname(req.getHoTen());
        acc.setRole("STAFF");
        acc.setStatus(0); // Chờ duyệt

        // ĐÂY LÀ PHẦN QUAN TRỌNG NHẤT
        acc.setNhanVienId(savedNv.getId());

        accountRepo.save(acc);
    }
    @Transactional
    public void rejectRegister(Integer accountId) throws Exception {
        // 1. Tìm tài khoản
        OrderAccount acc = accountRepo.findById(Long.valueOf(accountId))
                .orElseThrow(() -> new Exception("Không tìm thấy tài khoản!"));

        // 2. Lấy ID nhân viên liên quan trước khi xóa account
        Integer nvId = acc.getNhanVienId();

        // 3. Xóa tài khoản
        accountRepo.delete(acc);

        // 4. Xóa nhân viên tương ứng
        if (nvId != null) {
            nhanVienRepo.deleteById(nvId);
        }
    }
    // Trong AuthService.java (Server)
    public List<AccountRequestDTO> getPendingRequests() {
        List<OrderAccount> accounts = accountRepo.findByStatus(0);
        return accounts.stream().map(acc -> {
            AccountRequestDTO dto = new AccountRequestDTO();
            dto.setAccountId(Math.toIntExact(acc.getId()));
            dto.setUsername(acc.getUsername());

            // Tìm thông tin nhân viên tương ứng
            nhanVienRepo.findById(acc.getNhanVienId()).ifPresent(nv -> {
                dto.setHoTen(nv.getHoTen());
                dto.setChucVu(nv.getChucVu());
                dto.setSdt(nv.getSdt());
                dto.setNgaySinh(nv.getNgaySinh());
            });
            return dto;
        }).collect(Collectors.toList());
    }
    @Transactional
    public void approveAccount(Integer accountId) throws Exception {
        // 1. Tìm tài khoản
        OrderAccount acc = accountRepo.findById(Long.valueOf(accountId))
                .orElseThrow(() -> new Exception("Không tìm thấy tài khoản!"));

        // 2. Cập nhật trạng thái thành 1 (Đã duyệt)
        acc.setStatus(1);
        accountRepo.save(acc);

        // 3. Lấy thông tin nhân viên để gửi mail
        NhanVien nv = nhanVienRepo.findById(acc.getNhanVienId()).orElse(null);

        if (nv != null && nv.getEmail() != null && !nv.getEmail().isEmpty()) {
            sendApprovalEmail(nv.getEmail(), nv.getHoTen(), acc.getUsername());
        }
    }

    private void sendApprovalEmail(String toEmail, String hoTen, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("e0bmanager001@gmail.com", "E0b Manager System");
            helper.setTo(toEmail);
            helper.setSubject("CHÚC MỪNG: Tài khoản nhân viên của bạn đã được duyệt!");

            String htmlContent = "<h3>Thân gửi " + hoTen + ",</h3>" +
                    "<p>Yêu cầu đăng ký của bạn đã được <b>phê duyệt thành công</b>.</p>" +
                    "<p>Thông tin đăng nhập:</p>" +
                    "<ul>" +
                    "<li>Tài khoản: <b>" + username + "</b></li>" +
                    "<li>Trạng thái: Hoạt động</li>" +
                    "</ul>" +
                    "<p>Chào mừng bạn gia nhập đội ngũ!</p>";

            helper.setText(htmlContent, true); // true để gửi định dạng HTML

            mailSender.send(message);
            System.out.println("Email đã gửi thành công tới: " + toEmail);
        } catch (Exception e) {
            System.err.println("Lỗi gửi mail: " + e.getMessage());
            e.printStackTrace();
        }
    }
}