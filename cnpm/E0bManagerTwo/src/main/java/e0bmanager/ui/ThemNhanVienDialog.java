package e0bmanager.ui;

import com.toedter.calendar.JDateChooser;
import e0bmanager.client.UserClient;
import e0bmanager.dto.NhanVienDTO;
import e0bmanager.panels.EmployeePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;

public class ThemNhanVienDialog extends JDialog {
    private JTextField txtTen, txtLuong, txtChucVu, txtSdt;
    private JDateChooser jdNgaySinh;
    private JButton btnLuu, btnHuy;
    private EmployeePanel employeePanel;
    private UserClient userClient = new UserClient(); // Khởi tạo Client

    public ThemNhanVienDialog(JFrame owner, EmployeePanel employeePanel) {
        super(owner, "Thêm nhân viên mới", true);
        this.employeePanel = employeePanel;
        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel pnlMain = new JPanel(new GridLayout(6, 2, 10, 15));
        pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));

        pnlMain.add(new JLabel("Họ tên:"));
        txtTen = new JTextField(20);
        pnlMain.add(txtTen);

        pnlMain.add(new JLabel("Ngày sinh:"));
        jdNgaySinh = new JDateChooser();
        jdNgaySinh.setDateFormatString("dd/MM/yyyy");
        jdNgaySinh.setDate(new Date());
        pnlMain.add(jdNgaySinh);

        pnlMain.add(new JLabel("Lương cơ bản:"));
        txtLuong = new JTextField();
        pnlMain.add(txtLuong);

        pnlMain.add(new JLabel("Chức vụ:"));
        txtChucVu = new JTextField();
        pnlMain.add(txtChucVu);

        pnlMain.add(new JLabel("Số điện thoại:"));
        txtSdt = new JTextField();
        pnlMain.add(txtSdt);

        btnLuu = new JButton("Lưu dữ liệu");
        btnLuu.setBackground(new Color(46, 204, 113));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);

        btnHuy = new JButton("Hủy bỏ");
        btnHuy.addActionListener(e -> dispose());

        pnlMain.add(btnLuu);
        pnlMain.add(btnHuy);

        add(pnlMain);
        btnLuu.addActionListener(e -> saveEmployee());
    }

    private void saveEmployee() {
        // 1. Validation
        if (txtTen.getText().isBlank() || txtLuong.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Họ tên và Lương!");
            return;
        }

        try {
            // 2. Thu thập dữ liệu vào DTO
            NhanVienDTO nv = new NhanVienDTO();
            nv.setHoTen(txtTen.getText().trim());
            nv.setNgaySinh(String.valueOf(jdNgaySinh.getDate()));
            nv.setChucVu(txtChucVu.getText().trim());
            nv.setSdt(txtSdt.getText().trim());
            nv.setTrangThai("Đang làm việc"); // Trạng thái mặc định

            try {
                double luong = Double.parseDouble(txtLuong.getText().replace(",", ""));
                nv.setLuong(luong);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Lương phải là một con số!");
                return;
            }

            // 3. Gọi API qua UserClient
            boolean success = userClient.addEmployee(nv);

            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                if (employeePanel != null) {
                    employeePanel.loadEmployeeData(); // Reload lại bảng ở Panel chính
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể thêm nhân viên qua Server.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}