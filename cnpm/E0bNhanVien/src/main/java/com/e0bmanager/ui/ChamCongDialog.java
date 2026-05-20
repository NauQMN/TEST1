package com.e0bmanager.ui;

import com.e0bmanager.client.ChamCongClient;
import com.e0bmanager.client.ChamCongClient.CheckinStatus;
import com.e0bmanager.dto.AccountDTO;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Dialog chấm công — nhận diện khuôn mặt MediaPipe.
 *
 * FIX: startCheckin() được gọi TRƯỚC setVisible(true) trong StaffMainForm
 * vì setVisible(true) trên modal dialog block EDT, khiến camera không mở được.
 *
 * Luồng đúng (trong StaffMainForm):
 *   dlg.startCheckin();   // gọi trước — chạy trong Thread riêng
 *   dlg.setVisible(true); // mở dialog sau — block EDT nhưng camera đã chạy rồi
 */
public class ChamCongDialog extends JDialog {

    public interface OnConfirmedListener {
        void onConfirmed(String timestamp);
    }

    private final AccountDTO          account;
    private final ChamCongClient      client = new ChamCongClient();
    private final OnConfirmedListener listener;
    private       Timer               pollTimer;

    // ── Màu ──────────────────────────────────────────────────────────────
    private static final Color PURPLE_DARK  = new Color(88,  61, 172);
    private static final Color PURPLE_LIGHT = new Color(130, 90, 230);
    private static final Color WHITE        = Color.WHITE;
    private static final Color TEXT_DARK    = new Color(22,  27,  46);
    private static final Color TEXT_GRAY    = new Color(120, 130, 150);
    private static final Color GREEN        = new Color(16,  185, 129);
    private static final Color ORANGE       = new Color(245, 158, 11);
    private static final Color RED          = new Color(239, 68,  68);
    private static final Color BG           = new Color(245, 246, 250);

    // ── UI refs ───────────────────────────────────────────────────────────
    private JLabel       lblStatusIcon;
    private JLabel       lblStatusText;
    private JLabel       lblSubText;
    private JProgressBar progressBar;
    private JLabel       lblValueFace;
    private JLabel       lblValueConf;
    private JButton      btnAction;

    public ChamCongDialog(Frame parent, AccountDTO account, OnConfirmedListener listener) {
        super(parent, "Chấm Công — Nhận Diện Khuôn Mặt", true);
        this.account  = account;
        this.listener = listener;
        buildUI();
        setSize(460, 500);
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) { handleCancel(); }
        });
    }

    // ════════════════════════════════════════════════════════════════════
    //  BUILD UI
    // ════════════════════════════════════════════════════════════════════
    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(WHITE);

        // ── Header gradient ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PURPLE_DARK, getWidth(), 0, PURPLE_LIGHT));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 72));
        header.setBorder(new EmptyBorder(0, 28, 0, 28));

        JPanel pnlTitles = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitles.setOpaque(false);
        JLabel lblTitle = new JLabel("⏱  Chấm Công");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(WHITE);
        JLabel lblName = new JLabel(account.getFullname() != null
                ? account.getFullname() : account.getUsername());
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblName.setForeground(new Color(210, 200, 255));
        pnlTitles.add(lblTitle);
        pnlTitles.add(lblName);
        header.add(pnlTitles, BorderLayout.CENTER);

        // ── Body ─────────────────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(30, 40, 20, 40));

        // Icon trạng thái
        lblStatusIcon = new JLabel("⏳", SwingConstants.CENTER);
        lblStatusIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        lblStatusIcon.setAlignmentX(CENTER_ALIGNMENT);

        // Text chính
        lblStatusText = new JLabel("Đang kết nối tới server...", SwingConstants.CENTER);
        lblStatusText.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblStatusText.setForeground(TEXT_DARK);
        lblStatusText.setAlignmentX(CENTER_ALIGNMENT);

        // Text phụ
        lblSubText = new JLabel("Vui lòng chờ trong giây lát", SwingConstants.CENTER);
        lblSubText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubText.setForeground(TEXT_GRAY);
        lblSubText.setAlignmentX(CENTER_ALIGNMENT);

        // Progress bar (giữ mặt)
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        progressBar.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; background: #E2E8F0; foreground: #10B981;");

        // Stats 2 ô
        JPanel pnlStats = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlStats.setOpaque(false);
        pnlStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        pnlStats.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel cardFace = buildStatCard("👤  Khuôn mặt");
        JPanel cardConf = buildStatCard("🎯  Độ tin cậy");
        lblValueFace = (JLabel) cardFace.getComponent(1);
        lblValueConf = (JLabel) cardConf.getComponent(1);
        lblValueFace.setText("—");
        lblValueConf.setText("—");
        pnlStats.add(cardFace);
        pnlStats.add(cardConf);

        body.add(lblStatusIcon);
        body.add(Box.createVerticalStrut(14));
        body.add(lblStatusText);
        body.add(Box.createVerticalStrut(6));
        body.add(lblSubText);
        body.add(Box.createVerticalStrut(22));
        body.add(progressBar);
        body.add(pnlStats);

        // ── Nút hành động ────────────────────────────────────────────────
        btnAction = new JButton("Hủy chấm công");
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAction.setPreferredSize(new Dimension(200, 44));
        btnAction.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #FEE2E2; foreground: #EF4444; borderWidth: 0; focusWidth: 0;");
        btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAction.addActionListener(e -> handleCancel());

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
        pnlBottom.setBackground(WHITE);
        pnlBottom.add(btnAction);

        main.add(header,    BorderLayout.NORTH);
        main.add(body,      BorderLayout.CENTER);
        main.add(pnlBottom, BorderLayout.SOUTH);
        setContentPane(main);
    }

    private JPanel buildStatCard(String label) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 4));
        card.setBackground(BG);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12");

        JLabel lblLabel = new JLabel(label, SwingConstants.CENTER);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLabel.setForeground(TEXT_GRAY);

        JLabel lblValue = new JLabel("—", SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(PURPLE_DARK);

        card.add(lblLabel);
        card.add(lblValue);
        return card;
    }

    // ════════════════════════════════════════════════════════════════════
    //  LOGIC — được gọi TRƯỚC setVisible() từ StaffMainForm
    // ════════════════════════════════════════════════════════════════════
    public void startCheckin() {
        new Thread(() -> {
            // Bước 1: Kiểm tra server có sẵn không
            SwingUtilities.invokeLater(() -> setDetectingState("Đang kết nối server...", "Kiểm tra main.py đang chạy"));

            if (!client.isServerRunning()) {
                SwingUtilities.invokeLater(() -> showError(
                        "Server MediaPipe chưa chạy!\n\n" +
                                "Hãy mở terminal và chạy:\n" +
                                "  python main.py\n\n" +
                                "Sau đó nhấn Thử lại."));
                return;
            }

            // Bước 2: Gọi start-checkin
            try {
                SwingUtilities.invokeLater(() -> setDetectingState("Đang mở camera...", "Vui lòng chờ..."));
                client.startCheckin();

                // Bước 3: Đợi 800ms cho camera khởi động rồi mới bắt đầu polling
                Thread.sleep(800);
                SwingUtilities.invokeLater(() -> {
                    setDetectingState("Đang tìm khuôn mặt...", "Nhìn thẳng vào camera");
                    startPolling();
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> showError(
                        "Lỗi kết nối tới server MediaPipe:\n" + ex.getMessage()));
            }
        }).start();
    }

    // ── Polling mỗi 400ms ────────────────────────────────────────────────
    private void startPolling() {
        pollTimer = new Timer(400, e -> {
            new Thread(() -> {
                try {
                    CheckinStatus s = client.getStatus();
                    SwingUtilities.invokeLater(() -> updateUI(s));
                } catch (Exception ex) {
                    // Bỏ qua lỗi lẻ — chỉ hiện lỗi nếu liên tiếp nhiều lần
                }
            }).start();
        });
        pollTimer.start();
    }

    // ── Cập nhật UI theo trạng thái ──────────────────────────────────────
    private void updateUI(CheckinStatus s) {
        lblValueFace.setText(String.valueOf(s.faceCount));
        lblValueConf.setText(String.format("%.0f%%", s.confidence * 100));
        progressBar.setValue((int)(s.holdProgress * 100));

        switch (s.status) {
            case "detecting" -> {
                if (s.faceCount == 0) {
                    setDetectingState("Đang tìm khuôn mặt...", "Nhìn thẳng vào camera");
                    lblStatusIcon.setText("🔍");
                } else {
                    lblStatusIcon.setText("😊");
                    lblStatusText.setText("Đang xác nhận...");
                    lblStatusText.setForeground(PURPLE_DARK);
                    lblSubText.setText(String.format("Giữ nguyên vị trí — %.0f%%", s.holdProgress * 100));
                }
            }
            case "confirmed" -> { stopPolling(); showSuccess(); }
            case "timeout"   -> { stopPolling(); showTimeout(); }
            case "error"     -> { stopPolling(); showError("Camera gặp lỗi: " + s.message); }
        }
    }

    // ── Các trạng thái hiển thị ───────────────────────────────────────────
    private void setDetectingState(String main, String sub) {
        lblStatusIcon.setText("🔍");
        lblStatusText.setText(main);
        lblStatusText.setForeground(TEXT_DARK);
        lblSubText.setText(sub);
    }

    private void showSuccess() {
        String ts = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        lblStatusIcon.setText("✅");
        lblStatusText.setText("Chấm công thành công!");
        lblStatusText.setForeground(GREEN);
        lblSubText.setText("Thời gian: " + ts);
        progressBar.setValue(100);
        progressBar.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; background: #D1FAE5; foreground: #10B981;");

        // Đổi nút thành Đóng
        btnAction.setText("✔  Đóng");
        btnAction.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #D1FAE5; foreground: #059669; borderWidth: 0; focusWidth: 0;");
        for (var al : btnAction.getActionListeners()) btnAction.removeActionListener(al);
        btnAction.addActionListener(e -> {
            client.stop();
            dispose();
            if (listener != null) listener.onConfirmed(ts);
        });
    }

    private void showTimeout() {
        lblStatusIcon.setText("⏰");
        lblStatusText.setText("Hết thời gian!");
        lblStatusText.setForeground(ORANGE);
        lblSubText.setText("Không nhận diện được khuôn mặt.");

        btnAction.setText("🔄  Thử lại");
        btnAction.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #FEF3C7; foreground: #D97706; borderWidth: 0; focusWidth: 0;");
        for (var al : btnAction.getActionListeners()) btnAction.removeActionListener(al);
        btnAction.addActionListener(e -> {
            // Reset UI rồi thử lại
            progressBar.setValue(0);
            lblValueFace.setText("—");
            lblValueConf.setText("—");
            btnAction.setText("Hủy chấm công");
            btnAction.putClientProperty(FlatClientProperties.STYLE,
                    "arc: 14; background: #FEE2E2; foreground: #EF4444; borderWidth: 0; focusWidth: 0;");
            for (var al2 : btnAction.getActionListeners()) btnAction.removeActionListener(al2);
            btnAction.addActionListener(ev -> handleCancel());
            startCheckin();
        });
    }

    private void showError(String msg) {
        stopPolling();
        lblStatusIcon.setText("❌");
        lblStatusText.setText("Lỗi kết nối!");
        lblStatusText.setForeground(RED);
        lblSubText.setText("<html><center>" + msg.replace("\n", "<br>") + "</center></html>");
        progressBar.setValue(0);

        btnAction.setText("🔄  Thử lại");
        btnAction.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #FEF3C7; foreground: #D97706; borderWidth: 0; focusWidth: 0;");
        for (var al : btnAction.getActionListeners()) btnAction.removeActionListener(al);
        btnAction.addActionListener(e -> {
            lblValueFace.setText("—");
            lblValueConf.setText("—");
            btnAction.setText("Hủy chấm công");
            btnAction.putClientProperty(FlatClientProperties.STYLE,
                    "arc: 14; background: #FEE2E2; foreground: #EF4444; borderWidth: 0; focusWidth: 0;");
            for (var al2 : btnAction.getActionListeners()) btnAction.removeActionListener(al2);
            btnAction.addActionListener(ev -> handleCancel());
            startCheckin();
        });
    }

    private void handleCancel() {
        stopPolling();
        client.stop();
        dispose();
    }

    private void stopPolling() {
        if (pollTimer != null && pollTimer.isRunning()) pollTimer.stop();
    }
}