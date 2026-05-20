package e0bmanager.panels;

import e0bmanager.client.UserClient;
import e0bmanager.dto.LuongDTO;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public class TinhLuongPanel extends JPanel {
    private JTextField txtID_CT, txtTen_CT, txtLuongGio_CT;
    private JTextField txtSoCa_CT, txtSoGio_CT;
    private JTextField txtPhuCap_CT, txtThuong_CT, txtTongLuong_CT;
    private JComboBox<String> cbThangLoc;
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;
    private JLabel lblTongCongTatCa;
    private double tileHeSo = 0.0;
    private UserClient userClient = new UserClient();
    private ActionListener navigationListener;
    private DecimalFormat df = new DecimalFormat("#,###");

    public TinhLuongPanel(JFrame parentFrame, ActionListener navigationListener) {
        this.navigationListener = navigationListener;
        initComponents();
        loadLuongHistory(); // Load dữ liệu ngay khi mở
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250)); // Nền xám nhạt hiện đại

        // --- TIÊU ĐỀ CHÍNH (Cố định ở trên cùng) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(25, 30, 10, 30));

        JLabel lblMainTitle = new JLabel("QUẢN LÝ LƯƠNG");
        lblMainTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblMainTitle.setForeground(new Color(30, 41, 59));
        pnlHeader.add(lblMainTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- KHU VỰC TRUNG TÂM (Chứa Form nhập và Bảng - Sẽ được thêm Scroll) ---
        JPanel pnlCenterWrapper = new JPanel();
        pnlCenterWrapper.setLayout(new BoxLayout(pnlCenterWrapper, BoxLayout.Y_AXIS));
        pnlCenterWrapper.setOpaque(false);
        pnlCenterWrapper.setBorder(new EmptyBorder(10, 30, 30, 30));

        // ==========================================
        // PHẦN 1: FORM NHẬP LIỆU
        // ==========================================
        JPanel pnlInputCard = new JPanel(new BorderLayout(10, 15));
        pnlInputCard.setBackground(Color.WHITE);
        pnlInputCard.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 20;"
                + "border: 1,1,1,1, #E2E8F0,, 20;");
        pnlInputCard.setBorder(new EmptyBorder(20, 25, 20, 25));
        pnlInputCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        JLabel lblFormTitle = new JLabel("⚙️ Tính toán & Chốt lương tháng này");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(71, 85, 105));
        pnlInputCard.add(lblFormTitle, BorderLayout.NORTH);

        // Grid 5 hàng × 4 cột
        JPanel pnlInputGrid = new JPanel(new GridLayout(5, 4, 20, 12));
        pnlInputGrid.setOpaque(false);

        txtID_CT = createStyledTextField(true);
        txtID_CT.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập ID rồi Enter...");

        txtTen_CT       = createStyledTextField(false);
        txtLuongGio_CT  = createStyledTextField(false);
        txtSoCa_CT      = createStyledTextField(false);  txtSoCa_CT.setText("0");
        txtSoGio_CT     = createStyledTextField(false);  txtSoGio_CT.setText("0");
        txtThuong_CT    = createStyledTextField(false);  txtThuong_CT.setText("0%");
        txtPhuCap_CT    = createStyledTextField(true);   txtPhuCap_CT.setText("0");

        txtTongLuong_CT = new JTextField();
        txtTongLuong_CT.setEditable(false);
        txtTongLuong_CT.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtTongLuong_CT.setForeground(new Color(239, 68, 68));
        txtTongLuong_CT.setBackground(new Color(254, 242, 242));
        txtTongLuong_CT.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; margin: 4,10,4,10; borderWidth: 0; focusWidth: 0;");

        // Hàng 1: ID | Tên
        pnlInputGrid.add(createLabel("ID Nhân viên:"));     pnlInputGrid.add(txtID_CT);
        pnlInputGrid.add(createLabel("Tên nhân viên:"));    pnlInputGrid.add(txtTen_CT);
        // Hàng 2: Lương/giờ | Tổng ca
        pnlInputGrid.add(createLabel("Lương/giờ (VNĐ):"));  pnlInputGrid.add(txtLuongGio_CT);
        pnlInputGrid.add(createLabel("Tổng số ca:"));       pnlInputGrid.add(txtSoCa_CT);
        // Hàng 3: Tổng giờ | Hệ số
        pnlInputGrid.add(createLabel("Tổng giờ thực tế:")); pnlInputGrid.add(txtSoGio_CT);
        pnlInputGrid.add(createLabel("Hệ số đánh giá:"));   pnlInputGrid.add(txtThuong_CT);
        // Hàng 4: Phụ cấp | Tổng lĩnh | Nút chốt
        pnlInputGrid.add(createLabel("Phụ cấp (VNĐ):"));   pnlInputGrid.add(txtPhuCap_CT);
        pnlInputGrid.add(createLabel("TỔNG LĨNH:"));        pnlInputGrid.add(txtTongLuong_CT);

        JButton btnLuu = new JButton("✓ CHỐT LƯƠNG");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuu.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #10B981; foreground: #FFFFFF;"
                + "arc: 999; borderWidth: 0; focusWidth: 0; margin: 5, 20, 5, 20;"
                + "hoverBackground: lighten(#10B981, 10%);"
                + "pressedBackground: darken(#10B981, 10%);");
        btnLuu.addActionListener(e -> luuBangLuong());

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btnLuu);
        pnlInputGrid.add(pnlBtn);
        pnlInputGrid.add(new JLabel()); // ô trống căn lưới
        pnlInputCard.add(pnlInputGrid, BorderLayout.CENTER);

        // Thêm Card 1 vào Wrapper
        pnlCenterWrapper.add(pnlInputCard);
        pnlCenterWrapper.add(Box.createVerticalStrut(25)); // Khoảng cách giữa 2 phần

        // ==========================================
        // PHẦN 2: BẢNG LỊCH SỬ (Nằm trong Card trắng bo góc)
        // ==========================================
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 20;"
                + "border: 1,1,1,1, #E2E8F0,, 20;");
        pnlTableCard.setPreferredSize(new Dimension(0, 400)); // Cố định chiều cao tối thiểu cho bảng
        pnlTableCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 800));

        // Header của Bảng
        JPanel pnlTableTitle = new JPanel(new BorderLayout());
        pnlTableTitle.setOpaque(false);
        pnlTableTitle.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("📋 CHI TIẾT BẢNG LƯƠNG");
        lblTitle.setForeground(new Color(30, 41, 59));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel pnlMonthPicker = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlMonthPicker.setOpaque(false);

        JLabel lblMonth = new JLabel("Xem theo tháng: ");
        lblMonth.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMonth.setForeground(new Color(100, 116, 139));
        pnlMonthPicker.add(lblMonth);

        cbThangLoc = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"});
        cbThangLoc.setSelectedItem(String.valueOf(LocalDate.now().getMonthValue()));
        cbThangLoc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbThangLoc.setPreferredSize(new Dimension(80, 32));
        cbThangLoc.addActionListener(e -> loadLuongHistory());
        pnlMonthPicker.add(cbThangLoc);

        pnlTableTitle.add(lblTitle, BorderLayout.WEST);
        pnlTableTitle.add(pnlMonthPicker, BorderLayout.EAST);
        pnlTableCard.add(pnlTableTitle, BorderLayout.NORTH);

        // Khởi tạo JTable và Áp dụng giao diện FlatLaf
        String[] columns = {"ID", "Họ Tên", "Tháng", "Lương/Giờ", "Tổng Ca", "Tổng Giờ", "Phụ Cấp", "Hệ Số", "Tổng Lương"};
        modelChiTiet = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChiTiet = new JTable(modelChiTiet);

        tblChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblChiTiet.setRowHeight(40);
        tblChiTiet.putClientProperty(FlatClientProperties.STYLE, ""
                + "showHorizontalLines: true; showVerticalLines: false; intercellSpacing: 0, 0;"
                + "selectionBackground: #eff6ff; selectionForeground: #1e3a8a;");
        tblChiTiet.putClientProperty("JTable.alternateRowColor", new Color(248, 250, 252));
        tblChiTiet.setGridColor(new Color(226, 232, 240));
        tblChiTiet.setFocusable(false);

        // Header bảng
        tblChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblChiTiet.getTableHeader().setBackground(new Color(241, 245, 249));
        tblChiTiet.getTableHeader().setForeground(new Color(71, 85, 105));
        tblChiTiet.getTableHeader().setPreferredSize(new Dimension(0, 45));
        tblChiTiet.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(203, 213, 225)));

        // Căn lề các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); tblChiTiet.getColumnModel().getColumn(0).setMaxWidth(50);
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); tblChiTiet.getColumnModel().getColumn(2).setMaxWidth(55);
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); tblChiTiet.getColumnModel().getColumn(4).setMaxWidth(70);
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); tblChiTiet.getColumnModel().getColumn(5).setMaxWidth(75);
        tblChiTiet.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); tblChiTiet.getColumnModel().getColumn(7).setMaxWidth(70);

        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);  // Lương/Giờ
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);  // Phụ cấp
        tblChiTiet.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);  // Tổng lương

        JScrollPane innerScrollPane = new JScrollPane(tblChiTiet);
        innerScrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        innerScrollPane.getViewport().setBackground(Color.WHITE);
        pnlTableCard.add(innerScrollPane, BorderLayout.CENTER);

        // Nhãn Tổng quỹ lương (Phía dưới bảng)
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setOpaque(false);
        pnlFooter.setBorder(new EmptyBorder(10, 20, 10, 20));

        lblTongCongTatCa = new JLabel("TỔNG CHI LƯƠNG TOÀN CÔNG TY: 0 VNĐ");
        lblTongCongTatCa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongCongTatCa.setForeground(new Color(239, 68, 68)); // Đỏ nổi bật
        pnlFooter.add(lblTongCongTatCa);

        pnlTableCard.add(pnlFooter, BorderLayout.SOUTH);

        // Thêm Card 2 vào Wrapper
        pnlCenterWrapper.add(pnlTableCard);

        // ==========================================
        // ĐÃ THÊM: BỌC TOÀN BỘ VÀO SCROLL PANE
        // ==========================================
        JScrollPane mainScroll = new JScrollPane(pnlCenterWrapper);
        mainScroll.setBorder(BorderFactory.createEmptyBorder()); // Bỏ viền của ScrollPane
        mainScroll.getViewport().setBackground(new Color(245, 247, 250)); // Nền đồng bộ
        mainScroll.getVerticalScrollBar().setUnitIncrement(16); // Tăng tốc độ cuộn chuột cho mượt
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Tắt cuộn ngang

        add(mainScroll, BorderLayout.CENTER);

        // --- TASKBAR ---

        // Sự kiện tự động tính toán qua API
        txtID_CT.addActionListener(e -> tuDongLayThongTin());
        txtPhuCap_CT.addActionListener(e -> capNhatTongLuongRealtime());
    }

    // --- HÀM TIỆN ÍCH TẠO GIAO DIỆN ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 116, 139));
        return lbl;
    }

    private JTextField createStyledTextField(boolean isEditable) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setEditable(isEditable);
        if(!isEditable) {
            txt.setBackground(new Color(248, 250, 252)); // Nền xám nhạt báo hiệu không sửa được
            txt.setForeground(new Color(100, 116, 139));
        }
        txt.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 10;" // Bo góc
                + "margin: 4, 10, 4, 10;"); // Thêm padding (khoảng trống bên trong)
        return txt;
    }

    // --- CÁC HÀM XỬ LÝ CHỨC NĂNG (Giữ nguyên 100%) ---

    private void tuDongLayThongTin() {
        String idStr = txtID_CT.getText().trim();
        if (idStr.isEmpty()) return;
        try {
            int nvId = Integer.parseInt(idStr);
            int thang = Integer.parseInt(cbThangLoc.getSelectedItem().toString());
            int nam = LocalDate.now().getYear();

            LuongDTO res = userClient.calculateLuong(nvId, thang, nam);
            if (res != null) {
                txtTen_CT.setText(res.getTenNv());
                txtLuongGio_CT.setText(df.format(res.getLuongCoBan()));
                txtSoCa_CT.setText(String.valueOf(res.getSoCa() != null ? res.getSoCa() : 0));
                txtSoGio_CT.setText(res.getSoGioThucTe() != null
                        ? String.format("%.1f", res.getSoGioThucTe()) : "0");
                txtThuong_CT.setText(res.getHeSo());

                // Parse hệ số
                tileHeSo = 0.0;
                String hs = res.getHeSo();
                if (hs != null) {
                    if      (hs.contains("+10")) tileHeSo =  0.10;
                    else if (hs.contains("+5"))  tileHeSo =  0.05;
                    else if (hs.contains("-5"))  tileHeSo = -0.05;
                    else if (hs.contains("-10")) tileHeSo = -0.10;
                }
                capNhatTongLuongRealtime();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu cho nhân viên ID: " + idStr);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID phải là số!");
        }
    }

    private void capNhatTongLuongRealtime() {
        try {
            double luongGio = Double.parseDouble(txtLuongGio_CT.getText().replace(",", "").isEmpty()
                    ? "0" : txtLuongGio_CT.getText().replace(",", ""));
            double soGio = Double.parseDouble(txtSoGio_CT.getText().isEmpty() ? "0" : txtSoGio_CT.getText());
            double phuCap = Double.parseDouble(txtPhuCap_CT.getText().replace(",", "").isEmpty()
                    ? "0" : txtPhuCap_CT.getText().replace(",", ""));
            // Công thức: luongGio × soGioThucTe × (1 + heSo) + phuCap
            double tongLinh = luongGio * soGio * (1 + tileHeSo) + phuCap;
            txtTongLuong_CT.setText(String.format("%,.0f VNĐ", tongLinh));
        } catch (Exception e) {
            txtTongLuong_CT.setText("0 VNĐ");
        }
    }

    private void luuBangLuong() {
        if (txtID_CT.getText().isEmpty() || txtTen_CT.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID và nhấn Enter để lấy thông tin!");
            return;
        }
        try {
            LuongDTO dto = new LuongDTO();
            dto.setIdNv(Integer.parseInt(txtID_CT.getText().trim()));
            dto.setTenNv(txtTen_CT.getText());
            dto.setThang(Integer.parseInt(cbThangLoc.getSelectedItem().toString()));
            dto.setNam(LocalDate.now().getYear());
            dto.setLuongCoBan(Double.parseDouble(txtLuongGio_CT.getText().replace(",", "")));
            dto.setSoCa(Integer.parseInt(txtSoCa_CT.getText().isEmpty() ? "0" : txtSoCa_CT.getText()));
            dto.setSoGioThucTe(Double.parseDouble(txtSoGio_CT.getText().isEmpty() ? "0" : txtSoGio_CT.getText()));
            dto.setPhuCap(Double.parseDouble(txtPhuCap_CT.getText().replace(",", "").isEmpty() ? "0" : txtPhuCap_CT.getText().replace(",", "")));
            dto.setHeSo(txtThuong_CT.getText());
            dto.setTongLuong(Double.parseDouble(txtTongLuong_CT.getText().replaceAll("[^\\d]", "")));

            if (userClient.chotLuong(dto)) {
                JOptionPane.showMessageDialog(this, "✅ Đã chốt lương thành công!");
                loadLuongHistory();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Lỗi khi chốt lương!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLuongHistory() {
        if (modelChiTiet == null) return;
        modelChiTiet.setRowCount(0);
        double tongQuy = 0;

        int thang = Integer.parseInt(cbThangLoc.getSelectedItem().toString());
        int nam = LocalDate.now().getYear();

        List<LuongDTO> list = userClient.getLuongHistory(thang, nam);
        if (list != null) {
            for (LuongDTO l : list) {
                tongQuy += (l.getTongLuong() != null ? l.getTongLuong() : 0);
                modelChiTiet.addRow(new Object[]{
                        l.getIdNv(),
                        l.getTenNv(),
                        l.getThang(),
                        df.format(l.getLuongCoBan()),
                        l.getSoCa() != null ? l.getSoCa() : 0,
                        l.getSoGioThucTe() != null ? String.format("%.1f", l.getSoGioThucTe()) : "0",
                        df.format(l.getPhuCap() != null ? l.getPhuCap() : 0),
                        l.getHeSo(),
                        df.format(l.getTongLuong() != null ? l.getTongLuong() : 0)
                });
            }
        }
        lblTongCongTatCa.setText("TỔNG CHI LƯƠNG TOÀN CÔNG TY: " + df.format(tongQuy) + " VNĐ");
    }
}