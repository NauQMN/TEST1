package e0bmanager.ui;

import com.toedter.calendar.JDateChooser;
import e0bmanager.database.DatabaseConnection;
import e0bmanager.panels.GiaoViecPanel;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class GiaoViecMoiDialog extends JDialog {
    private JDateChooser dcNgay;
    private JComboBox<String> cbCa, cbNhanVien;
    private JTextField txtTenViec;
    private JTextArea txtMoTa;
    private ArrayList<Integer> listIdNhanVien = new ArrayList<>();
    private GiaoViecPanel parentPanel; // Dùng để load lại bảng sau khi lưu

    public GiaoViecMoiDialog(JPanel parent) {
        // Tìm JFrame chứa Panel này
        super((Window) SwingUtilities.getWindowAncestor(parent), "Giao công việc mới", ModalityType.APPLICATION_MODAL);
        this.parentPanel = (GiaoViecPanel) parent;

        initComponents();

        setSize(480, 550);
        setLocationRelativeTo(parent);

        // Load lần đầu
        loadNhanVienByLich();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // 1. Chọn ngày
        gbc.gridx = 0; gbc.gridy = 0;
        pnlMain.add(new JLabel("Chọn ngày:"), gbc);
        dcNgay = new JDateChooser(new java.util.Date());
        dcNgay.setDateFormatString("dd/MM/yyyy");
        gbc.gridy = 1;
        pnlMain.add(dcNgay, gbc);

        // 2. Chọn ca
        gbc.gridy = 2;
        pnlMain.add(new JLabel("Chọn ca làm:"), gbc);
        cbCa = new JComboBox<>(new String[]{"Ca Sáng (08:00 - 12:00)", "Ca Chiều (12:00 - 18:00)", "Ca Tối (18:00 - 23:00)"});
        gbc.gridy = 3;
        pnlMain.add(cbCa, gbc);

        // 3. Chọn nhân viên
        gbc.gridy = 4;
        pnlMain.add(new JLabel("Nhân viên trực ca (Lấy từ lịch làm việc):"), gbc);
        cbNhanVien = new JComboBox<>();
        gbc.gridy = 5;
        pnlMain.add(cbNhanVien, gbc);

        // 4. Tên công việc
        gbc.gridy = 6;
        pnlMain.add(new JLabel("Tên công việc:"), gbc);
        txtTenViec = new JTextField();
        gbc.gridy = 7;
        pnlMain.add(txtTenViec, gbc);

        // 5. Mô tả
        gbc.gridy = 8;
        pnlMain.add(new JLabel("Mô tả chi tiết:"), gbc);
        txtMoTa = new JTextArea(4, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        gbc.gridy = 9;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        pnlMain.add(new JScrollPane(txtMoTa), gbc);

        add(pnlMain, BorderLayout.CENTER);

        // 6. Nút bấm (South)
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Xác nhận Giao việc");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);

        JButton btnCancel = new JButton("Hủy");

        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);
        add(pnlButtons, BorderLayout.SOUTH);

        // --- SỰ KIỆN ---
        dcNgay.addPropertyChangeListener("date", e -> loadNhanVienByLich());
        cbCa.addActionListener(e -> loadNhanVienByLich());
        btnSave.addActionListener(e -> saveTask());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadNhanVienByLich() {
        if (dcNgay.getDate() == null) return;

        cbNhanVien.removeAllItems();
        listIdNhanVien.clear();

        // Sử dụng Text Block (JDK 21)
        String sql = """
            SELECT nv.id, nv.ho_ten 
            FROM LICH_LAM_VIEC l 
            JOIN nhanvien nv ON l.nhan_vien_id = nv.id 
            WHERE l.ngay_lam = ? AND l.ca_lam = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setDate(1, new java.sql.Date(dcNgay.getDate().getTime()));
            pst.setString(2, cbCa.getSelectedItem().toString());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                listIdNhanVien.add(rs.getInt("id"));
                cbNhanVien.addItem(rs.getString("ho_ten"));
            }

            if (cbNhanVien.getItemCount() == 0) {
                cbNhanVien.addItem("-- Không có nhân viên trực ca này --");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveTask() {
        if (listIdNhanVien.isEmpty() || cbNhanVien.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Không có nhân viên hợp lệ để giao việc!");
            return;
        }
        if (txtTenViec.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên công việc!");
            return;
        }

        String sql = """
            INSERT INTO giao_viec (id_nhan_vien, ten_cong_viec, mo_ta, ngay_thuc_hien, ca_lam_viec, trang_thai) 
            VALUES (?, ?, ?, ?, ?, 'Chưa xong')
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            int index = cbNhanVien.getSelectedIndex();
            // Phòng trường hợp chọn dòng "-- Không có nhân viên --"
            if (index >= listIdNhanVien.size()) return;

            pst.setInt(1, listIdNhanVien.get(index));
            pst.setString(2, txtTenViec.getText().trim());
            pst.setString(3, txtMoTa.getText().trim());
            pst.setDate(4, new java.sql.Date(dcNgay.getDate().getTime()));
            pst.setString(5, cbCa.getSelectedItem().toString());

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Giao việc thành công!");

            // Gọi hàm load lại bảng của GiaoViecPanel
            if (parentPanel != null) {
                parentPanel.loadTaskData();
            }
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
}