package e0bmanager.ui;

import e0bmanager.client.UserClient;
import e0bmanager.dto.UserDTO;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JFrame {
    private JTextField txtUsername, txtFullname, txtEmail;
    private JPasswordField txtPassword, txtConfirm;
    private JButton btnSignup;
    private final UserClient userClient = new UserClient();
    private final Color COLOR_PRIMARY = new Color(44, 62, 80);

    public RegisterForm() {
        initComponents();
        setTitle("E0b Manager - Đăng ký tài khoản");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initComponents() {
        JPanel pnlMain = new JPanel(new GridLayout(1, 2));

        // BÊN TRÁI: Thông tin chào mừng (Màu xanh sẫm)
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(COLOR_PRIMARY);
        JLabel lblInfo = new JLabel("Tham gia cùng chúng tôi!");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblInfo.setForeground(Color.WHITE);
        pnlLeft.add(lblInfo);

        // BÊN PHẢI: Form đăng ký
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 50, 8, 50); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0;

        JLabel lblTitle = new JLabel("Tạo tài khoản");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridy = 0; pnlRight.add(lblTitle, gbc);

        txtUsername = createStyledField("Tên đăng nhập");
        gbc.gridy = 1; pnlRight.add(txtUsername, gbc);

        txtFullname = createStyledField("Họ và tên");
        gbc.gridy = 2; pnlRight.add(txtFullname, gbc);

        txtEmail = createStyledField("Email");
        gbc.gridy = 3; pnlRight.add(txtEmail, gbc);

        txtPassword = createStyledPassField("Mật khẩu");
        gbc.gridy = 4; pnlRight.add(txtPassword, gbc);

        txtConfirm = createStyledPassField("Xác nhận mật khẩu");
        gbc.gridy = 5; pnlRight.add(txtConfirm, gbc);

        btnSignup = new JButton("Đăng ký");
        btnSignup.setBackground(new Color(26, 188, 156));
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSignup.setPreferredSize(new Dimension(300, 40));
        btnSignup.addActionListener(e -> handleSignup());
        gbc.gridy = 6; gbc.insets = new Insets(20, 50, 10, 50);
        pnlRight.add(btnSignup, gbc);

        JButton btnBack = new JButton("Đã có tài khoản? Đăng nhập");
        btnBack.setContentAreaFilled(false); btnBack.setBorder(null);
        btnBack.addActionListener(e -> { new LoginForm().setVisible(true); this.dispose(); });
        gbc.gridy = 7; pnlRight.add(btnBack, gbc);

        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);
        add(pnlMain);
    }

    private JTextField createStyledField(String placeholder) {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(300, 35));
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #f0f0f0; borderWidth: 0");
        return f;
    }

    private JPasswordField createStyledPassField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.setPreferredSize(new Dimension(300, 35));
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #f0f0f0; borderWidth: 0");
        return f;
    }

    private void handleSignup() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (user.isEmpty() || pass.isEmpty() || !pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Thông tin không hợp lệ hoặc mật khẩu không khớp!");
            return;
        }

        UserDTO dto = new UserDTO();
        dto.setUsername(user);
        dto.setPassword(pass);
        dto.setFullName(txtFullname.getText().trim());
        dto.setEmail(txtEmail.getText().trim());

        if (userClient.register(dto)) {
            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Hãy đăng nhập.");
            new LoginForm().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại (Tài khoản có thể đã tồn tại).");
        }
    }
}