package com.e0bmanager.ui;

import com.e0bmanager.client.StaffApiClient;
import com.e0bmanager.dto.AccountDTO;
import com.e0bmanager.dto.LuongDTO;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class LuongPanel extends JPanel {

    private final AccountDTO account;
    private final StaffApiClient apiClient = new StaffApiClient();

    private static final Color BG          = new Color(245, 246, 250);
    private static final Color PURPLE_DARK = new Color(88,  61, 172);
    private static final Color PURPLE_LIGHT= new Color(130, 90, 230);
    private static final Color WHITE       = Color.WHITE;
    private static final Color TEXT_DARK   = new Color(22,  27,  46);
    private static final Color TEXT_GRAY   = new Color(120, 130, 150);
    private static final Color GREEN       = new Color(16,  185, 129);

    private JSpinner spnThang;
    private JSpinner spnNam;
    private JPanel pnlResult;

    public LuongPanel(AccountDTO account) {
        this.account = account;
        setBackground(BG);
        setLayout(new BorderLayout(0, 24));
        setBorder(new EmptyBorder(24, 28, 24, 28));
        buildUI();
    }

    private void buildUI() {
        // ── Header ─────────────────────────────────────────────────────────
        JLabel lblTitle = new JLabel("💵  Thông Tin Lương");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(0, 0, 4, 0));

        // ── Bộ chọn tháng / năm ────────────────────────────────────────────
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlFilter.setOpaque(false);

        JLabel lblThang = new JLabel("Tháng:");
        lblThang.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        spnThang = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        spnThang.setPreferredSize(new Dimension(70, 36));

        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        spnNam = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2099, 1));
        spnNam.setPreferredSize(new Dimension(90, 36));

        JButton btnXem = new JButton("Xem lương");
        btnXem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXem.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #583DAC; foreground: #FFFFFF; borderWidth: 0; focusWidth: 0;");
        btnXem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXem.addActionListener(e -> loadLuong());

        pnlFilter.add(lblThang);
        pnlFilter.add(spnThang);
        pnlFilter.add(Box.createHorizontalStrut(8));
        pnlFilter.add(lblNam);
        pnlFilter.add(spnNam);
        pnlFilter.add(Box.createHorizontalStrut(8));
        pnlFilter.add(btnXem);

        // ── Khu vực hiển thị kết quả ───────────────────────────────────────
        pnlResult = new JPanel(new GridBagLayout());
        pnlResult.setOpaque(false);
        showPlaceholder("Chọn tháng và nhấn \"Xem lương\" để xem thông tin lương.");

        JPanel pnlTop = new JPanel(new BorderLayout(0, 16));
        pnlTop.setOpaque(false);
        pnlTop.add(lblTitle,   BorderLayout.NORTH);
        pnlTop.add(pnlFilter,  BorderLayout.CENTER);

        add(pnlTop,    BorderLayout.NORTH);
        add(pnlResult, BorderLayout.CENTER);

        // Tự động load tháng hiện tại
        loadLuong();
    }

    private void loadLuong() {
        if (account.getNhanVienId() == null) {
            showPlaceholder("⚠️  Tài khoản chưa được liên kết với hồ sơ nhân viên.");
            return;
        }

        int thang = (int) spnThang.getValue();
        int nam   = (int) spnNam.getValue();
        int nvId;
        try {
            nvId = Integer.parseInt(account.getNhanVienId());
        } catch (NumberFormatException ex) {
            showPlaceholder("⚠️  Mã nhân viên không hợp lệ.");
            return;
        }

        showPlaceholder("⏳  Đang tính lương...");

        int finalNvId = nvId;
        new Thread(() -> {
            try {
                LuongDTO dto = apiClient.tinhLuong(finalNvId, thang, nam);
                SwingUtilities.invokeLater(() -> showLuong(dto));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        showPlaceholder("❌  " + ex.getMessage()));
            }
        }).start();
    }

    private void showLuong(LuongDTO dto) {
        pnlResult.removeAll();
        pnlResult.setLayout(new BorderLayout(0, 20));

        // ── Card tổng lương ────────────────────────────────────────────────
        JPanel cardTotal = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PURPLE_DARK, getWidth(), getHeight(), PURPLE_LIGHT));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        cardTotal.setOpaque(false);
        cardTotal.setBorder(new EmptyBorder(28, 36, 28, 36));
        cardTotal.setPreferredSize(new Dimension(0, 140));

        JLabel lblSubTotal = new JLabel("Tổng thu nhập tháng " + dto.getThang() + "/" + dto.getNam());
        lblSubTotal.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubTotal.setForeground(new Color(210, 200, 255));

        double tongLuong = dto.getTongLuong() != null ? dto.getTongLuong()
                : tinhTong(dto);
        JLabel lblTong = new JLabel(formatVND(tongLuong));
        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTong.setForeground(Color.WHITE);

        JLabel lblName = new JLabel(dto.getTenNv() != null ? "👤  " + dto.getTenNv() : "");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblName.setForeground(new Color(200, 190, 255));

        cardTotal.add(lblSubTotal, BorderLayout.NORTH);
        cardTotal.add(lblTong,     BorderLayout.CENTER);
        cardTotal.add(lblName,     BorderLayout.SOUTH);

        // ── Lưới chi tiết ─────────────────────────────────────────────────
        JPanel pnlDetails = new JPanel(new GridLayout(2, 3, 16, 16));
        pnlDetails.setOpaque(false);

        pnlDetails.add(buildDetailCard("💼", "Lương cơ bản / giờ",
                formatVND(dto.getLuongCoBan()), new Color(230, 242, 255), new Color(59, 130, 246)));
        pnlDetails.add(buildDetailCard("⏱", "Số giờ làm việc",
                String.format("%.1f giờ", dto.getSoGioThucTe() != null ? dto.getSoGioThucTe() : 0.0),
                new Color(209, 250, 229), new Color(16, 185, 129)));
        pnlDetails.add(buildDetailCard("📋", "Số ca làm việc",
                (dto.getSoCa() != null ? dto.getSoCa() : 0) + " ca",
                new Color(254, 243, 199), new Color(245, 158, 11)));
        pnlDetails.add(buildDetailCard("📈", "Hệ số thưởng",
                dto.getHeSo() != null ? dto.getHeSo() : "0%",
                new Color(243, 232, 255), new Color(168, 85, 247)));
        pnlDetails.add(buildDetailCard("🎁", "Phụ cấp",
                formatVND(dto.getPhuCap()),
                new Color(255, 237, 213), new Color(249, 115, 22)));
        pnlDetails.add(buildDetailCard("✅", "Trạng thái",
                "Đã tính", new Color(209, 250, 229), GREEN));

        pnlResult.add(cardTotal,    BorderLayout.NORTH);
        pnlResult.add(pnlDetails,   BorderLayout.CENTER);
        pnlResult.revalidate();
        pnlResult.repaint();
    }

    private JPanel buildDetailCard(String icon, String label, String value,
                                   Color bgColor, Color accentColor) {
        JPanel card = new JPanel(new GridLayout(3, 1, 0, 4));
        card.setBackground(WHITE);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc: 16; border: 1,1,1,1, #E2E8F0,, 16;");

        JLabel lblIcon = new JLabel(icon + "  " + label);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblIcon.setForeground(TEXT_GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(accentColor);

        // Thanh màu nhỏ
        JPanel bar = new JPanel();
        bar.setBackground(bgColor);
        bar.setPreferredSize(new Dimension(0, 4));
        bar.putClientProperty(FlatClientProperties.STYLE, "arc: 4");

        card.add(lblIcon);
        card.add(lblValue);
        card.add(bar);
        return card;
    }

    private void showPlaceholder(String msg) {
        pnlResult.removeAll();
        pnlResult.setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("<html><center>" + msg + "</center></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(TEXT_GRAY);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        pnlResult.add(lbl);
        pnlResult.revalidate();
        pnlResult.repaint();
    }

    private double tinhTong(LuongDTO dto) {
        double luong = dto.getLuongCoBan() != null ? dto.getLuongCoBan() : 0;
        double gio   = dto.getSoGioThucTe() != null ? dto.getSoGioThucTe() : 0;
        double phuCap= dto.getPhuCap() != null ? dto.getPhuCap() : 0;
        double heSo  = parseHeSo(dto.getHeSo());
        return luong * gio * (1 + heSo) + phuCap;
    }

    private double parseHeSo(String heSo) {
        if (heSo == null || heSo.isBlank()) return 0;
        try { return Double.parseDouble(heSo.replace("%", "").trim()) / 100.0; }
        catch (Exception e) { return 0; }
    }

    private String formatVND(Double amount) {
        if (amount == null) return "0 ₫";
        return NumberFormat.getNumberInstance(new Locale("vi", "VN"))
                .format(amount.longValue()) + " ₫";
    }
}