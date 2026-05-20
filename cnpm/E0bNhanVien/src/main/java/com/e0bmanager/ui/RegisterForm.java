package com.e0bmanager.ui;

import com.e0bmanager.client.AuthClient;
import com.e0bmanager.dto.RegisterRequestDTO;
import com.formdev.flatlaf.FlatClientProperties;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class RegisterForm extends JFrame {
    private JTextField txtUser, txtHoTen, txtSdt, txtLuong;
    private JTextField txtEmail;
    private JPasswordField txtPass;
    private JComboBox<String> cbChucVu;
    private JDateChooser dateNgaySinh;
    private JButton btnSubmit;
    private JLabel lblBackToLogin;
    private AuthClient authClient = new AuthClient();

    public RegisterForm() {
        // Cấu hình Frame
        setTitle("E0b Staff - Đăng ký ứng tuyển");
        setSize(900, 700); // Tăng chiều cao để chứa nhiều field hơn
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel chính chia làm 2 phần
        JPanel pnlMain = new JPanel(new GridLayout(1, 2));
        pnlMain.setBackground(Color.WHITE);

        // --- PHẦN BÊN TRÁI: BACKGROUND (Giống LoginForm) ---
        BackgroundPanel pnlLeft = new BackgroundPanel();
        pnlLeft.setLayout(new BorderLayout());
        pnlLeft.setBorder(new EmptyBorder(50, 40, 50, 40));

        JLabel lblWelcome = new JLabel("<html>Join Our<br>Team!</html>");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 45));
        lblWelcome.setForeground(Color.WHITE);
        pnlLeft.add(lblWelcome, BorderLayout.NORTH);

        JLabel lblSlogan = new JLabel("<html>Bắt đầu sự nghiệp của bạn<br>tại E0b Manager System</html>");
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSlogan.setForeground(new Color(255, 255, 255, 200));
        pnlLeft.add(lblSlogan, BorderLayout.CENTER);

        // --- PHẦN BÊN PHẢI: FORM ĐĂNG KÝ ---
        JPanel pnlRight = new JPanel();
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Header
        JLabel lblTitle = new JLabel("Đơn Ứng Tuyển");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Vui lòng điền đầy đủ thông tin bên dưới");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Tạo các Input Field với style FlatLaf ---
        txtUser = createStyledTextField("👤 Tài khoản đăng nhập (*)");
        txtPass = createStyledPasswordField("🔒 Mật khẩu (*)");
        txtHoTen = createStyledTextField("📛 Họ và tên (*)");
        txtSdt = createStyledTextField("📞 Số điện thoại (*)");
        txtLuong = createStyledTextField("💰 Mức lương đề xuất (VNĐ)");

        // Ngày sinh (JDateChooser)
        JPanel pnlDate = new JPanel(new BorderLayout());
        pnlDate.setOpaque(false);
        pnlDate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        dateNgaySinh = new JDateChooser();
        dateNgaySinh.setDateFormatString("dd/MM/yyyy");
        dateNgaySinh.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        pnlDate.add(new JLabel(" Ngày sinh (*)"), BorderLayout.NORTH);
        pnlDate.add(dateNgaySinh, BorderLayout.CENTER);

        // Chức vụ (ComboBox)
        JPanel pnlCombo = new JPanel(new BorderLayout());
        pnlCombo.setOpaque(false);
        pnlCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        String[] chucVuList = {"Phục vụ", "Pha chế"};
        cbChucVu = new JComboBox<>(chucVuList);
        cbChucVu.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        pnlCombo.add(new JLabel(" Chức vụ mong muốn"), BorderLayout.NORTH);
        pnlCombo.add(cbChucVu, BorderLayout.CENTER);

        // Nút bấm
        btnSubmit = new JButton("Gửi yêu cầu đăng ký");
        btnSubmit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnSubmit.setBackground(new Color(46, 204, 113));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSubmit.putClientProperty(FlatClientProperties.STYLE, "arc: 20; borderWidth: 0; focusWidth: 0");
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblBackToLogin = new JLabel("Đã có tài khoản? Đăng nhập ngay");
        lblBackToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBackToLogin.setForeground(new Color(52, 152, 219));
        lblBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBackToLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to Right Panel
        pnlRight.add(lblTitle);
        pnlRight.add(Box.createVerticalStrut(5));
        pnlRight.add(lblSub);
        pnlRight.add(Box.createVerticalStrut(20));
        pnlRight.add(txtUser); pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(txtPass); pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(txtHoTen); pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(txtSdt); pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(pnlDate); pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(pnlCombo); pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(txtLuong); pnlRight.add(Box.createVerticalStrut(25));
        txtEmail = createStyledTextField("📧 Email nhận thông báo (*)");
        pnlRight.add(txtEmail); pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(btnSubmit);
        pnlRight.add(Box.createVerticalStrut(15));
        pnlRight.add(lblBackToLogin);

        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);
        add(pnlMain);

        // Sự kiện
        btnSubmit.addActionListener(e -> handleSubmit());
        lblBackToLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        return tf;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        pf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        pf.putClientProperty(FlatClientProperties.STYLE, "arc: 15; showRevealButton: true");
        return pf;
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    private void handleSubmit() {
        if (txtUser.getText().isEmpty() || new String(txtPass.getPassword()).isEmpty() ||
                txtHoTen.getText().isEmpty() || dateNgaySinh.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đủ các trường có dấu (*)");
            return;

        }
        if (!isValidEmail(txtEmail.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Định dạng Email không hợp lệ! (Ví dụ: abc@gmail.com)");
            txtEmail.requestFocus();
            return;
        }
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang gửi yêu cầu...");

        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername(txtUser.getText().trim());
        req.setPassword(new String(txtPass.getPassword()));
        req.setHoTen(txtHoTen.getText().trim());
        req.setSdt(txtSdt.getText().trim());
        req.setChucVu(cbChucVu.getSelectedItem().toString());
        req.setEmail(txtEmail.getText().trim());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        req.setNgaySinh(sdf.format(dateNgaySinh.getDate()));

        try {
            req.setLuong(txtLuong.getText().isEmpty() ? 0.0 : Double.parseDouble(txtLuong.getText()));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lương phải là một số!");
            btnSubmit.setEnabled(true);
            return;
        }

        new Thread(() -> {
            try {
                if (authClient.register(req)) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đợi quản lý phê duyệt.");
                        new LoginForm().setVisible(true);
                        dispose();
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Gửi yêu cầu đăng ký");
                });
            }
        }).start();
    }

    // Tận dụng lại class vẽ background từ LoginForm
    class BackgroundPanel extends JPanel {
        private Image img;
        public BackgroundPanel() {
            try {
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
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(46, 204, 113), getWidth(), getHeight(), new Color(39, 174, 96)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}