package e0bmanager.panels;

import e0bmanager.client.UserClient;
import e0bmanager.dto.NhanVienDTO;
import e0bmanager.ui.*;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmployeePanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private JFrame parentFrame;
    ActionListener navigationListener;
    private UserClient userClient = new UserClient();
    private JButton btnApprove;

    public EmployeePanel(JFrame parent, ActionListener navigationListener) {
        this.parentFrame = parent;
        this.navigationListener = navigationListener;
        initComponents();
        loadEmployeeData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20)); // Tăng khoảng cách giữa các khối
        setBorder(new EmptyBorder(30, 35, 25, 35)); // Lề tổng thể rộng rãi, thoáng hơn
        setBackground(new Color(245, 247, 250)); // Màu nền xám nhạt hiện đại

        // --- THANH CÔNG CỤ (NORTH) ---
        JPanel pnlNorth = new JPanel(new BorderLayout(0, 20));
        pnlNorth.setOpaque(false); // Xuyên thấu để lấy màu nền tổng thể

        // Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59)); // Màu chữ tối, sang trọng
        pnlNorth.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlActions = new JPanel(new BorderLayout(15, 0));
        pnlActions.setOpaque(false);

        // Thanh tìm kiếm với FlatLaf
        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(350, 42));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🔍 Nhập tên hoặc SĐT để tìm kiếm...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        // Bo góc tròn trịa và thêm padding bên trong
        txtSearch.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 20;"
                + "margin: 0, 15, 0, 15;"
                + "focusColor: #3498db;"
                + "borderColor: #d1d5db;");

        // Giữ nguyên logic tìm kiếm
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String query = txtSearch.getText().toLowerCase();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
                table.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
            }
        });
        pnlActions.add(txtSearch, BorderLayout.WEST);

        // Các nút hành động
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlButtons.setOpaque(false);

        btnApprove = createStyledButton("📋 Chờ duyệt (0)", new Color(59, 130, 246), Color.WHITE);
        JButton btnEdit = createStyledButton("✎ Sửa", new Color(245, 158, 11), Color.WHITE);
        JButton btnDelete = createStyledButton("🗑 Xóa", new Color(239, 68, 68), Color.WHITE);

        pnlButtons.add(btnApprove);
        pnlButtons.add(btnEdit);
        pnlButtons.add(btnDelete);
        pnlActions.add(pnlButtons, BorderLayout.EAST);

        pnlNorth.add(pnlActions, BorderLayout.CENTER);

        // --- BẢNG DỮ LIỆU (CENTER) ---
        String[] columns = { "ID", "Họ tên", "Ngày sinh", "Lương (VNĐ)", "Chức vụ", "SĐT" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        // Giao diện Bảng (JTable) - Cải tiến mạnh mẽ
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45); // Tăng chiều cao hàng

        // Sử dụng FlatLaf properties để làm đẹp bảng
        table.putClientProperty(FlatClientProperties.STYLE, ""
                + "showHorizontalLines: true;"
                + "showVerticalLines: false;"
                + "intercellSpacing: 0, 0;"
                + "selectionBackground: #eff6ff;" // Màu xanh lơ khi chọn
                + "selectionForeground: #1e3a8a;"
        ); // Hàng màu xen kẽ
        table.putClientProperty("JTable.alternateRowColor", new Color(248, 250, 252));
        table.setGridColor(new Color(226, 232, 240)); // Màu kẻ ngang nhạt
        table.setFocusable(false);

        // Giao diện Header của Bảng
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(71, 85, 105));
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        // Viền dưới cho header
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(203, 213, 225)));

        // Căn lề cho các cột (Cực kỳ quan trọng để UI trông chuyên nghiệp)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID căn giữa
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // SĐT căn giữa
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);  // Lương căn phải

        // Bọc Table trong ScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Bỏ viền mặc định của ScrollPane
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Tạo một Panel "Card" để chứa ScrollPane (Tạo khối trắng bo góc)
        JPanel pnlCard = new JPanel(new BorderLayout());
        pnlCard.setBackground(Color.WHITE);
        pnlCard.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 20;" // Bo góc thẻ
                + "border: 1,1,1,1, #e2e8f0,, 20;"); // Viền mỏng màu xám nhạt
        pnlCard.add(scrollPane, BorderLayout.CENTER);

        // Thêm khoảng cách (padding) bên trong thẻ Card
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(10, 0, 10, 0));
        cardWrapper.add(pnlCard, BorderLayout.CENTER);

        // Bọc toàn bộ nội dung (tiêu đề + bảng) vào một panel rồi đặt vào JScrollPane
        JPanel pnlScrollContent = new JPanel(new BorderLayout(20, 20));
        pnlScrollContent.setOpaque(false);
        pnlScrollContent.add(pnlNorth, BorderLayout.NORTH);
        pnlScrollContent.add(cardWrapper, BorderLayout.CENTER);

        JScrollPane mainScroll = new JScrollPane(pnlScrollContent);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getViewport().setBackground(new Color(245, 247, 250));
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(mainScroll, BorderLayout.CENTER);

        // --- XỬ LÝ SỰ KIỆN (Giữ nguyên 100%) ---
        btnApprove.addActionListener(e -> {
            new DuyetNhanVienDialog((JFrame) SwingUtilities.getWindowAncestor(this), this).setVisible(true);
            updatePendingCount();
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) table.getValueAt(row, 0);
            String ten = table.getValueAt(row, 1).toString();
            // Bảng lưu ngày dạng "dd/MM/yyyy", parse lại thành Date cho JDateChooser
            Date ngay = null;
            try {
                String ngayStr = table.getValueAt(row, 2).toString();
                if (!ngayStr.isEmpty())
                    ngay = new SimpleDateFormat("dd/MM/yyyy").parse(ngayStr);
            } catch (Exception ex) { ex.printStackTrace(); }
            double luong = Double.parseDouble(table.getValueAt(row, 3).toString().replace(",", ""));
            String cv = table.getValueAt(row, 4).toString();
            String sdt = table.getValueAt(row, 5).toString();

            new SuaNhanVienDialog((MainForm) parentFrame, this, id, ten, ngay, luong, cv, sdt).setVisible(true);
            loadEmployeeData();
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) table.getValueAt(row, 0);
            String ten = table.getValueAt(row, 1).toString();
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên: " + ten + "?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                deleteEmployee(id);
            }
        });
    }

    // ĐÃ SỬA: Tính toán mã màu trực tiếp để hiệu ứng hover hoạt động chính xác
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(135, 42));

        // Chuyển đổi màu nền sang mã HEX
        String bgHex = String.format("#%02x%02x%02x", bg.getRed(), bg.getGreen(), bg.getBlue());

        // Sử dụng mã màu HEX trực tiếp thay vì biến $Button.background
        btn.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 15;"
                + "borderWidth: 0;"
                + "focusWidth: 0;"
                + "hoverBackground: lighten(" + bgHex + ", 10%);"
                + "pressedBackground: darken(" + bgHex + ", 10%);");
        return btn;
    }

    // ĐÃ SỬA: Cập nhật lại hiệu ứng hover khi nút đổi màu đỏ/xanh
    public void updatePendingCount() {
        new Thread(() -> {
            int count = userClient.getPendingCount();
            SwingUtilities.invokeLater(() -> {
                btnApprove.setText("📋 Chờ duyệt (" + count + ")");

                Color newBg = count > 0 ? new Color(239, 68, 68) : new Color(59, 130, 246);
                btnApprove.setBackground(newBg);

                String bgHex = String.format("#%02x%02x%02x", newBg.getRed(), newBg.getGreen(), newBg.getBlue());

                btnApprove.putClientProperty(FlatClientProperties.STYLE, ""
                        + "arc: 15;"
                        + "borderWidth: 0;"
                        + "focusWidth: 0;"
                        + "hoverBackground: lighten(" + bgHex + ", 10%);"
                        + "pressedBackground: darken(" + bgHex + ", 10%);");
            });
        }).start();
    }

    public void loadEmployeeData() {
        tableModel.setRowCount(0);
        List<NhanVienDTO> list = userClient.getAllEmployees();

        if (list != null && !list.isEmpty()) {
            SimpleDateFormat displayFmt = new SimpleDateFormat("dd/MM/yyyy");
            for (NhanVienDTO nv : list) {
                // Format ngày "yyyy-MM-dd" từ server thành "dd/MM/yyyy" để hiển thị
                String ngaySinhHienThi = "";
                if (nv.getNgaySinh() != null && !nv.getNgaySinh().isEmpty()) {
                    try {
                        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(nv.getNgaySinh());
                        ngaySinhHienThi = displayFmt.format(d);
                    } catch (Exception ex) {
                        ngaySinhHienThi = nv.getNgaySinh(); // fallback
                    }
                }
                tableModel.addRow(new Object[] {
                        nv.getId(),
                        nv.getHoTen(),
                        ngaySinhHienThi,
                        String.format("%,.0f", nv.getLuong()),
                        nv.getChucVu(),
                        nv.getSdt()
                });
            }
        }
    }

    private void deleteEmployee(int id) {
        if (userClient.deleteEmployee(id)) {
            loadEmployeeData();
            JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi: Không thể xóa nhân viên này!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}