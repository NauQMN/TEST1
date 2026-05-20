package e0bmanager.ui;

import e0bmanager.utils.NotificationManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Thanh trên cùng cố định trong MainForm — chứa tiêu đề trang hiện tại và chuông thông báo.
 * Chỉ hiển thị khi không ở HomePanel.
 */
public class TopBar extends JPanel {

    private final JLabel lblPageTitle;
    private final NotificationBell bell;

    public TopBar(NotificationManager notificationManager) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(0, 30, 0, 20)
        ));
        setPreferredSize(new Dimension(0, 52));

        // Tiêu đề trang hiện tại (bên trái)
        lblPageTitle = new JLabel("");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPageTitle.setForeground(new Color(100, 116, 139));
        add(lblPageTitle, BorderLayout.WEST);

        // Chuông + tên app (bên phải)
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        pnlRight.setOpaque(false);

        bell = new NotificationBell(notificationManager);

        JLabel lblApp = new JLabel("E0B Manager");
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblApp.setForeground(new Color(148, 163, 184));

        pnlRight.add(lblApp);
        pnlRight.add(new JSeparator(SwingConstants.VERTICAL) {{
            setPreferredSize(new Dimension(1, 22));
            setForeground(new Color(226, 232, 240));
        }});
        pnlRight.add(bell);

        add(pnlRight, BorderLayout.EAST);
    }

    /** Cập nhật tiêu đề trang hiện tại */
    public void setPageTitle(String title) {
        lblPageTitle.setText(title);
    }

    /** Cập nhật badge sau khi refresh thông báo */
    public void updateBell() {
        bell.updateBadge();
    }
}