package e0bmanager.ui;

import e0bmanager.client.UserClient;
import e0bmanager.dto.UserDTO;
import e0bmanager.utils.SessionManager;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private final UserClient userClient = new UserClient();

    private final Color COLOR_PRIMARY = new Color(44, 62, 80);
    private final Color COLOR_SECONDARY = new Color(52, 73, 94);

    public LoginForm() {
        initComponents();
        setAppLogo();
        setTitle("E0b Manager - Đăng nhập");
        setSize(850, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        JPanel pnlMain = new JPanel(new GridLayout(1, 2));

        // =========================================================
        // 1. PHẦN BÊN TRÁI (FORM TRẮNG)
        // =========================================================
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(Color.WHITE);
        GridBagConstraints gbcL = new GridBagConstraints(); // Dùng riêng gbcL cho bên trái
        gbcL.insets = new Insets(10, 50, 10, 50);
        gbcL.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblSignin = new JLabel("Đăng nhập");
        lblSignin.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblSignin.setForeground(COLOR_PRIMARY);
        gbcL.gridx = 0; gbcL.gridy = 0;
        pnlLeft.add(lblSignin, gbcL);

        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(300, 45));
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tên đăng nhập");
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #f0f0f0; borderWidth: 0");
        gbcL.gridy = 1;
        pnlLeft.add(txtUsername, gbcL);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(300, 45));
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mật khẩu");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #f0f0f0; borderWidth: 0");
        gbcL.gridy = 2;
        pnlLeft.add(txtPassword, gbcL);

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setPreferredSize(new Dimension(300, 45));
        btnLogin.setBackground(new Color(26, 188, 156));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(this::handleLogin);
        gbcL.gridy = 3; gbcL.insets = new Insets(25, 50, 10, 50);
        pnlLeft.add(btnLogin, gbcL);

        // =========================================================
        // 2. PHẦN BÊN PHẢI (MÀU XANH SẪM)
        // =========================================================
        JPanel pnlRight = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gd = new GradientPaint(0, 0, COLOR_PRIMARY, 0, getHeight(), COLOR_SECONDARY);
                g2.setPaint(gd);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        GridBagConstraints gbcR = new GridBagConstraints(); // Dùng riêng gbcR cho bên phải
        gbcR.insets = new Insets(15, 40, 15, 40);
        gbcR.gridx = 0;

        JLabel lblWelcome = new JLabel("Chào mừng trở lại!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(Color.WHITE);
        gbcR.gridy = 0;
        pnlRight.add(lblWelcome, gbcR);

        JTextArea txtDesc = new JTextArea("Chúng tôi rất vui khi thấy bạn quay lại. Chúc bạn có một ngày làm việc hiệu quả.");
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtDesc.setForeground(new Color(236, 240, 241));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setOpaque(false);
        txtDesc.setEditable(false);
        txtDesc.setPreferredSize(new Dimension(280, 60));
        gbcR.gridy = 1;
        pnlRight.add(txtDesc, gbcR);

        // Nút Đăng ký (Đặt ở panel bên phải)
        JButton btnSwitchSignup = new JButton("Chưa có tài khoản? Đăng ký");
        btnSwitchSignup.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSwitchSignup.setForeground(Color.WHITE);
        btnSwitchSignup.setContentAreaFilled(false);
        btnSwitchSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tạo viền trắng bo tròn cho nút
        btnSwitchSignup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        btnSwitchSignup.addActionListener(e -> {
            new RegisterForm().setVisible(true);
            this.dispose();
        });

        gbcR.gridy = 2;
        gbcR.insets = new Insets(30, 40, 10, 40);
        pnlRight.add(btnSwitchSignup, gbcR);

        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);
        add(pnlMain);

        getRootPane().setDefaultButton(btnLogin);
    }

    private void handleLogin(ActionEvent e) {
        // ... (Giữ nguyên logic login cũ) ...
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tài khoản!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            btnLogin.setEnabled(false);
            btnLogin.setText("ĐANG XÁC THỰC...");

            UserDTO loggedInUser = userClient.login(user, pass);
            if (loggedInUser != null) {
                SessionManager.currentUser = loggedInUser;
                new MainForm(loggedInUser).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tài khoản hoặc mật khẩu không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                btnLogin.setEnabled(true);
                btnLogin.setText("ĐĂNG NHẬP");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối tới Server API!");
            btnLogin.setEnabled(true);
            btnLogin.setText("ĐĂNG NHẬP");
        }
    }
    private void setAppLogo() {
        try {
            // Thay đổi "/icons/logo.png" thành đường dẫn đúng tới file ảnh của bạn
            java.net.URL iconURL = getClass().getResource("/logo.png");
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