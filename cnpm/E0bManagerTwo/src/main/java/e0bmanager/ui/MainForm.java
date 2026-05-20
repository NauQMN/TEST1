package e0bmanager.ui;

import e0bmanager.client.UserClient;
import e0bmanager.dto.UserDTO;
import e0bmanager.panels.*;
import e0bmanager.utils.NotificationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;

public class MainForm extends JFrame {
    private CardLayout cardLayout;
    private JPanel pnlContainer;
    private String loggedInUser;
    private BottomTaskbar taskbar;
    private TopBar topBar;
    private NotificationManager notificationManager;
    String testBase64;

    // Tên hiển thị trên TopBar cho từng màn hình
    private static final Map<String, String> PAGE_TITLES = Map.of(
            "Tổng quan",      "📊  Tổng quan hệ thống",
            "Nhân viên",      "🧑‍💼  Quản lý nhân viên",
            "Lịch làm việc",  "📅  Lịch làm việc",
            "Giao việc",      "📋  Giao việc",
            "Tính lương",     "💵  Tính lương",
            "Đánh giá",       "⭐  Đánh giá nhân viên",
            "Cá nhân",        "👤  Hồ sơ cá nhân"
    );

    public MainForm(UserDTO user) {
        setTitle("E0b Manager - Hệ thống Quản lý Cửa hàng");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setAppLogo();
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        pnlContainer = new JPanel(cardLayout);

        ActionListener navListener = e -> handleNavigation(e.getActionCommand());

        pnlContainer.add(new HomePanel(loggedInUser, navListener), "TRANG_CHU");

        // ── Notification system ──────────────────────────────────────────
        notificationManager = new NotificationManager(new UserClient());

        // ── TopBar (cố định NORTH, ẩn ở HomePanel) ──────────────────────
        topBar = new TopBar(notificationManager);
        topBar.setVisible(false);

        // ── BottomTaskbar (cố định SOUTH, ẩn ở HomePanel) ───────────────
        taskbar = new BottomTaskbar(navListener);
        taskbar.setVisible(false);

        setLayout(new BorderLayout());
        add(topBar,        BorderLayout.NORTH);
        add(pnlContainer,  BorderLayout.CENTER);
        add(taskbar,       BorderLayout.SOUTH);

        cardLayout.show(pnlContainer, "TRANG_CHU");

        // ── Polling thông báo mỗi 60 giây ───────────────────────────────
        startNotificationPolling();
    }

    /** Kiểm tra thông báo ngay lần đầu và sau mỗi 60 giây */
    private void startNotificationPolling() {
        // Lần đầu sau 2 giây (đợi app ổn định)
        Timer firstCheck = new Timer(2000, e -> refreshNotifications());
        firstCheck.setRepeats(false);
        firstCheck.start();

        // Sau đó mỗi 60 giây
        Timer polling = new Timer(60_000, e -> refreshNotifications());
        polling.start();
    }

    private void refreshNotifications() {
        new Thread(() -> {
            notificationManager.refresh();
            SwingUtilities.invokeLater(() -> topBar.updateBell());
        }).start();
    }

    public void handleNavigation(String command) {
        System.out.println("Command nhận được: " + command);
        ActionListener navListener = e -> handleNavigation(e.getActionCommand());

        switch (command) {
            case "TRANG_CHU" -> {
                topBar.setVisible(false);
                taskbar.setVisible(false);
                cardLayout.show(pnlContainer, "TRANG_CHU");
                return;
            }
            case "Tổng quan" -> {
                pnlContainer.add(new TongQuanPanel(this, navListener), "Tổng quan");
            }
            case "Nhân viên" -> {
                pnlContainer.add(new EmployeePanel(this, navListener), "Nhân viên");
            }
            case "Lịch làm việc" -> {
                pnlContainer.add(new WorkSchedulePanel(this, navListener), "Lịch làm việc");
            }
            case "Giao việc" -> {
                pnlContainer.add(new GiaoViecPanel(this, navListener), "Giao việc");
            }
            case "Tính lương" -> {
                pnlContainer.add(new TinhLuongPanel(this, navListener), "Tính lương");
            }
            case "Đánh giá" -> {
                pnlContainer.add(new DanhGiaPanel(this, navListener), "Đánh giá");
            }
            case "Cá nhân" -> {
                pnlContainer.add(new CaNhanPanel(navListener), "Cá nhân");
                showPage("Cá nhân");
                return;
            }
            case "Đăng xuất" -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    new LoginForm().setVisible(true);
                    this.dispose();
                }
                return;
            }
            default -> {
                System.out.println("Lệnh không xác định: " + command);
                return;
            }
        }
        showPage(command);
    }

    /** Hiển thị panel và cập nhật TopBar */
    private void showPage(String command) {
        topBar.setVisible(true);
        taskbar.setVisible(true);
        topBar.setPageTitle(PAGE_TITLES.getOrDefault(command, command));
        cardLayout.show(pnlContainer, command);
    }

    private void setAppLogo() {
        try {
            java.net.URL iconURL = getClass().getResource("/logo.png");
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                this.setIconImage(icon.getImage());
            } else {
                System.err.println("Không tìm thấy file logo!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}