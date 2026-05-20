package e0bmanager.panels;

import e0bmanager.client.UserClient;
import e0bmanager.dto.UserDTO;
import e0bmanager.ui.BottomTaskbar;
import e0bmanager.ui.CircleAvatar;
import e0bmanager.utils.SessionManager;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import javax.imageio.ImageIO;

public class CaNhanPanel extends JPanel {
    private UserClient userClient = new UserClient();
    private UserDTO currentUser;
    private ActionListener navigationListener;

    // --- Components hiển thị ---
    private JLabel lblIdValue, lblUserValue, lblFullNameValue, lblPhoneValue, lblCCCDValue, lblEmailValue;
    private CircleAvatar lblAvatar;
    private JPanel pnlCards;
    private CardLayout cardLayout;

    // --- Form Bảo mật ---
    private JPasswordField txtOldPass, txtNewPass, txtConfirmPass;
    private String currentAvatarBase64;

    private final Color COLOR_BG_SIDEBAR = new Color(236, 240, 241);
    private final String CARD_INFO = "INFO";
    private final String CARD_SECURITY = "SECURITY";

    public CaNhanPanel(ActionListener navigationListener) {
        this.navigationListener = navigationListener;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- TIÊU ĐỀ ---
        JLabel lblTitle = new JLabel("HỒ SƠ CÁ NHÂN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(44, 62, 80));
        add(lblTitle, BorderLayout.NORTH);

        JPanel pnlMainContent = new JPanel(new BorderLayout(30, 0));
        pnlMainContent.setOpaque(false);

        // --- 1. CỘT TRÁI (SIDEBAR) ---
        pnlMainContent.add(createSidebar(), BorderLayout.WEST);

        // --- 2. CỘT PHẢI (NỘI DUNG ĐỘNG) ---
        cardLayout = new CardLayout();
        pnlCards = new JPanel(cardLayout);
        pnlCards.setOpaque(false);

        pnlCards.add(createInfoPanel(), CARD_INFO);
        pnlCards.add(createSecurityPanel(), CARD_SECURITY);

        pnlMainContent.add(pnlCards, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(pnlMainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(245, 246, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel pnlLeft = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BG_SIDEBAR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        pnlLeft.setOpaque(false);
        pnlLeft.setPreferredSize(new Dimension(240, 550));
        pnlLeft.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        // AVATAR TRÒN
        lblAvatar = new CircleAvatar();
        lblAvatar.setPreferredSize(new Dimension(180, 180));
        lblAvatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAvatar.setToolTipText("Click để phóng to ảnh");
        lblAvatar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { showLargeAvatar(); }
        });

        JButton btnView = createStyledButton("Thông tin chung");
        JButton btnSecurity = createStyledButton("Bảo mật & MK");
        JButton btnLogout = createStyledButton("Đăng xuất");
        btnLogout.setForeground(new Color(192, 57, 43)); // Màu đỏ cho nút đăng xuất

        // Sự kiện chuyển đổi Card
        btnView.addActionListener(e -> cardLayout.show(pnlCards, CARD_INFO));
        btnSecurity.addActionListener(e -> cardLayout.show(pnlCards, CARD_SECURITY));

        // Sự kiện Đăng xuất
        btnLogout.addActionListener(e -> handleLogout());

        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(lblAvatar);
        pnlLeft.add(Box.createVerticalStrut(20));
        pnlLeft.add(btnView);
        pnlLeft.add(new JSeparator(SwingConstants.HORIZONTAL));
        pnlLeft.add(btnSecurity);
        pnlLeft.add(new JSeparator(SwingConstants.HORIZONTAL));
        pnlLeft.add(btnLogout);

        return pnlLeft;
    }

    private JPanel createInfoPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 20));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(30, 40, 30, 40));
        pnl.putClientProperty(FlatClientProperties.STYLE, "arc: 30");

        JLabel title = new JLabel("Chi tiết tài khoản");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(41, 128, 185));
        pnl.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 25));
        form.setOpaque(false);
        form.add(createInfoLabel("ID người dùng:")); lblIdValue = createValueLabel("---"); form.add(lblIdValue);
        form.add(createInfoLabel("Tên đăng nhập:")); lblUserValue = createValueLabel("---"); form.add(lblUserValue);
        form.add(createInfoLabel("Họ và tên:")); lblFullNameValue = createValueLabel("---"); form.add(lblFullNameValue);
        form.add(createInfoLabel("Số điện thoại:")); lblPhoneValue = createValueLabel("---"); form.add(lblPhoneValue);
        form.add(createInfoLabel("Số CCCD:")); lblCCCDValue = createValueLabel("---"); form.add(lblCCCDValue);
        form.add(createInfoLabel("Email:")); lblEmailValue = createValueLabel("---"); form.add(lblEmailValue);

        pnl.add(form, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createSecurityPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 20));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(30, 40, 30, 40));
        pnl.putClientProperty(FlatClientProperties.STYLE, "arc: 30");

        JLabel title = new JLabel("Cập nhật bảo mật & Ảnh");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(231, 76, 60));
        pnl.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 1, 10, 15));
        form.setOpaque(false);

        JButton btnUpload = new JButton("📁 Chọn ảnh đại diện mới");
        btnUpload.addActionListener(e -> chooseNewAvatar());

        txtOldPass = new JPasswordField();
        txtNewPass = new JPasswordField();
        txtConfirmPass = new JPasswordField();

        form.add(btnUpload);
        form.add(createFieldPanel("Mật khẩu cũ:", txtOldPass));
        form.add(createFieldPanel("Mật khẩu mới:", txtNewPass));
        form.add(createFieldPanel("Xác nhận mật khẩu:", txtConfirmPass));

        JButton btnUpdate = new JButton("Lưu thay đổi");
        btnUpdate.putClientProperty(FlatClientProperties.STYLE, "background: #27ae60; foreground: #fff; arc: 10");
        btnUpdate.addActionListener(e -> handleUpdateSecurity());

        pnl.add(form, BorderLayout.CENTER);
        pnl.add(btnUpdate, BorderLayout.SOUTH);
        return pnl;
    }

    // =========================================================
    // LOGIC XỬ LÝ
    // =========================================================

    private void loadData() {
        if (SessionManager.currentUser == null) return;
        currentUser = userClient.getUserProfile(Math.toIntExact(SessionManager.currentUser.getId()));
        if (currentUser != null) {
            lblIdValue.setText("UID-" + currentUser.getId());
            lblUserValue.setText(currentUser.getUsername());
            lblFullNameValue.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "Chưa đặt tên");
            lblPhoneValue.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "N/A");
            lblCCCDValue.setText(currentUser.getCccd() != null ? currentUser.getCccd() : "N/A");
            lblEmailValue.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A");
            if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                currentAvatarBase64 = currentUser.getAvatar();
                lblAvatar.setIcon(decodeBase64ToIcon(currentAvatarBase64, 180, 180));
            }
        }
    }

    private void chooseNewAvatar() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                byte[] bytes = Files.readAllBytes(file.toPath());
                currentAvatarBase64 = Base64.getEncoder().encodeToString(bytes);
                lblAvatar.setIcon(decodeBase64ToIcon(currentAvatarBase64, 180, 180));
                JOptionPane.showMessageDialog(this, "Đã chọn ảnh! Nhấn Lưu để hoàn tất.");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void handleUpdateSecurity() {
        String newPass = new String(txtNewPass.getPassword());
        String confirm = new String(txtConfirmPass.getPassword());

        if (!newPass.isEmpty() && !newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
            return;
        }

        UserDTO dto = new UserDTO();
        dto.setId(currentUser.getId());
        dto.setAvatar(currentAvatarBase64);
        if(!newPass.isEmpty()) dto.setPassword(newPass);

        if (userClient.updateUserProfile(dto)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData(); // Tải lại ảnh mới
        }
    }

    private void handleLogout() {
        int opt = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opt == JOptionPane.YES_OPTION) {
            // 1. Xóa dữ liệu phiên đăng nhập
            e0bmanager.utils.SessionManager.currentUser = null;

            // 2. Tìm và đóng cửa sổ chính (MainFrame/MainForm)
            Window currentWindow = SwingUtilities.getWindowAncestor(this);
            if (currentWindow != null) {
                currentWindow.dispose();
            }

            // 3. Mở lại màn hình Đăng nhập
            // Thay 'LoginForm' bằng tên class thực tế của bạn
            SwingUtilities.invokeLater(() -> {
                new e0bmanager.ui.LoginForm().setVisible(true);
            });

            System.out.println("Đã đăng xuất và quay về màn hình Login.");
        }
    }

    private void showLargeAvatar() {
        if (currentAvatarBase64 == null) return;
        JDialog dial = new JDialog((Frame)null, "Ảnh đại diện", true);
        dial.add(new JLabel(decodeBase64ToIcon(currentAvatarBase64, 450, 450)));
        dial.pack();
        dial.setLocationRelativeTo(this);
        dial.setVisible(true);
    }

    // --- Helpers ---
    private ImageIcon decodeBase64ToIcon(String base64, int w, int h) {
        try {
            byte[] b = Base64.getDecoder().decode(base64);
            return new ImageIcon(ImageIO.read(new ByteArrayInputStream(b)).getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return null; }
    }

    private JPanel createFieldPanel(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(44, 62, 80));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 45));
        return btn;
    }

    private JLabel createInfoLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        l.setForeground(new Color(149, 165, 166)); return l;
    }

    private JLabel createValueLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(new Color(44, 62, 80)); return l;
    }
}