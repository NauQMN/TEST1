package com.e0bmanager.ui;

import com.e0bmanager.client.AuthClient;
import com.e0bmanager.dto.AccountDTO;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class LoginForm extends JFrame {
    // Logic fields
    private JTextField txtUser;
    private JPasswordField txtPass;
    private AuthClient authClient = new AuthClient();

    // UI components mẫu
    private JButton btnLogin;
    private JLabel lblSignUp;

    public LoginForm() {
        setAppLogo();
        // 1. Khởi tạo FlatLaf để có giao diện bo góc hiện đại
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Cấu hình Frame
        setTitle("E0b Staff - Login");
        setSize(850, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 3. Panel chính chia làm 2 phần (GridLayout 1 hàng 2 cột)
        JPanel pnlMain = new JPanel(new GridLayout(1, 2));
        pnlMain.setBackground(Color.WHITE);

        // --- PHẦN BÊN TRÁI: BACKGROUND & WELCOME ---
        BackgroundPanel pnlLeft = new BackgroundPanel();
        pnlLeft.setLayout(new BorderLayout());
        pnlLeft.setBorder(new EmptyBorder(50, 40, 50, 40));

        JLabel lblWelcome = new JLabel("<html>Welcome<br>Back!</html>");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 45));
        lblWelcome.setForeground(Color.WHITE);
        pnlLeft.add(lblWelcome, BorderLayout.NORTH);

        JLabel lblCopy = new JLabel("© 2026 E0b Manager System");
        lblCopy.setForeground(new Color(255, 255, 255, 180));
        pnlLeft.add(lblCopy, BorderLayout.SOUTH);

        // --- PHẦN BÊN PHẢI: FORM ĐĂNG NHẬP ---
        JPanel pnlRight = new JPanel();
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(new EmptyBorder(60, 50, 60, 50));

        // Logo & Title
        JLabel lblTitle = new JLabel("Welcome to Company");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Sign in to continue");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input Fields
        txtUser = new JTextField();
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "👤 Tên đăng nhập");
        txtUser.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        txtPass = new JPasswordField();
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🔒 Mật khẩu");
        txtPass.putClientProperty(FlatClientProperties.STYLE, "arc: 15; showRevealButton: true");

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlButtons.setOpaque(false);

        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(120, 40));
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "arc: 20; borderWidth: 0; focusWidth: 0");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblSignUp = new JLabel("Sign up");
        lblSignUp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSignUp.setForeground(new Color(44, 62, 80));
        lblSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlButtons.add(btnLogin);
        pnlButtons.add(lblSignUp);

        // Thêm vào Panel Right
        pnlRight.add(lblTitle);
        pnlRight.add(Box.createVerticalStrut(5));
        pnlRight.add(lblSub);
        pnlRight.add(Box.createVerticalStrut(40));
        pnlRight.add(txtUser);
        pnlRight.add(Box.createVerticalStrut(20));
        pnlRight.add(txtPass);
        pnlRight.add(Box.createVerticalStrut(30));
        pnlRight.add(pnlButtons);

        // 4. Gộp vào Frame
        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);
        add(pnlMain);

        // --- 5. Xử lý Sự kiện (Logic) ---
        btnLogin.addActionListener(e -> handleLogin());

        lblSignUp.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new RegisterForm().setVisible(true);
                dispose();
            }
        });
    }

    private void handleLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Vô hiệu hóa nút để tránh nhấn nhiều lần
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        new Thread(() -> {
            try {
                AccountDTO account = authClient.login(user, pass);
                if (account != null) {
                    SwingUtilities.invokeLater(() -> {
                        new StaffMainForm(account).setVisible(true);
                        this.dispose();
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                });
            }
        }).start();
    }

    // Inner class để vẽ hình nền Panel bên trái
    class BackgroundPanel extends JPanel {
        private Image img;

        public BackgroundPanel() {
            try {
                // Đảm bảo đường dẫn file ảnh chính xác trong resources
                img = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/login_bg.png"))).getImage();
            } catch (Exception e) {
                System.err.println("Không tìm thấy file login_bg.png");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Fallback nếu không có ảnh: vẽ màu Gradient
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185), getWidth(), getHeight(), new Color(109, 213, 250));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
    private void setAppLogo() {
        try {
            // Thay đổi "/icons/logo.png" thành đường dẫn đúng tới file ảnh của bạn
            java.net.URL iconURL = getClass().getResource("/icons/logo.png");
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                this.setIconImage(icon.getImage()); // Set logo cho Window/Taskbar
            } else {
                System.err.println("Không tìm thấy file logo!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}