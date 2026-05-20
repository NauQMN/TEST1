package e0bmanager.ui;

import e0bmanager.client.UserClient;
import e0bmanager.dto.NhanVienDTO;
import e0bmanager.panels.EmployeePanel;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DuyetNhanVienDialog extends JDialog {
    private JTable table;
    private DefaultTableModel model;
    private UserClient userClient = new UserClient();
    private EmployeePanel mainPanel;

    public DuyetNhanVienDialog(JFrame parent, EmployeePanel mainPanel) {
        super(parent, "Danh sách nhân viên chờ duyệt", true);
        this.mainPanel = mainPanel; // Lưu lại để dùng cho refresh

        setSize(900, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header
        String[] cols = {"ID Acc", "Họ tên", "Tài khoản", "Chức vụ", "Ngày sinh", "SĐT"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(30);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnAccept = new JButton("Duyệt Tài Khoản");
        JButton btnReject = new JButton("Từ Chối/Xóa");

        btnAccept.setBackground(new Color(46, 204, 113));
        btnAccept.setForeground(Color.WHITE);
        btnReject.setBackground(new Color(231, 76, 60));
        btnReject.setForeground(Color.WHITE);

        pnlActions.add(btnReject);
        pnlActions.add(btnAccept);
        add(pnlActions, BorderLayout.SOUTH);

        // --- EVENTS ---

        // Duyệt
        btnAccept.addActionListener(e -> handleApprove());

        // Từ chối (Xóa hoàn toàn yêu cầu)
        btnReject.addActionListener(e -> handleReject());

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        // Lưu ý: UserClient.getPendingAccounts() bây giờ sẽ nhận về JSON chứa đủ hoTen, sdt...
        List<NhanVienDTO> list = userClient.getPendingAccounts();

        if (list != null) {
            for (NhanVienDTO nv : list) {
                model.addRow(new Object[]{
                        nv.getAccountID(), // Lấy đúng ID của Account để tí nữa Duyệt/Từ chối
                        nv.getHoTen(),     // Bây giờ hoTen đã có dữ liệu (không còn null)
                        //nv.getUsername(),
                        nv.getChucVu(),
                        nv.getNgaySinh(),
                        nv.getSdt()
                });
            }
        }
    }

    private void handleApprove() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một yêu cầu!");
            return;
        }

        int accId = (int) table.getValueAt(row, 0);
        if (userClient.updateAccountStatus(accId, 1)) {
            JOptionPane.showMessageDialog(this, "Đã duyệt nhân viên thành công!");
            loadData();
            mainPanel.loadEmployeeData(); // Refresh bảng chính ở EmployeePanel
            mainPanel.updatePendingCount(); // Cập nhật lại số badge trên nút
        }
    }

    private void handleReject() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu muốn từ chối!");
            return;
        }

        // 1. Lấy ID an toàn
        Object idObj = table.getValueAt(row, 0);
        if (idObj == null) return;
        int accId = (int) idObj;

        // 2. Lấy tên an toàn (Tránh NullPointerException ở đây)
        Object nameObj = table.getValueAt(row, 1);
        String name = (nameObj != null) ? nameObj.toString() : "Nhân viên không tên";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn TỪ CHỐI và XÓA yêu cầu của: " + name + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Thực hiện lệnh xóa như cũ
            if (userClient.rejectRequest(accId)) {
                JOptionPane.showMessageDialog(this, "Đã xóa yêu cầu đăng ký.");
                loadData();
                mainPanel.updatePendingCount();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa yêu cầu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
