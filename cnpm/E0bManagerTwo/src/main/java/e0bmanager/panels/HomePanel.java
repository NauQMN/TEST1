package e0bmanager.panels;

import e0bmanager.ui.CircleAvatar;
import e0bmanager.utils.SessionManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.ByteArrayInputStream;
import java.util.Base64;

public class HomePanel extends JPanel {

    private int currentBannerIndex = 0;
    private final String[] bannerPaths = {
            "/e0bmang-bannerimg.png", "/banner2.png", "/banner3.png", "/banner4.png"
    };
    private JLabel lblBanner;
    private JLabel[] dots;

    private static final Color COLOR_BG     = new Color(245, 246, 250);
    private static final Color COLOR_HEADER = new Color(30, 35, 50);
    private static final Color COLOR_ACCENT = new Color(241, 196, 15);

    private static final Color[][] CARD_COLORS = {
            {new Color(99, 102, 241),  new Color(67, 56, 202)},
            {new Color(16, 185, 129),  new Color(5, 150, 105)},
            {new Color(245, 158, 11),  new Color(217, 119, 6)},
            {new Color(239, 68, 68),   new Color(185, 28, 28)},
            {new Color(59, 130, 246),  new Color(37, 99, 235)},
            {new Color(168, 85, 247),  new Color(126, 34, 206)},
    };

    public HomePanel(String loggedinUser, ActionListener navigationListener) {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        add(buildHeader(loggedinUser, navigationListener), BorderLayout.NORTH);
        add(buildCenter(navigationListener), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        updateBanner();
        new Timer(5000, e -> {
            currentBannerIndex = (currentBannerIndex + 1) % bannerPaths.length;
            updateBanner();
        }).start();
    }

    // ===== HEADER =====
    private JPanel buildHeader(String loggedinUser, ActionListener navigationListener) {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, COLOR_HEADER, getWidth(), 0, new Color(45, 52, 71)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(100, 64));
        header.setBorder(new EmptyBorder(0, 25, 0, 20));

        JPanel pnlLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlLogo.setOpaque(false);
        JLabel dot = new JLabel("● ");
        dot.setForeground(COLOR_ACCENT);
        dot.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel title = new JLabel("E0B MANAGER");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlLogo.add(dot); pnlLogo.add(title);
        header.add(pnlLogo, BorderLayout.WEST);

        String name = (SessionManager.currentUser != null && SessionManager.currentUser.getFullName() != null)
                ? SessionManager.currentUser.getFullName() : (loggedinUser != null ? loggedinUser : "Admin");

        JPanel pnlProfile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 13));
        pnlProfile.setOpaque(false);
        pnlProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblGreet = new JLabel("Xin chào,");
        lblGreet.setForeground(new Color(180, 185, 200));
        lblGreet.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblUser = new JLabel(name + "  ▾");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));

        CircleAvatar avatar = new CircleAvatar();
        avatar.setPreferredSize(new Dimension(38, 38));
        if (SessionManager.currentUser != null && SessionManager.currentUser.getAvatar() != null)
            avatar.setIcon(decodeBase64ToIcon(SessionManager.currentUser.getAvatar(), 38, 38));

        pnlProfile.add(lblGreet); pnlProfile.add(lblUser); pnlProfile.add(avatar);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem iInfo = new JMenuItem("👤  Thông tin cá nhân");
        iInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        iInfo.addActionListener(e -> navigationListener.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Cá nhân")));
        JMenuItem iLogout = new JMenuItem("🚪  Đăng xuất");
        iLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        iLogout.setForeground(new Color(231, 76, 60));
        iLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                navigationListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Đăng xuất"));
        });
        menu.add(iInfo); menu.addSeparator(); menu.add(iLogout);

        pnlProfile.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                menu.show(pnlProfile, pnlProfile.getWidth() - menu.getPreferredSize().width, pnlProfile.getHeight());
            }
            public void mouseEntered(MouseEvent e) { lblUser.setForeground(COLOR_ACCENT); }
            public void mouseExited(MouseEvent e)  { lblUser.setForeground(Color.WHITE); }
        });
        header.add(pnlProfile, BorderLayout.EAST);
        return header;
    }

    // ===== CENTER =====
    private JPanel buildCenter(ActionListener navigationListener) {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        // Banner bo tròn
        JPanel bannerWrapper = new JPanel(new BorderLayout());
        bannerWrapper.setOpaque(false);
        bannerWrapper.setBorder(new EmptyBorder(18, 25, 0, 25));

        lblBanner = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                if (getIcon() == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Đổ bóng nhẹ phía dưới
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 4, 26, 26);
                // Clip bo tròn rồi vẽ ảnh
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() - 4, 24, 24));
                g2.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight() - 4, this);
                g2.dispose();
            }
        };
        lblBanner.setPreferredSize(new Dimension(1100, 300));

        // Dots
        JPanel pnlDots = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        pnlDots.setOpaque(false);
        dots = new JLabel[bannerPaths.length];
        for (int i = 0; i < bannerPaths.length; i++) {
            final int idx = i;
            dots[i] = new JLabel("⬤");
            dots[i].setFont(new Font("Arial", Font.PLAIN, 10));
            dots[i].setForeground(new Color(200, 205, 215));
            dots[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            dots[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { currentBannerIndex = idx; updateBanner(); }
            });
            pnlDots.add(dots[i]);
        }
        bannerWrapper.add(lblBanner, BorderLayout.CENTER);
        bannerWrapper.add(pnlDots, BorderLayout.SOUTH);
        center.add(bannerWrapper, BorderLayout.NORTH);

        // Menu grid
        JPanel menuWrapper = new JPanel(new BorderLayout());
        menuWrapper.setOpaque(false);
        menuWrapper.setBorder(new EmptyBorder(16, 30, 10, 30));

        JLabel lblSub = new JLabel("Chọn chức năng để bắt đầu");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(new Color(100, 116, 139));
        lblSub.setBorder(new EmptyBorder(0, 0, 12, 0));
        menuWrapper.add(lblSub, BorderLayout.NORTH);

        String[] labels = {"Tổng quan", "Nhân viên", "Lịch làm việc", "Giao việc", "Tính lương", "Đánh giá"};
        String[] icons  = {"/tongquan-img.png", "/nhanvien-img.png", "/lichlamviec-img.png",
                "/giaoviec-img.png", "/tinhluong-img.png", "/danhgia-img.png"};
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setOpaque(false);
        for (int i = 0; i < labels.length; i++)
            grid.add(createMenuCard(labels[i], icons[i], CARD_COLORS[i], navigationListener));

        menuWrapper.add(grid, BorderLayout.CENTER);
        center.add(menuWrapper, BorderLayout.CENTER);
        return center;
    }

    // ===== FOOTER =====
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 8));
        footer.setOpaque(false);
        JLabel lbl = new JLabel("Build: 2026.03.03 | Version 1.1.0 (Stable)");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(170, 178, 195));
        footer.add(lbl);
        return footer;
    }

    // ===== MENU CARD =====
    private JPanel createMenuCard(String text, String iconPath, Color[] gc, ActionListener listener) {
        JPanel card = new JPanel(new BorderLayout()) {
            boolean hovered = false;
            {
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                    public void mouseClicked(MouseEvent e) {
                        listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, text));
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Bóng đổ
                g2.setColor(new Color(0, 0, 0, hovered ? 28 : 15));
                g2.fillRoundRect(3, 5, getWidth() - 6, getHeight() - 4, 22, 22);
                // Gradient
                g2.setPaint(new GradientPaint(0, 0, gc[0], getWidth(), getHeight(), gc[1]));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 5, 20, 20);
                // Highlight hover
                if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 35));
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 5, 20, 20);
                }
                g2.dispose();
            }
        };
        card.setBorder(new EmptyBorder(18, 16, 22, 16));

        JLabel lblIcon = new JLabel();
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            Image raw = new ImageIcon(getClass().getResource(iconPath))
                    .getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
            lblIcon.setIcon(new ImageIcon(raw));
        } catch (Exception ex) {
            lblIcon.setText("★");
            lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 26));
            lblIcon.setForeground(Color.WHITE);
        }

        JLabel lblText = new JLabel(text, SwingConstants.CENTER);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblText.setForeground(Color.WHITE);
        lblText.setBorder(new EmptyBorder(8, 0, 0, 0));

        card.add(lblIcon, BorderLayout.CENTER);
        card.add(lblText, BorderLayout.SOUTH);
        return card;
    }

    // ===== HELPERS =====
    private void updateBanner() {
        try {
            lblBanner.setIcon(new ImageIcon(getClass().getResource(bannerPaths[currentBannerIndex])));
            for (int i = 0; i < dots.length; i++) {
                dots[i].setForeground(i == currentBannerIndex ? COLOR_ACCENT : new Color(200, 205, 215));
                dots[i].setFont(new Font("Arial", Font.PLAIN, i == currentBannerIndex ? 13 : 10));
            }
            lblBanner.repaint();
        } catch (Exception e) {
            lblBanner.setText("Banner không tìm thấy");
        }
    }

    private ImageIcon decodeBase64ToIcon(String base64, int w, int h) {
        try {
            byte[] b = Base64.getDecoder().decode(base64);
            return new ImageIcon(ImageIO.read(new ByteArrayInputStream(b)).getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return null; }
    }
}