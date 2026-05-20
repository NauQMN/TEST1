package e0bmanager.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BottomTaskbar extends JPanel {

    public BottomTaskbar(ActionListener navigationListener) {
        setOpaque(false);

        // TĂNG khoảng cách ngang (35) để thanh trải dài ra
        // GIẢM khoảng cách dọc (8) để thanh mỏng lại
        setLayout(new FlowLayout(FlowLayout.CENTER, 35, 8));

        // Giảm chiều cao tổng thể xuống 70 (thay vì 85 như trước)
        setPreferredSize(new Dimension(100, 70));

        String[] menus = {"Tổng quan", "Nhân viên", "Lịch làm việc", "Giao việc", "Tính lương", "Đánh giá","Cá nhân"};
        String[] icons = {
                "/tongquan-icon.png", "/nhanvien-icon.png",
                "/lichlamviec-icon.png", "/giaoviec-icon.png",
                "/tinhluong-icon.png", "/danhgia-icon.png",
                "/canhan-icon.png"
        };

        for (int i = 0; i < menus.length; i++) {
            add(createTaskbarButton(menus[i], icons[i], navigationListener));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(45, 52, 71));

        // Giảm marginX xuống 15 để thanh kéo dài ra sát mép hơn
        // Giảm marginY xuống 2 để thanh ép mỏng lại
        int marginX = 15;
        int marginY = 2;
        int width = getWidth() - (marginX * 2);
        int height = getHeight() - (marginY * 2);

        // Bo góc (arc = 35) cho phù hợp với chiều cao mới
        g2.fillRoundRect(marginX, marginY, width, height, 35, 35);

        g2.dispose();
    }

    private JButton createTaskbarButton(String text, String iconPath, ActionListener listener) {
        JButton btn = new JButton(text);
        // Hạ cỡ chữ xuống 11 để thanh mảnh và tinh tế hơn
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(new Color(220, 224, 232));
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        java.net.URL imgURL = getClass().getResource(iconPath);

        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            // Thu nhỏ icon (24x24) để vừa vặn với thanh taskbar đã mỏng đi
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        } else {
            btn.setText("★ " + text);
        }

        btn.setActionCommand(text);
        btn.addActionListener(listener);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(241, 196, 15));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(220, 224, 232));
            }
        });

        return btn;
    }
}