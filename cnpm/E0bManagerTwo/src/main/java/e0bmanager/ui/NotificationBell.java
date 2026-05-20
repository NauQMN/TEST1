package e0bmanager.ui;

import com.formdev.flatlaf.FlatClientProperties;
import e0bmanager.utils.NotificationManager;
import e0bmanager.utils.NotificationManager.Notification;
import e0bmanager.utils.NotificationManager.Priority;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Nút chuông thông báo — hiển thị badge số lượng, click mở popup danh sách.
 */
public class NotificationBell extends JPanel {

    private final NotificationManager manager;
    private JLabel lblBell;
    private JLabel lblBadge;
    private JPopupMenu popup;

    public NotificationBell(NotificationManager manager) {
        this.manager = manager;
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        initBell();
    }

    private void initBell() {
        JPanel pnlWrapper = new JPanel(null); // absolute layout cho badge chồng lên chuông
        pnlWrapper.setOpaque(false);
        pnlWrapper.setPreferredSize(new Dimension(46, 42));

        // Chuông
        lblBell = new JLabel("🔔");
        lblBell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblBell.setBounds(4, 6, 32, 32);
        pnlWrapper.add(lblBell);

        // Badge số lượng (chồng góc trên phải)
        lblBadge = new JLabel("0");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setHorizontalAlignment(SwingConstants.CENTER);
        lblBadge.setBounds(26, 4, 18, 14);
        lblBadge.setVisible(false);
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(239, 68, 68));
        lblBadge.putClientProperty(FlatClientProperties.STYLE, "arc: 99;");
        pnlWrapper.add(lblBadge);

        add(pnlWrapper);

        // Click → mở popup
        pnlWrapper.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { togglePopup(pnlWrapper); }
            @Override public void mouseEntered(MouseEvent e) { lblBell.setText("🔕"); }
            @Override public void mouseExited(MouseEvent e)  { lblBell.setText("🔔"); }
        });
    }

    /** Cập nhật badge sau khi refresh thông báo */
    public void updateBadge() {
        int count = manager.getUnreadCount();
        if (count > 0) {
            lblBadge.setText(count > 9 ? "9+" : String.valueOf(count));
            lblBadge.setVisible(true);
        } else {
            lblBadge.setVisible(false);
        }
        repaint();
    }

    private void togglePopup(JPanel anchor) {
        if (popup != null && popup.isVisible()) {
            popup.setVisible(false);
            return;
        }
        manager.markAllRead();
        updateBadge();
        popup = buildPopup();
        popup.show(anchor, anchor.getWidth() - popup.getPreferredSize().width, anchor.getHeight() + 4);
    }

    private JPopupMenu buildPopup() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(Color.WHITE);
        menu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(6, 0, 8, 0)
        ));

        List<Notification> list = manager.getAll();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(6, 16, 10, 16));
        JLabel lblHeader = new JLabel("Thông báo");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setForeground(new Color(30, 41, 59));
        header.add(lblHeader, BorderLayout.WEST);
        if (!list.isEmpty()) {
            JLabel lblCount = new JLabel(list.size() + " mục");
            lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblCount.setForeground(new Color(148, 163, 184));
            header.add(lblCount, BorderLayout.EAST);
        }
        menu.add(header);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(241, 245, 249));
        menu.add(sep);

        if (list.isEmpty()) {
            JLabel lblEmpty = new JLabel("   Không có thông báo nào   ");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblEmpty.setForeground(new Color(148, 163, 184));
            lblEmpty.setBorder(new EmptyBorder(16, 16, 16, 16));
            menu.add(lblEmpty);
        } else {
            for (Notification n : list) {
                menu.add(createNotificationItem(n));
            }
        }

        // Đặt chiều rộng popup cố định
        menu.setPreferredSize(new Dimension(370, menu.getPreferredSize().height));
        return menu;
    }

    private JPanel createNotificationItem(Notification n) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(10, 16, 10, 16));
        item.setMaximumSize(new Dimension(370, 80));

        // Màu dot theo priority
        Color dotColor = switch (n.priority) {
            case HIGH   -> new Color(239, 68, 68);
            case MEDIUM -> new Color(245, 158, 11);
            case LOW    -> new Color(148, 163, 184);
        };

        // Icon + dot
        JPanel pnlIcon = new JPanel(new BorderLayout());
        pnlIcon.setOpaque(false);
        pnlIcon.setPreferredSize(new Dimension(38, 0));

        JLabel lblIcon = new JLabel(n.icon) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(dotColor.getRed(), dotColor.getGreen(), dotColor.getBlue(), 25));
                g2.fillOval(0, 0, getWidth() - 2, getHeight() - 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        pnlIcon.add(lblIcon, BorderLayout.CENTER);

        // Nội dung
        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlText.setOpaque(false);

        JLabel lblTitle = new JLabel(n.title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(30, 41, 59));

        // Wrap message text
        JLabel lblMsg = new JLabel("<html><body style='width:240px'>" + n.message + "</body></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMsg.setForeground(new Color(100, 116, 139));

        pnlText.add(lblTitle);
        pnlText.add(lblMsg);

        // Dot màu priority
        JPanel pnlDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(0, 6, 8, 8);
                g2.dispose();
            }
        };
        pnlDot.setOpaque(false);
        pnlDot.setPreferredSize(new Dimension(12, 0));

        item.add(pnlDot,   BorderLayout.WEST);
        item.add(pnlIcon,  BorderLayout.CENTER);
        item.add(pnlText,  BorderLayout.EAST);

        // Hover highlight
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { item.setBackground(new Color(248, 250, 252)); }
            public void mouseExited(MouseEvent e)  { item.setBackground(Color.WHITE); }
        });

        // Đảm bảo layout đúng: dot trái + icon tengah + text phải
        item.removeAll();
        item.setLayout(new BorderLayout(10, 0));
        item.add(pnlIcon,  BorderLayout.WEST);
        item.add(pnlText,  BorderLayout.CENTER);
        item.add(pnlDot,   BorderLayout.EAST);

        return item;
    }
}
