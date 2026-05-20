package e0bmanager.ui;

import com.toedter.calendar.JDateChooser;
import e0bmanager.client.UserClient;
import e0bmanager.dto.NhanVienDTO;
import e0bmanager.panels.EmployeePanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;

public class SuaNhanVienDialog extends JDialog {
    private JTextField txtTen, txtLuong, txtChucVu, txtSdt;
    private JDateChooser jdNgaySinh;
    private JButton btnLuu, btnHuy;
    private int employeeId;
    private UserClient userClient = new UserClient();
    private EmployeePanel employeePanel; // Dùng EmployeePanel để gọi load dữ liệu sau khi sửa

    public SuaNhanVienDialog(MainForm parent, EmployeePanel employeePanel, int id, String ten, Date ngaySinh, double luong, String chucVu, String sdt) {
        super(parent, "Sửa thông tin nhân viên", true);
        this.employeeId = id;
        this.employeePanel = employeePanel;

        initComponents();

        // Đổ dữ liệu cũ vào form
        txtTen.setText(ten);
        jdNgaySinh.setDate(ngaySinh);
        txtLuong.setText(String.valueOf(luong));
        txtChucVu.setText(chucVu);
        txtSdt.setText(sdt);

        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        // Sử dụng JPanel làm container để set lề đẹp hơn
        JPanel pnlMain = new JPanel(new GridLayout(6, 2, 10, 10));
        pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));

        pnlMain.add(new JLabel("Họ tên:"));
        txtTen = new JTextField(20);
        pnlMain.add(txtTen);

        pnlMain.add(new JLabel("Ngày sinh:"));
        jdNgaySinh = new JDateChooser();
        jdNgaySinh.setDateFormatString("dd/MM/yyyy");
        pnlMain.add(jdNgaySinh);

        pnlMain.add(new JLabel("Lương:"));
        txtLuong = new JTextField();
        pnlMain.add(txtLuong);

        pnlMain.add(new JLabel("Chức vụ:"));
        txtChucVu = new JTextField();
        pnlMain.add(txtChucVu);

        pnlMain.add(new JLabel("Số điện thoại:"));
        txtSdt = new JTextField();
        pnlMain.add(txtSdt);

        btnLuu = new JButton("Cập nhật");
        btnLuu.setBackground(new Color(46, 204, 113));
        btnLuu.setForeground(Color.WHITE);

        btnHuy = new JButton("Hủy");

        pnlMain.add(btnLuu);
        pnlMain.add(btnHuy);

        add(pnlMain);

        btnLuu.addActionListener(e -> updateEmployee());
        btnHuy.addActionListener(e -> dispose());
    }

    private void updateEmployee() {
        try {
            // 1. Thu thập dữ liệu vào DTO
            NhanVienDTO nv = new NhanVienDTO();
            nv.setHoTen(txtTen.getText().trim());
            nv.setNgaySinhFromDate(jdNgaySinh.getDate()); // convert Date → "yyyy-MM-dd"
            nv.setChucVu(txtChucVu.getText().trim());
            nv.setSdt(txtSdt.getText().trim());

            try {
                double luong = Double.parseDouble(txtLuong.getText().replace(",", ""));
                nv.setLuong(luong);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Lương phải là con số!");
                return;
            }

            // 2. Gọi API thông qua UserClient
            boolean success = userClient.updateEmployee(employeeId, nv);

            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                if (employeePanel != null) {
                    employeePanel.loadEmployeeData(); // Làm mới bảng ở EmployeePanel
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại qua Server!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}