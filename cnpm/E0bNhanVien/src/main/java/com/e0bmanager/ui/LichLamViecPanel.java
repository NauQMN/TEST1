package com.e0bmanager.ui;

import com.e0bmanager.client.StaffApiClient;
import com.e0bmanager.dto.AccountDTO;
import com.e0bmanager.dto.LichLamViecDTO;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class LichLamViecPanel extends JPanel {

    private final AccountDTO account;
    private final StaffApiClient apiClient = new StaffApiClient();

    private static final Color BG           = new Color(245, 246, 250);
    private static final Color PURPLE_DARK  = new Color(88,  61, 172);
    private static final Color PURPLE_LIGHT = new Color(130, 90, 230);
    private static final Color WHITE        = Color.WHITE;
    private static final Color TEXT_DARK    = new Color(22,  27,  46);
    private static final Color TEXT_GRAY    = new Color(120, 130, 150);
    private static final Color GREEN        = new Color(16,  185, 129);
    private static final Color ORANGE       = new Color(245, 158, 11);
    private static final Color RED          = new Color(239, 68,  68);
    private static final Color BLUE         = new Color(59,  130, 246);

    private JPanel pnlDays;
    private JLabel lblWeekRange;
    private LocalDate weekStart;

    public LichLamViecPanel(AccountDTO account) {
        this.account = account;
        weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 24, 28));
        buildUI();
        loadData();
    }

    private void buildUI() {
        // ── Header ─────────────────────────────────────────────────────────
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitle = new JLabel("📅  Lịch Làm Việc");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        // Điều hướng tuần
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlNav.setOpaque(false);

        lblWeekRange = new JLabel();
        lblWeekRange.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWeekRange.setForeground(TEXT_GRAY);

        JButton btnPrev = createNavBtn("‹");
        JButton btnNext = createNavBtn("›");
        JButton btnToday = createTodayBtn();

        btnPrev.addActionListener(e -> { weekStart = weekStart.minusWeeks(1); updateWeekLabel(); loadData(); });
        btnNext.addActionListener(e -> { weekStart = weekStart.plusWeeks(1);  updateWeekLabel(); loadData(); });
        btnToday.addActionListener(e -> { weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY); updateWeekLabel(); loadData(); });

        pnlNav.add(btnToday);
        pnlNav.add(lblWeekRange);
        pnlNav.add(btnPrev);
        pnlNav.add(btnNext);

        pnlTop.add(lblTitle, BorderLayout.WEST);
        pnlTop.add(pnlNav,   BorderLayout.EAST);

        // ── Lưới 7 ngày ────────────────────────────────────────────────────
        pnlDays = new JPanel(new GridLayout(1, 7, 12, 0));
        pnlDays.setOpaque(false);

        add(pnlTop,   BorderLayout.NORTH);
        add(pnlDays,  BorderLayout.CENTER);
        updateWeekLabel();
    }

    private void updateWeekLabel() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        lblWeekRange.setText(weekStart.format(fmt) + " – " + weekStart.plusDays(6).format(fmt));
    }

    private void loadData() {
        pnlDays.removeAll();
        // Placeholder loading
        for (int i = 0; i < 7; i++) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(WHITE);
            p.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
            JLabel l = new JLabel("Đang tải...", SwingConstants.CENTER);
            l.setForeground(TEXT_GRAY);
            p.add(l, BorderLayout.CENTER);
            pnlDays.add(p);
        }
        pnlDays.revalidate();
        pnlDays.repaint();

        new Thread(() -> {
            // Load từng ngày song song
            JPanel[] cards = new JPanel[7];
            for (int i = 0; i < 7; i++) {
                LocalDate day = weekStart.plusDays(i);
                try {
                    List<LichLamViecDTO> list = apiClient.getLichTheoNgay(day.toString());
                    // Lọc theo nhanVienId của account hiện tại
                    List<LichLamViecDTO> myList = list.stream()
                            .filter(l -> account.getNhanVienId() != null &&
                                    String.valueOf(l.getNhanVienId()).equals(account.getNhanVienId()))
                            .toList();
                    cards[i] = buildDayCard(day, myList);
                } catch (Exception ex) {
                    cards[i] = buildDayCard(day, List.of());
                }
            }
            JPanel[] finalCards = cards;
            SwingUtilities.invokeLater(() -> {
                pnlDays.removeAll();
                for (JPanel c : finalCards) pnlDays.add(c);
                pnlDays.revalidate();
                pnlDays.repaint();
            });
        }).start();
    }

    private JPanel buildDayCard(LocalDate day, List<LichLamViecDTO> shifts) {
        boolean isToday = day.equals(LocalDate.now());

        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(isToday ? new Color(240, 235, 255) : WHITE);
        card.setBorder(new EmptyBorder(14, 12, 14, 12));
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc: 16; border: 1,1,1,1," + (isToday ? "#9370DB" : "#E2E8F0") + ",, 16;");

        // Header ngày
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlHeader.setOpaque(false);

        String tenNgay = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("vi", "VN"));
        JLabel lblDay = new JLabel(tenNgay.substring(0, 1).toUpperCase() + tenNgay.substring(1),
                SwingConstants.CENTER);
        lblDay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDay.setForeground(isToday ? PURPLE_DARK : TEXT_GRAY);

        JLabel lblDate = new JLabel(day.format(DateTimeFormatter.ofPattern("dd/MM")),
                SwingConstants.CENTER);
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDate.setForeground(isToday ? PURPLE_DARK : TEXT_DARK);

        pnlHeader.add(lblDay);
        pnlHeader.add(lblDate);

        // Ca làm
        JPanel pnlShifts = new JPanel();
        pnlShifts.setLayout(new BoxLayout(pnlShifts, BoxLayout.Y_AXIS));
        pnlShifts.setOpaque(false);
        pnlShifts.setBorder(new EmptyBorder(8, 0, 0, 0));

        if (shifts.isEmpty()) {
            JLabel lbl = new JLabel("Nghỉ", SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(new Color(180, 190, 200));
            lbl.setAlignmentX(CENTER_ALIGNMENT);
            pnlShifts.add(lbl);
        } else {
            for (LichLamViecDTO s : shifts) {
                pnlShifts.add(buildShiftChip(s));
                pnlShifts.add(Box.createVerticalStrut(6));
            }
        }

        card.add(pnlHeader, BorderLayout.NORTH);
        card.add(pnlShifts, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildShiftChip(LichLamViecDTO s) {
        Color[] colors = getShiftColors(s.getTrangThai());

        JPanel chip = new JPanel(new GridLayout(3, 1, 0, 2));
        chip.setBackground(colors[0]);
        chip.setBorder(new EmptyBorder(6, 8, 6, 8));
        chip.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        String gio = (s.getGioVao() != null && s.getGioRa() != null)
                ? s.getGioVao() + " – " + s.getGioRa() : "";
        JLabel lblGio = new JLabel(gio, SwingConstants.CENTER);
        lblGio.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblGio.setForeground(colors[1]);

        String caText = s.getCaLam() != null ? s.getCaLam() : "Ca làm";
        JLabel lblCa = new JLabel(caText.length() > 14 ? caText.substring(0, 13) + "…" : caText,
                SwingConstants.CENTER);
        lblCa.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCa.setForeground(colors[1]);

        JLabel lblStatus = new JLabel(s.getTrangThai() != null ? s.getTrangThai() : "", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblStatus.setForeground(colors[2]);

        chip.add(lblGio);
        chip.add(lblCa);
        chip.add(lblStatus);
        return chip;
    }

    private Color[] getShiftColors(String trangThai) {
        if (trangThai == null) return new Color[]{new Color(230,242,255), BLUE, BLUE};
        return switch (trangThai) {
            case "Đã duyệt", "Bình thường" -> new Color[]{new Color(209,250,229), GREEN, GREEN};
            case "Chờ duyệt"               -> new Color[]{new Color(254,243,199), ORANGE, ORANGE};
            case "Từ chối"                 -> new Color[]{new Color(254,226,226), RED, RED};
            default                        -> new Color[]{new Color(230,242,255), BLUE, BLUE};
        };
    }

    private JButton createNavBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(36, 36));
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 18; background: #FFFFFF; borderWidth: 1; focusWidth: 0;");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createTodayBtn() {
        JButton btn = new JButton("Hôm nay");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #583DAC; foreground: #FFFFFF; borderWidth: 0; focusWidth: 0;");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}