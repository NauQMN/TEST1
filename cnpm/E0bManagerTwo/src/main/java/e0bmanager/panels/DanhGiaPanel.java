package e0bmanager.panels;

import e0bmanager.client.UserClient;
import e0bmanager.dto.DanhGiaDTO;
import e0bmanager.ui.BottomTaskbar;
import e0bmanager.ui.FormDanhGiaDialog;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class DanhGiaPanel extends JPanel {
    private JComboBox<Integer> cboThang, cboNam;
    private DefaultTableModel model;
    private JTable table;
    private UserClient userClient = new UserClient();
    private ActionListener navigationListener;

    public DanhGiaPanel(JFrame parentFrame, ActionListener navigationListener) {
        this.navigationListener = navigationListener;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 30, 20, 30)); // Căn lề rộng rãi
        setBackground(new Color(245, 247, 250)); // Nền xám nhạt hiện đại

        // --- TOP PANEL (Header + Điều khiển) ---
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        // 1. Dòng Tiêu đề & Chọn ngày tháng
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setOpaque(false);

        JLabel lblTitle = new JLabel("ĐÁNH GIÁ NHÂN VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));
        pnlToolbar.add(lblTitle, BorderLayout.WEST);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilter.setOpaque(false);

        cboThang = new JComboBox<>();
        cboNam = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cboThang.addItem(i);
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 1; i <= currentYear + 1; i++) cboNam.addItem(i);

        cboThang.setSelectedItem(LocalDate.now().getMonthValue());
        cboNam.setSelectedItem(currentYear);

        cboThang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboThang.setPreferredSize(new Dimension(80, 35));
        cboNam.setPreferredSize(new Dimension(100, 35));

        JLabel lblThang = new JLabel("Tháng:");
        lblThang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblThang.setForeground(new Color(100, 116, 139));

        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNam.setForeground(new Color(100, 116, 139));

        JButton btnTai = createStyledButton("🔄 Tải danh sách", "#10B981", "#FFFFFF");
        btnTai.addActionListener(e -> loadData());

        pnlFilter.add(lblThang);
        pnlFilter.add(cboThang);
        pnlFilter.add(lblNam);
        pnlFilter.add(cboNam);
        pnlFilter.add(Box.createHorizontalStrut(5)); // Khoảng đệm
        pnlFilter.add(btnTai);

        pnlToolbar.add(pnlFilter, BorderLayout.EAST);
        pnlTop.add(pnlToolbar, BorderLayout.NORTH);

        // 2. Dòng Ghi chú quy đổi điểm (Màu sắc hiện đại)
        JLabel lblNote = new JLabel("<html><b>Quy đổi hệ số:</b> &nbsp;&nbsp; "
                + "&ge; 9: <font color='#10B981'>+10%</font> &nbsp;|&nbsp; "
                + "&ge; 8: <font color='#10B981'>+5%</font> &nbsp;|&nbsp; "
                + "&ge; 7: <font color='#64748B'>0%</font> &nbsp;|&nbsp; "
                + "&ge; 6: <font color='#EF4444'>-5%</font> &nbsp;|&nbsp; "
                + "&lt; 6: <font color='#EF4444'>-10%</font></html>");
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNote.setForeground(new Color(71, 85, 105));

        // Bọc note vào panel để có background trắng bo góc nhẹ
        JPanel pnlNote = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlNote.setBackground(Color.WHITE);
        pnlNote.putClientProperty(FlatClientProperties.STYLE, "arc: 15;");
        pnlNote.add(lblNote);
        pnlTop.add(pnlNote, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);

        // --- BẢNG DỮ LIỆU (CENTER) ---
        String[] columns = {"ID", "Họ tên", "Chức vụ", "Điểm tháng", "Hệ số lương"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);

        // Cải tiến giao diện JTable
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.putClientProperty(FlatClientProperties.STYLE, ""
                + "showHorizontalLines: true;"
                + "showVerticalLines: false;"
                + "intercellSpacing: 0, 0;"
                + "selectionBackground: #eff6ff;"
                + "selectionForeground: #1e3a8a;");
        table.putClientProperty("JTable.alternateRowColor", new Color(248, 250, 252));
        table.setGridColor(new Color(226, 232, 240));
        table.setFocusable(false);

        // Nâng cấp Header bảng
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(71, 85, 105));
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(203, 213, 225)));

        // Căn lề các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Điểm
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Hệ số

        // Đóng gói vào ScrollPane và Panel bo góc (Card Style)
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel pnlCard = new JPanel(new BorderLayout());
        pnlCard.setBackground(Color.WHITE);
        pnlCard.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 20;"
                + "border: 1,1,1,1, #e2e8f0,, 20;");
        pnlCard.add(scrollPane, BorderLayout.CENTER);

        // Khoảng đệm cho Card
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(10, 0, 10, 0));
        cardWrapper.add(pnlCard, BorderLayout.CENTER);

        // --- BOTTOM (Nút Sửa) ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        pnlBottom.setOpaque(false);

        JButton btnSua = createStyledButton("⭐ Cập nhật đánh giá", "#3B82F6", "#FFFFFF");
        btnSua.setPreferredSize(new Dimension(190, 42));
        btnSua.addActionListener(e -> openEvaluateDialog());
        pnlBottom.add(btnSua);

        // Gộp toàn bộ nội dung vào panel cuộn được
        JPanel pnlScrollContent = new JPanel(new BorderLayout(20, 20));
        pnlScrollContent.setOpaque(false);
        pnlScrollContent.add(pnlTop, BorderLayout.NORTH);
        pnlScrollContent.add(cardWrapper, BorderLayout.CENTER);
        pnlScrollContent.add(pnlBottom, BorderLayout.SOUTH);

        JScrollPane mainScroll = new JScrollPane(pnlScrollContent);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getViewport().setBackground(new Color(245, 247, 250));
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(mainScroll, BorderLayout.CENTER);
    }

    // Hàm tạo nút bấm bo tròn FlatLaf
    private JButton createStyledButton(String text, String bgHex, String fgHex) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 38));

        btn.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: " + bgHex + ";"
                + "foreground: " + fgHex + ";"
                + "arc: 999;" // Dạng viên thuốc
                + "borderWidth: 0;"
                + "focusWidth: 0;"
                + "margin: 4, 12, 4, 12;"
                + "hoverBackground: lighten(" + bgHex + ", 10%);"
                + "pressedBackground: darken(" + bgHex + ", 10%);");
        return btn;
    }

    // --- CÁC HÀM XỬ LÝ CHỨC NĂNG (Giữ nguyên 100%) ---

    public void loadData() {
        model.setRowCount(0);
        int thang = (int) cboThang.getSelectedItem();
        int nam = (int) cboNam.getSelectedItem();

        List<DanhGiaDTO> list = userClient.getDanhGiaList(thang, nam);
        if (list != null) {
            for (DanhGiaDTO d : list) {
                model.addRow(new Object[]{
                        d.getNhanVienId(),
                        d.getHoTen(),
                        d.getChucVu(),
                        d.getDiemTong() == null ? "---" : d.getDiemTong(),
                        d.getHeSoLuong() == null ? "---" : d.getHeSoLuong()
                });
            }
        }
    }

    private void openEvaluateDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn nhân viên!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String ten = (String) model.getValueAt(row, 1);
        String cv = (String) model.getValueAt(row, 2);
        int thang = (int) cboThang.getSelectedItem();
        int nam = (int) cboNam.getSelectedItem();

        // Dialog này cũng nên được sửa để dùng API gửi dữ liệu lên Server
        new FormDanhGiaDialog(null, id, ten, cv, thang, nam).setVisible(true);
        loadData();
    }
}