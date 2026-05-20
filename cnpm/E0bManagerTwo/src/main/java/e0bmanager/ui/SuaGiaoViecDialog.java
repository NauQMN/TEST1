package e0bmanager.ui;

import e0bmanager.database.DatabaseConnection;
import e0bmanager.panels.GiaoViecPanel;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SuaGiaoViecDialog extends JDialog {
    private JTextField txtTenViec;
    private JTextArea txtMoTa;
    private JComboBox<String> cbTrangThai;
    private int idTask;
    private GiaoViecPanel parentPanel;

    // Chỉnh sửa Constructor để nhận vào JPanel (GiaoViecPanel)
    public SuaGiaoViecDialog(JPanel parent, int idTask, String tenCu, String moTaCu, String trangThaiCu) {
        // Tự động tìm JFrame/JDialog tổ tiên để làm chủ thể
        super((Window) SwingUtilities.getWindowAncestor(parent), "Chỉnh sửa công việc", ModalityType.APPLICATION_MODAL);
        this.idTask = idTask;
        this.parentPanel = (GiaoViecPanel) parent;

        initComponents(tenCu, moTaCu, trangThaiCu);

        setSize(450, 400);
        setLocationRelativeTo(parent);
    }

    private void initComponents(String tenCu, String moTaCu, String trangThaiCu) {
        setLayout(new BorderLayout(10, 10));

        // --- PHẦN NHẬP LIỆU ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Tên công việc
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Tên công việc:"), gbc);
        txtTenViec = new JTextField(tenCu);
        gbc.gridy = 1;
        pnlForm.add(txtTenViec, gbc);

        // Trạng thái
        gbc.gridy = 2;
        pnlForm.add(new JLabel("Trạng thái:"), gbc);
        cbTrangThai = new JComboBox<>(new String[]{"Chưa xong", "Đang làm", "Hoàn thành"});
        cbTrangThai.setSelectedItem(trangThaiCu);
        gbc.gridy = 3;
        pnlForm.add(cbTrangThai, gbc);

        // Mô tả
        gbc.gridy = 4;
        pnlForm.add(new JLabel("Mô tả:"), gbc);
        txtMoTa = new JTextArea(moTaCu, 5, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        pnlForm.add(new JScrollPane(txtMoTa), gbc);

        add(pnlForm, BorderLayout.CENTER);

        // --- NÚT BẤM ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnUpdate = new JButton("Cập nhật thay đổi");
        btnUpdate.setBackground(new Color(241, 196, 15)); // Màu vàng đặc trưng cho nút Sửa
        btnUpdate.setFocusPainted(false);

        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dispose());

        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnHuy);
        add(pnlButtons, BorderLayout.SOUTH);

        btnUpdate.addActionListener(e -> updateTask());
    }

    private void updateTask() {
        if (txtTenViec.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên công việc không được để trống!");
            return;
        }

        // Sử dụng Text Block JDK 21
        String sql = """
                     UPDATE giao_viec 
                     SET ten_cong_viec = ?, mo_ta = ?, trang_thai = ? 
                     WHERE id = ?
                     """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, txtTenViec.getText().trim());
            pst.setString(2, txtMoTa.getText().trim());
            pst.setString(3, cbTrangThai.getSelectedItem().toString());
            pst.setInt(4, idTask);

            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật công việc thành công!");

                // Gọi hàm nạp lại dữ liệu trên Panel cha
                if (parentPanel != null) {
                    parentPanel.loadTaskData();
                }
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}