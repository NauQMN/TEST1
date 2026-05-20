package com.e0bmanager.ui;

import com.e0bmanager.client.StaffApiClient;
import com.e0bmanager.dto.AccountDTO;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DoiMatKhauDialog extends JDialog {

    private final AccountDTO account;
    private final StaffApiClient apiClient = new StaffApiClient();

    private JPasswordField txtCu, txtMoi, txtMoiLai;
    private JButton btnLuu;

    private static final Color PURPLE_DARK = new Color(88,  61, 172);
    private static final Color WHITE       = Color.WHITE;
    private static final Color TEXT_DARK   = new Color(22,  27,  46);
    private static final Color TEXT_GRAY   = new Color(120, 130, 150);
    private static final Color RED         = new Color(239, 68,  68);
    private static final Color GREEN       = new Color(16, 185, 129);

    public DoiMatKhauDialog(Frame parent, AccountDTO account) {
        super(parent, "Đổi Mật Khẩu", true);
        this.account = account;
        buildUI();
        setSize(440, 420);
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(WHITE);
        main.setBorder(new EmptyBorder(32, 36, 32, 36));

        // Icon + tiêu đề
        JLabel lblIcon = new JLabel("🔐", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Đổi Mật Khẩu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Nhập mật khẩu hiện tại và mật khẩu mới");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_GRAY);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        // Fields
        txtCu    = createPassField("🔒 Mật khẩu hiện tại");
        txtMoi   = createPassField("🆕 Mật khẩu mới");
        txtMoiLai= createPassField("✅ Nhập lại mật khẩu mới");

        // Nút lưu
        btnLuu = new JButton("Lưu mật khẩu mới");
        btnLuu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLuu.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #583DAC; foreground: #FFFFFF; borderWidth: 0; focusWidth: 0;");
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuu.addActionListener(e -> handleSave());
        btnLuu.setAlignmentX(CENTER_ALIGNMENT);

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnHuy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnHuy.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #F3F4F6; foreground: #6B7280; borderWidth: 0; focusWidth: 0;");
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> dispose());
        btnHuy.setAlignmentX(CENTER_ALIGNMENT);

        main.add(lblIcon);
        main.add(Box.createVerticalStrut(8));
        main.add(lblTitle);
        main.add(Box.createVerticalStrut(4));
        main.add(lblSub);
        main.add(Box.createVerticalStrut(28));
        main.add(txtCu);
        main.add(Box.createVerticalStrut(12));
        main.add(txtMoi);
        main.add(Box.createVerticalStrut(12));
        main.add(txtMoiLai);
        main.add(Box.createVerticalStrut(24));
        main.add(btnLuu);
        main.add(Box.createVerticalStrut(8));
        main.add(btnHuy);

        setContentPane(main);
    }

    private void handleSave() {
        String cu      = new String(txtCu.getPassword());
        String moi     = new String(txtMoi.getPassword());
        String moiLai  = new String(txtMoiLai.getPassword());

        if (cu.isEmpty() || moi.isEmpty() || moiLai.isEmpty()) {
            showMsg("Vui lòng điền đầy đủ tất cả các trường!", RED);
            return;
        }
        if (moi.length() < 6) {
            showMsg("Mật khẩu mới phải có ít nhất 6 ký tự!", RED);
            return;
        }
        if (!moi.equals(moiLai)) {
            showMsg("Mật khẩu mới và nhập lại không khớp!", RED);
            txtMoiLai.requestFocus();
            return;
        }

        btnLuu.setEnabled(false);
        btnLuu.setText("Đang xử lý...");

        new Thread(() -> {
            try {
                // Bước 1: Xác minh mật khẩu cũ
                boolean valid = apiClient.xacMinhMatKhau(account.getUsername(), cu);
                if (!valid) {
                    SwingUtilities.invokeLater(() -> {
                        showMsg("Mật khẩu hiện tại không đúng!", RED);
                        resetBtn();
                    });
                    return;
                }
                // Bước 2: Đổi mật khẩu
                apiClient.doiMatKhau(Long.valueOf(account.getId()), moi);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "✅  Đổi mật khẩu thành công!\nVui lòng đăng nhập lại.",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    showMsg("Lỗi: " + ex.getMessage(), RED);
                    resetBtn();
                });
            }
        }).start();
    }

    private void showMsg(String msg, Color color) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo",
                color.equals(RED) ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetBtn() {
        btnLuu.setEnabled(true);
        btnLuu.setText("Lưu mật khẩu mới");
    }

    private JPasswordField createPassField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        pf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        pf.putClientProperty(FlatClientProperties.STYLE, "arc: 14; showRevealButton: true");
        pf.setAlignmentX(CENTER_ALIGNMENT);
        return pf;
    }
}