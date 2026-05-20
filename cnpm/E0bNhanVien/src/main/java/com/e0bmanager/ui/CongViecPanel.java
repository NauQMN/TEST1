package com.e0bmanager.ui;

import com.e0bmanager.client.StaffApiClient;
import com.e0bmanager.dto.AccountDTO;
import com.e0bmanager.dto.GiaoViecDTO;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CongViecPanel extends JPanel {

    private final AccountDTO account;
    private final StaffApiClient apiClient = new StaffApiClient();

    private static final Color BG          = new Color(245, 246, 250);
    private static final Color WHITE       = Color.WHITE;
    private static final Color TEXT_DARK   = new Color(22,  27,  46);
    private static final Color TEXT_GRAY   = new Color(120, 130, 150);
    private static final Color GREEN       = new Color(16,  185, 129);
    private static final Color ORANGE      = new Color(245, 158, 11);
    private static final Color RED         = new Color(239, 68,  68);
    private static final Color PURPLE_DARK = new Color(88,  61, 172);

    private JPanel pnlList;
    private JLabel lblDate;
    private LocalDate selectedDate;

    public CongViecPanel(AccountDTO account) {
        this.account = account;
        this.selectedDate = LocalDate.now();
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

        JLabel lblTitle = new JLabel("📋  Công Việc Được Giao");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        // Điều hướng ngày
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlNav.setOpaque(false);

        lblDate = new JLabel();
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDate.setForeground(PURPLE_DARK);
        updateDateLabel();

        JButton btnPrev   = createSmallBtn("‹");
        JButton btnNext   = createSmallBtn("›");
        JButton btnToday  = createTodayBtn();

        btnPrev.addActionListener(e  -> { selectedDate = selectedDate.minusDays(1); updateDateLabel(); loadData(); });
        btnNext.addActionListener(e  -> { selectedDate = selectedDate.plusDays(1);  updateDateLabel(); loadData(); });
        btnToday.addActionListener(e -> { selectedDate = LocalDate.now();           updateDateLabel(); loadData(); });

        pnlNav.add(btnToday);
        pnlNav.add(lblDate);
        pnlNav.add(btnPrev);
        pnlNav.add(btnNext);

        pnlTop.add(lblTitle, BorderLayout.WEST);
        pnlTop.add(pnlNav,   BorderLayout.EAST);

        // ── Danh sách công việc ─────────────────────────────────────────────
        pnlList = new JPanel();
        pnlList.setLayout(new BoxLayout(pnlList, BoxLayout.Y_AXIS));
        pnlList.setOpaque(false);

        JScrollPane scroll = new JScrollPane(pnlList);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        add(pnlTop,  BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);
    }

    private void updateDateLabel() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new java.util.Locale("vi", "VN"));
        String text = selectedDate.format(fmt);
        lblDate.setText(text.substring(0, 1).toUpperCase() + text.substring(1));
    }

    private void loadData() {
        pnlList.removeAll();
        JLabel loading = new JLabel("⏳  Đang tải công việc...", SwingConstants.CENTER);
        loading.setForeground(TEXT_GRAY);
        loading.setAlignmentX(CENTER_ALIGNMENT);
        pnlList.add(Box.createVerticalStrut(40));
        pnlList.add(loading);
        pnlList.revalidate(); pnlList.repaint();

        LocalDate date = selectedDate;
        new Thread(() -> {
            try {
                List<GiaoViecDTO> all = apiClient.getCongViecTheoNgay(date.toString());
                // Lọc theo nhân viên hiện tại
                List<GiaoViecDTO> mine = all.stream()
                        .filter(g -> account.getNhanVienId() != null &&
                                String.valueOf(g.getNhanVienId()).equals(account.getNhanVienId()))
                        .toList();
                SwingUtilities.invokeLater(() -> renderList(mine));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> showEmpty("❌  " + ex.getMessage()));
            }
        }).start();
    }

    private void renderList(List<GiaoViecDTO> tasks) {
        pnlList.removeAll();
        if (tasks.isEmpty()) {
            showEmpty("🎉  Không có công việc nào trong ngày này!");
            return;
        }

        // Thống kê nhanh
        long done    = tasks.stream().filter(t -> "Hoàn thành".equals(t.getTrangThai())).count();
        long pending = tasks.size() - done;

        JPanel pnlStats = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pnlStats.setOpaque(false);
        pnlStats.setBorder(new EmptyBorder(0, 0, 16, 0));
        pnlStats.add(buildBadge("Tổng: " + tasks.size(), new Color(230,242,255), new Color(59,130,246)));
        pnlStats.add(buildBadge("Hoàn thành: " + done,   new Color(209,250,229), GREEN));
        pnlStats.add(buildBadge("Còn lại: " + pending,   new Color(254,243,199), ORANGE));
        pnlStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnlList.add(pnlStats);

        for (GiaoViecDTO task : tasks) {
            pnlList.add(buildTaskRow(task));
            pnlList.add(Box.createVerticalStrut(12));
        }
        pnlList.revalidate();
        pnlList.repaint();
    }

    private JPanel buildTaskRow(GiaoViecDTO task) {
        boolean isDone = "Hoàn thành".equals(task.getTrangThai());

        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(WHITE);
        row.setBorder(new EmptyBorder(16, 20, 16, 20));
        row.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; border: 1,1,1,1," + (isDone ? "#D1FAE5" : "#E2E8F0") + ",, 14;");
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Icon trạng thái
        JLabel lblIcon = new JLabel(isDone ? "✅" : "🔲");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Nội dung
        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlText.setOpaque(false);

        JLabel lblTen = new JLabel(task.getTenCongViec() != null ? task.getTenCongViec() : "—");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTen.setForeground(isDone ? TEXT_GRAY : TEXT_DARK);

        String subText = (task.getCaLamViec() != null ? "Ca: " + task.getCaLamViec() : "");
        JLabel lblSub = new JLabel(subText);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(TEXT_GRAY);

        pnlText.add(lblTen);
        pnlText.add(lblSub);

        // Badge trạng thái + nút đổi
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlRight.setOpaque(false);

        JLabel lblStatus = buildBadge(
                task.getTrangThai() != null ? task.getTrangThai() : "Chưa xong",
                isDone ? new Color(209,250,229) : new Color(254,226,226),
                isDone ? GREEN : RED);

        JButton btnAction = new JButton(isDone ? "↩ Mở lại" : "✔ Xong");
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAction.putClientProperty(FlatClientProperties.STYLE,
                isDone
                        ? "arc: 12; background: #F3F4F6; foreground: #6B7280; borderWidth: 0; focusWidth: 0;"
                        : "arc: 12; background: #10B981; foreground: #FFFFFF; borderWidth: 0; focusWidth: 0;");
        btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String newStatus = isDone ? "Chưa xong" : "Hoàn thành";
        btnAction.addActionListener(e -> capNhatTrangThai(task.getId(), newStatus));

        pnlRight.add(lblStatus);
        pnlRight.add(btnAction);

        row.add(lblIcon,  BorderLayout.WEST);
        row.add(pnlText,  BorderLayout.CENTER);
        row.add(pnlRight, BorderLayout.EAST);
        return row;
    }

    private void capNhatTrangThai(int id, String trangThai) {
        new Thread(() -> {
            try {
                apiClient.capNhatTrangThaiCongViec(id, trangThai);
                SwingUtilities.invokeLater(this::loadData);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                                "Lỗi cập nhật: " + ex.getMessage(), "Lỗi",
                                JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void showEmpty(String msg) {
        pnlList.removeAll();
        pnlList.add(Box.createVerticalStrut(60));
        JLabel lbl = new JLabel("<html><center>" + msg + "</center></html>",
                SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setForeground(TEXT_GRAY);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        pnlList.add(lbl);
        pnlList.revalidate(); pnlList.repaint();
    }

    private JLabel buildBadge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(new EmptyBorder(4, 10, 4, 10));
        lbl.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        return lbl;
    }

    private JButton createSmallBtn(String text) {
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