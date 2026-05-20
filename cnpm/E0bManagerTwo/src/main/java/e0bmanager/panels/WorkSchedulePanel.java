package e0bmanager.panels;

import com.toedter.calendar.JDateChooser;
import e0bmanager.client.UserClient;
import e0bmanager.dto.LichLamViecDTO;
import e0bmanager.ui.PhanCaDialog;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WorkSchedulePanel extends JPanel {
    private JFrame parentFrame;
    private ActionListener navigationListener;
    private UserClient userClient = new UserClient();

    // Tab 1 — Quản lý lịch
    private JDateChooser dcSchedule;
    private JPanel pnlShiftContainer;

    // Tab 2 — Duyệt lịch
    private JPanel pnlPendingContainer;
    private JLabel lblPendingCount;

    public WorkSchedulePanel(JFrame parentFrame, ActionListener navigationListener) {
        this.parentFrame = parentFrame;
        this.navigationListener = navigationListener;
        initComponents();
        loadScheduleFromDB();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ── Header chung ──────────────────────────────────────────────────
        JLabel lblTitle = new JLabel("LỊCH LÀM VIỆC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));
        lblTitle.setBorder(new EmptyBorder(0, 0, 18, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ── JTabbedPane ───────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.putClientProperty(FlatClientProperties.STYLE,
                "tabHeight: 42; tabArc: 12;");

        tabs.addTab("📋  Quản lý phân ca", buildTab1());
        tabs.addTab("✅  Duyệt lịch nhân viên.", buildTab2());

        // Khi chuyển sang tab duyệt → tải lại danh sách
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) loadPendingSchedules();
        });

        add(tabs, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  TAB 1 — Quản lý phân ca
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildTab1() {
        JPanel tab = new JPanel(new BorderLayout(0, 15));
        tab.setOpaque(false);
        tab.setBorder(new EmptyBorder(15, 5, 5, 5));

        // Toolbar
        JPanel pnlControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlControl.setOpaque(false);

        JLabel lblPick = new JLabel("Ngày xem:");
        lblPick.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPick.setForeground(new Color(100, 116, 139));

        dcSchedule = new JDateChooser(new Date());
        dcSchedule.setPreferredSize(new Dimension(160, 38));
        dcSchedule.setDateFormatString("dd/MM/yyyy");
        dcSchedule.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnXem   = createBtn("🔍 Xem lịch",  "#3B82F6");
        JButton btnThem  = createBtn("＋ Phân ca mới", "#10B981");
        btnXem.addActionListener(e -> loadScheduleFromDB());
        btnThem.addActionListener(e -> { new PhanCaDialog(this).setVisible(true); loadScheduleFromDB(); });

        pnlControl.add(lblPick); pnlControl.add(dcSchedule);
        pnlControl.add(btnXem); pnlControl.add(btnThem);
        tab.add(pnlControl, BorderLayout.NORTH);

        // Vùng ca làm
        pnlShiftContainer = new JPanel();
        pnlShiftContainer.setLayout(new BoxLayout(pnlShiftContainer, BoxLayout.Y_AXIS));
        pnlShiftContainer.setBackground(new Color(245, 247, 250));

        JScrollPane scroll = new JScrollPane(pnlShiftContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(245, 247, 250));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tab.add(scroll, BorderLayout.CENTER);
        return tab;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  TAB 2 — Duyệt lịch nhân viên đăng ký
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildTab2() {
        JPanel tab = new JPanel(new BorderLayout(0, 15));
        tab.setOpaque(false);
        tab.setBorder(new EmptyBorder(15, 5, 5, 5));

        // Toolbar
        JPanel pnlTopBar = new JPanel(new BorderLayout());
        pnlTopBar.setOpaque(false);

        lblPendingCount = new JLabel("Đang tải...");
        lblPendingCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPendingCount.setForeground(new Color(100, 116, 139));

        JButton btnReload = createBtn("🔄 Tải lại", "#64748B");
        btnReload.addActionListener(e -> loadPendingSchedules());

        pnlTopBar.add(lblPendingCount, BorderLayout.WEST);
        pnlTopBar.add(btnReload, BorderLayout.EAST);
        tab.add(pnlTopBar, BorderLayout.NORTH);

        pnlPendingContainer = new JPanel();
        pnlPendingContainer.setLayout(new BoxLayout(pnlPendingContainer, BoxLayout.Y_AXIS));
        pnlPendingContainer.setBackground(new Color(245, 247, 250));

        JScrollPane scroll = new JScrollPane(pnlPendingContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(245, 247, 250));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tab.add(scroll, BorderLayout.CENTER);
        return tab;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  LOAD DỮ LIỆU — Tab 1
    // ══════════════════════════════════════════════════════════════════════
    public void loadScheduleFromDB() {
        pnlShiftContainer.removeAll();
        Date utilDate = dcSchedule.getDate();
        if (utilDate == null) return;

        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(utilDate);
        List<LichLamViecDTO> list = userClient.getScheduleByDate(dateStr);

        // Phân loại vào 3 nhóm theo giờ vào
        java.util.List<LichLamViecDTO> nhomSang  = new java.util.ArrayList<>();
        java.util.List<LichLamViecDTO> nhomChieu = new java.util.ArrayList<>();
        java.util.List<LichLamViecDTO> nhomToi   = new java.util.ArrayList<>();

        if (list != null) {
            for (LichLamViecDTO dto : list) {
                int gio = parseGio(dto.getGioVao(), dto.getCaLam());
                if      (gio < 12) nhomSang.add(dto);
                else if (gio < 17) nhomChieu.add(dto);
                else               nhomToi.add(dto);
            }
        }

        // Luôn hiển thị đủ 3 section dù có hay không có nhân viên
        pnlShiftContainer.add(buildSectionCard(
                "🌅  Ca Sáng",  "Khung giờ trước 12:00",  "#F59E0B", nhomSang));
        pnlShiftContainer.add(Box.createVerticalStrut(16));
        pnlShiftContainer.add(buildSectionCard(
                "☀️  Ca Chiều", "Khung giờ 12:00 – 17:00", "#3B82F6", nhomChieu));
        pnlShiftContainer.add(Box.createVerticalStrut(16));
        pnlShiftContainer.add(buildSectionCard(
                "🌙  Ca Tối",   "Khung giờ từ 17:00",      "#8B5CF6", nhomToi));
        pnlShiftContainer.add(Box.createVerticalStrut(20));

        pnlShiftContainer.revalidate();
        pnlShiftContainer.repaint();
    }

    /**
     * Tạo một section card cho một ca (luôn hiển thị dù danh sách rỗng).
     */
    private JPanel buildSectionCard(String tenCa, String moTaGio, String hexColor,
                                    java.util.List<LichLamViecDTO> danhSach) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc: 20; border: 1,1,1,1, #E2E8F0,, 20;");
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Header section ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 20, 10, 20));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlLeft.setOpaque(false);

        JLabel lblTen = new JLabel(tenCa + "  ");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTen.setForeground(Color.decode(hexColor));

        JLabel lblMoTa = new JLabel("· " + moTaGio);
        lblMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMoTa.setForeground(new Color(148, 163, 184));

        pnlLeft.add(lblTen);
        pnlLeft.add(lblMoTa);

        // Badge số lượng
        JLabel lblBadge = new JLabel(danhSach.size() + " người");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setForeground(Color.decode(hexColor));
        lblBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(hexColor), 1, true),
                new EmptyBorder(2, 10, 2, 10)));

        header.add(pnlLeft,   BorderLayout.WEST);
        header.add(lblBadge,  BorderLayout.EAST);
        card.add(header);

        // ── Đường kẻ ngang ──────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(241, 245, 249));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);

        // ── Nội dung ────────────────────────────────────────────────────
        if (danhSach.isEmpty()) {
            JLabel lblEmpty = new JLabel("  Chưa có nhân viên nào trong ca này");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblEmpty.setForeground(new Color(203, 213, 225));
            lblEmpty.setBorder(new EmptyBorder(14, 24, 16, 20));
            card.add(lblEmpty);
        } else {
            for (LichLamViecDTO dto : danhSach) {
                card.add(createShiftCard(dto));
            }
        }

        return card;
    }

    /** Parse giờ từ gioVao ("HH:mm") hoặc fallback từ tên ca */
    private int parseGio(String gioVao, String tenCa) {
        if (gioVao != null && gioVao.contains(":")) {
            try { return Integer.parseInt(gioVao.split(":")[0]); } catch (Exception ignored) {}
        }
        // Fallback theo tên ca nếu chưa có gioVao (dữ liệu cũ)
        if (tenCa != null) {
            String lower = tenCa.toLowerCase();
            if (lower.contains("sáng")) return 8;
            if (lower.contains("chiều")) return 13;
            if (lower.contains("tối")) return 18;
        }
        return 0;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  LOAD DỮ LIỆU — Tab 2
    // ══════════════════════════════════════════════════════════════════════
    private void loadPendingSchedules() {
        pnlPendingContainer.removeAll();
        List<LichLamViecDTO> list = userClient.getPendingSchedules();

        if (list == null || list.isEmpty()) {
            lblPendingCount.setText("Không có lịch nào đang chờ duyệt.");
            pnlPendingContainer.add(createEmptyState("Tất cả lịch đã được xử lý."));
        } else {
            lblPendingCount.setText("Đang chờ duyệt: " + list.size() + " lịch");
            for (LichLamViecDTO dto : list) {
                pnlPendingContainer.add(createPendingCard(dto));
                pnlPendingContainer.add(Box.createVerticalStrut(12));
            }
        }
        pnlPendingContainer.add(Box.createVerticalGlue());
        pnlPendingContainer.revalidate();
        pnlPendingContainer.repaint();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  UI BUILDERS
    // ══════════════════════════════════════════════════════════════════════

    /** Card hiển thị 1 ca trong tab Quản lý */
    private JPanel createShiftCard(LichLamViecDTO dto) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(14, 20, 14, 20));
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc: 18; border: 1,1,1,1, #E2E8F0,, 18;");
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        // Ô giờ bên trái
        String soGioStr = dto.getSoGio() != null
                ? String.format("%.1fh", dto.getSoGio()) : "?h";
        String gioDisplay = (dto.getGioVao() != null && dto.getGioRa() != null)
                ? dto.getGioVao() + " → " + dto.getGioRa()
                : (dto.getCaLam() != null ? dto.getCaLam() : "---");

        JPanel pnlTime = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTime.setOpaque(false);
        pnlTime.setPreferredSize(new Dimension(140, 0));

        JLabel lblGio = new JLabel(gioDisplay);
        lblGio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGio.setForeground(new Color(59, 130, 246));

        JLabel lblSoGio = new JLabel(soGioStr + " làm việc");
        lblSoGio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSoGio.setForeground(new Color(148, 163, 184));

        pnlTime.add(lblGio);
        pnlTime.add(lblSoGio);

        // Thông tin nhân viên giữa
        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlInfo.setOpaque(false);

        JLabel lblName = new JLabel("👤  " + dto.getHoTen());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(30, 41, 59));

        JLabel lblDetail = new JLabel(dto.getChucVu() + "   •   📞 " + dto.getSdt());
        lblDetail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetail.setForeground(new Color(100, 116, 139));

        pnlInfo.add(lblName);
        pnlInfo.add(lblDetail);

        // Nút xóa bên phải
        JButton btnXoa = new JButton("Xóa");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoa.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; borderWidth: 0; focusWidth: 0;" +
                        "background: #FEE2E2; foreground: #EF4444;" +
                        "hoverBackground: #FECACA; margin: 4,12,4,12;");
        btnXoa.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "Xóa ca làm của " + dto.getHoTen() + "?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (userClient.deleteSchedule(dto.getId())) loadScheduleFromDB();
                else JOptionPane.showMessageDialog(this, "Lỗi khi xóa!");
            }
        });

        card.add(pnlTime, BorderLayout.WEST);
        card.add(pnlInfo, BorderLayout.CENTER);
        card.add(btnXoa, BorderLayout.EAST);
        return card;
    }

    /** Card hiển thị 1 lịch chờ duyệt trong tab Duyệt lịch */
    private JPanel createPendingCard(LichLamViecDTO dto) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(14, 20, 14, 20));
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc: 18; border: 1,1,1,1, #FDE68A,, 18;"); // Viền vàng = chờ duyệt
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        // Thông tin
        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlInfo.setOpaque(false);

        String ngayStr = dto.getNgayLam() != null ? dto.getNgayLam().toString() : "---";
        String gioStr = (dto.getGioVao() != null && dto.getGioRa() != null)
                ? dto.getGioVao() + " → " + dto.getGioRa() : (dto.getCaLam() != null ? dto.getCaLam() : "---");
        String soGioStr = dto.getSoGio() != null ? String.format("%.1fh", dto.getSoGio()) : "?h";

        JLabel lblTop = new JLabel("👤  " + dto.getHoTen() + "   •   " + dto.getChucVu());
        lblTop.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTop.setForeground(new Color(30, 41, 59));

        JLabel lblBot = new JLabel("📅 " + ngayStr + "   🕐 " + gioStr + "   (" + soGioStr + ")");
        lblBot.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBot.setForeground(new Color(100, 116, 139));

        pnlInfo.add(lblTop);
        pnlInfo.add(lblBot);

        // Nút Duyệt / Từ chối
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBtns.setOpaque(false);

        JButton btnDuyet = new JButton("✔ Duyệt");
        btnDuyet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDuyet.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; borderWidth: 0; focusWidth: 0;" +
                        "background: #D1FAE5; foreground: #059669;" +
                        "hoverBackground: #A7F3D0; margin: 4,12,4,12;");
        btnDuyet.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDuyet.addActionListener(e -> {
            if (userClient.duyetLich(dto.getId())) loadPendingSchedules();
            else JOptionPane.showMessageDialog(this, "Lỗi khi duyệt lịch!");
        });

        JButton btnTuChoi = new JButton("✘ Từ chối");
        btnTuChoi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTuChoi.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; borderWidth: 0; focusWidth: 0;" +
                        "background: #FEE2E2; foreground: #EF4444;" +
                        "hoverBackground: #FECACA; margin: 4,12,4,12;");
        btnTuChoi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTuChoi.addActionListener(e -> {
            if (userClient.tuChoiLich(dto.getId())) loadPendingSchedules();
            else JOptionPane.showMessageDialog(this, "Lỗi khi từ chối lịch!");
        });

        pnlBtns.add(btnDuyet);
        pnlBtns.add(btnTuChoi);

        card.add(pnlInfo, BorderLayout.CENTER);
        card.add(pnlBtns, BorderLayout.EAST);
        return card;
    }

    private JPanel createEmptyState(String msg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);
        JLabel lbl = new JLabel(msg);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setBorder(new EmptyBorder(30, 0, 0, 0));
        p.add(lbl);
        return p;
    }

    private JButton createBtn(String text, String bgHex) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(145, 38));
        btn.putClientProperty(FlatClientProperties.STYLE,
                "background: " + bgHex + "; foreground: #FFFFFF;" +
                        "arc: 999; borderWidth: 0; focusWidth: 0;" +
                        "hoverBackground: lighten(" + bgHex + ", 10%);" +
                        "pressedBackground: darken(" + bgHex + ", 10%);");
        return btn;
    }
}