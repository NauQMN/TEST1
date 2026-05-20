package e0bmanager.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatClientProperties;
import com.toedter.calendar.JDateChooser;

import e0bmanager.client.UserClient;
import e0bmanager.dto.GiaoViecDTO;
import e0bmanager.ui.GiaoViecMoiDialog;
import e0bmanager.ui.SuaGiaoViecDialog;

public class GiaoViecPanel extends JPanel {
    private DefaultTableModel taskModel;
    private JTable tblTasks;
    private JDateChooser dcSchedule;
    private UserClient userClient = new UserClient();
    private ActionListener navigationListener;

    public GiaoViecPanel(JFrame parentFrame, ActionListener navigationListener) {
        this.navigationListener = navigationListener;
        initComponents();
        loadTaskData(); // Mặc định load theo ngày hiện tại trên JDateChooser
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 30, 20, 30)); // Căn lề rộng rãi
        setBackground(new Color(245, 247, 250)); // Nền xám nhạt hiện đại

        // --- THANH CÔNG CỤ (NORTH) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        // Tiêu đề lớn bên trái
        JLabel lblTitle = new JLabel("QUẢN LÝ GIAO VIỆC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        // Các nút chức năng và chọn ngày (Bên phải)
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnlButtons.setOpaque(false);

        JLabel lblPick = new JLabel("Ngày:");
        lblPick.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPick.setForeground(new Color(100, 116, 139));

        dcSchedule = new JDateChooser(new java.util.Date());
        dcSchedule.setPreferredSize(new Dimension(140, 38));
        dcSchedule.setDateFormatString("dd/MM/yyyy");
        dcSchedule.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnRefresh = createStyledButton("🔄 Làm mới", "#64748B", "#FFFFFF");
        btnRefresh.addActionListener(e -> loadTaskData());

        JButton btnAddTask = createStyledButton("+ Giao việc mới", "#10B981", "#FFFFFF");
        btnAddTask.addActionListener(e -> new GiaoViecMoiDialog(this).setVisible(true));

        JButton btnMarkDone = createStyledButton("✓ Hoàn thành", "#3B82F6", "#FFFFFF");
        btnMarkDone.addActionListener(e -> handleUpdateStatus("Hoàn thành"));

        JButton btnEditTask = createStyledButton("✎ Sửa", "#F59E0B", "#FFFFFF");
        btnEditTask.addActionListener(e -> openEditDialog());

        JButton btnDeleteTask = createStyledButton("🗑 Xóa", "#EF4444", "#FFFFFF");
        btnDeleteTask.addActionListener(e -> handleDeleteTask());

        pnlButtons.add(lblPick);
        pnlButtons.add(dcSchedule);
        pnlButtons.add(btnRefresh);
        pnlButtons.add(btnAddTask);
        pnlButtons.add(btnMarkDone);
        pnlButtons.add(btnEditTask);
        pnlButtons.add(btnDeleteTask);

        pnlHeader.add(pnlButtons, BorderLayout.EAST);

        // --- BẢNG HIỂN THỊ CÔNG VIỆC (CENTER) ---
        String[] columns = {"ID", "Nhân viên", "Công việc", "Ngày thực hiện", "Ca", "Trạng thái"};
        taskModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tblTasks = new JTable(taskModel);

        // Nâng cấp giao diện Bảng (JTable)
        tblTasks.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblTasks.setRowHeight(45); // Tăng chiều cao hàng

        tblTasks.putClientProperty(FlatClientProperties.STYLE, ""
                + "showHorizontalLines: true;"
                + "showVerticalLines: false;"
                + "intercellSpacing: 0, 0;"
                + "selectionBackground: #eff6ff;"
                + "selectionForeground: #1e3a8a;");
        tblTasks.putClientProperty("JTable.alternateRowColor", new Color(248, 250, 252)); // Màu xen kẽ
        tblTasks.setGridColor(new Color(226, 232, 240));
        tblTasks.setFocusable(false);
        tblTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Nâng cấp Header bảng
        tblTasks.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblTasks.getTableHeader().setBackground(new Color(241, 245, 249));
        tblTasks.getTableHeader().setForeground(new Color(71, 85, 105));
        tblTasks.getTableHeader().setPreferredSize(new Dimension(0, 50));
        tblTasks.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(203, 213, 225)));

        // Căn giữa cho các cột cần thiết
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tblTasks.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        tblTasks.getColumnModel().getColumn(0).setMaxWidth(60);
        tblTasks.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Ngày thực hiện
        tblTasks.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Ca
        tblTasks.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Trạng thái
        tblTasks.getColumnModel().getColumn(4).setMaxWidth(100);

        // Bọc JTable trong ScrollPane và cho vào Card trắng bo góc
        JScrollPane scrollPane = new JScrollPane(tblTasks);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel pnlCard = new JPanel(new BorderLayout());
        pnlCard.setBackground(Color.WHITE);
        pnlCard.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 20;" // Bo góc thẻ 20px
                + "border: 1,1,1,1, #e2e8f0,, 20;");
        pnlCard.add(scrollPane, BorderLayout.CENTER);

        // Bọc Card vào lớp đệm (Padding)
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(10, 0, 10, 0));
        cardWrapper.add(pnlCard, BorderLayout.CENTER);

        JPanel pnlScrollContent = new JPanel(new BorderLayout(20, 20));
        pnlScrollContent.setOpaque(false);
        pnlScrollContent.add(pnlHeader, BorderLayout.NORTH);
        pnlScrollContent.add(cardWrapper, BorderLayout.CENTER);

        JScrollPane mainScroll = new JScrollPane(pnlScrollContent);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getViewport().setBackground(new Color(245, 247, 250));
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(mainScroll, BorderLayout.CENTER);
    }

    // Hàm tạo nút bấm phong cách FlatLaf bo tròn hiện đại
    private JButton createStyledButton(String text, String bgHex, String fgHex) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 38));

        btn.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: " + bgHex + ";"
                + "foreground: " + fgHex + ";"
                + "arc: 999;" // Bo tròn tối đa dạng viên thuốc
                + "borderWidth: 0;"
                + "focusWidth: 0;"
                + "margin: 4, 12, 4, 12;"
                + "hoverBackground: lighten(" + bgHex + ", 10%);"
                + "pressedBackground: darken(" + bgHex + ", 10%);");
        return btn;
    }

    // Nạp dữ liệu qua API thay vì JDBC (Giữ nguyên logic)
    public void loadTaskData() {
        if (dcSchedule.getDate() == null) return;
        taskModel.setRowCount(0);

        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(dcSchedule.getDate());

        // Gọi API lấy danh sách công việc
        List<GiaoViecDTO> tasks = userClient.getTasksByDate(dateStr);

        if (tasks != null) {
            for (GiaoViecDTO t : tasks) {
                taskModel.addRow(new Object[]{
                        t.getId(),
                        t.getHoTenNhanVien(),
                        t.getTenCongViec(),
                        t.getNgayThucHien(),
                        t.getCaLamViec(),
                        t.getTrangThai()
                });
            }
        }
    }

    private void handleUpdateStatus(String status) {
        int row = tblTasks.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một công việc!");
            return;
        }
        int id = (int) tblTasks.getValueAt(row, 0);
        if (userClient.updateTaskStatus(id, status)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadTaskData();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái!");
        }
    }

    private void openEditDialog() {
        int row = tblTasks.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn công việc cần sửa!");
            return;
        }
        int id = (int) tblTasks.getValueAt(row, 0);
        String tenViec = tblTasks.getValueAt(row, 2).toString();
        String trangThai = tblTasks.getValueAt(row, 5).toString();
        new SuaGiaoViecDialog(this, id, tenViec, "", trangThai).setVisible(true);
    }

    private void handleDeleteTask() {
        int row = tblTasks.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn công việc cần xóa!");
            return;
        }
        int id = (int) tblTasks.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userClient.deleteTask(id)) {
                loadTaskData();
                JOptionPane.showMessageDialog(this, "Đã xóa!");
            }
        }
    }
}