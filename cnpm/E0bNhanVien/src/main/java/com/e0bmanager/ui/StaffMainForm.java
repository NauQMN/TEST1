package com.e0bmanager.ui;

import com.e0bmanager.dto.AccountDTO;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;

public class StaffMainForm extends JFrame {

    private final AccountDTO account;
    private CardLayout cardLayout;
    private JPanel pnlContent;

    private JButton btnNavHome, btnNavNotif, btnNavTask, btnNavProfile;

    private static final Color BG           = new Color(245, 246, 250);
    private static final Color PURPLE_DARK  = new Color(88,  61, 172);
    private static final Color PURPLE_LIGHT = new Color(130, 90, 230);
    private static final Color WHITE        = Color.WHITE;
    private static final Color TEXT_DARK    = new Color(22,  27,  46);
    private static final Color TEXT_GRAY    = new Color(120, 130, 150);

    public StaffMainForm(AccountDTO account) {
        this.account = account;
        setTitle("E0b Staff - " + (account.getFullname() != null ? account.getFullname() : account.getUsername()));
        setSize(1300, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(BG);

        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        pnlContent.setBackground(BG);

        pnlContent.add(buildHomePage(),              "HOME");
        pnlContent.add(buildNotifPage(),             "NOTIF");
        pnlContent.add(buildProfilePage(),           "PROFILE");
        pnlContent.add(new LichLamViecPanel(account),"LICH");
        pnlContent.add(new LuongPanel(account),      "LUONG");
        pnlContent.add(new CongViecPanel(account),   "CONGVIEC");

        add(buildTopHeader(),  BorderLayout.NORTH);
        add(pnlContent,        BorderLayout.CENTER);
        add(buildBottomNav(),  BorderLayout.SOUTH);

        cardLayout.show(pnlContent, "HOME");
        setNavActive(btnNavHome);
    }

    // ══════════════════════════════════════════════════════
    //  TOP HEADER — cố định trên cùng
    // ══════════════════════════════════════════════════════
    private JPanel buildTopHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, PURPLE_DARK, getWidth(), 0, PURPLE_LIGHT));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(new EmptyBorder(0, 30, 0, 24));

        // Trái: logo + lời chào
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pnlLeft.setOpaque(false);

        JLabel lblLogo = new JLabel("● E0B STAFF");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(WHITE);

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 22));
        sep.setForeground(new Color(255, 255, 255, 60));

        JLabel lblGreet = new JLabel(getGreeting() + "  " +
                (account.getFullname() != null ? account.getFullname() : account.getUsername()));
        lblGreet.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblGreet.setForeground(new Color(220, 210, 255));

        pnlLeft.add(lblLogo);
        pnlLeft.add(sep);
        pnlLeft.add(lblGreet);

        // Phải: avatar + role
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        pnlRight.setOpaque(false);

        JLabel lblRole = new JLabel(account.getRole() != null ? account.getRole() : "Nhân viên");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRole.setForeground(new Color(200, 190, 255));

        JLabel lblAvatar = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(160, 130, 255));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String init = account.getFullname() != null && !account.getFullname().isEmpty()
                        ? String.valueOf(account.getFullname().charAt(0)).toUpperCase() : "U";
                g2.drawString(init,
                        (getWidth() - fm.stringWidth(init)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        lblAvatar.setPreferredSize(new Dimension(38, 38));

        pnlRight.add(lblRole);
        pnlRight.add(lblAvatar);

        header.add(pnlLeft,  BorderLayout.WEST);
        header.add(pnlRight, BorderLayout.EAST);
        return header;
    }

    // ══════════════════════════════════════════════════════
    //  TRANG CHỦ — layout 2 cột
    // ══════════════════════════════════════════════════════
    private JPanel buildHomePage() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 30, 28, 30));

        // ── NORTH: Banner toàn chiều rộng ────────────────────
        JPanel pnlBanner = new JPanel(new BorderLayout());
        pnlBanner.setOpaque(false);
        pnlBanner.setPreferredSize(new Dimension(0, 210));
        pnlBanner.add(buildBanner(), BorderLayout.CENTER);

        // ── CENTER: Chấm công + 4 nút xếp dọc ───────────────
        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
        pnlCenter.setOpaque(false);

        // Chấm công — chiều cao cố định
        JPanel pnlChamCong = new JPanel(new BorderLayout());
        pnlChamCong.setOpaque(false);
        pnlChamCong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        pnlChamCong.setBorder(new EmptyBorder(20, 0, 20, 0));
        pnlChamCong.add(buildChamCongCard(), BorderLayout.CENTER);

        // 4 nút chức năng — 1 hàng ngang
        JPanel pnlGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        pnlGrid.setOpaque(false);
        pnlGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        JPanel cardLich     = createFuncCard("📅", "Lịch làm việc",
                "Xem ca làm của bạn",
                new Color(230, 242, 255), new Color(59, 130, 246));
        JPanel cardLuong    = createFuncCard("💵", "Tính lương",
                "Xem lương tháng này",
                new Color(230, 255, 242), new Color(16, 185, 129));
        JPanel cardBangTin  = createFuncCard("📰", "Bảng tin",
                "Tin tức từ công ty",
                new Color(255, 243, 230), new Color(245, 158, 11));
        JPanel cardNghi     = createFuncCard("🏖️", "Đăng ký nghỉ",
                "Xin nghỉ phép",
                new Color(243, 232, 255), new Color(168, 85, 247));

        // Gắn sự kiện điều hướng
        cardLich.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardLich.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(pnlContent, "LICH");
                setNavActive(btnNavHome);
            }
        });
        cardLuong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardLuong.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(pnlContent, "LUONG");
                setNavActive(btnNavHome);
            }
        });
        cardBangTin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardBangTin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(pnlContent, "NOTIF");
                setNavActive(btnNavNotif);
            }
        });
        cardNghi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardNghi.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(StaffMainForm.this,
                        "⏳  Tính năng đăng ký nghỉ phép đang phát triển.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        pnlGrid.add(cardLich);
        pnlGrid.add(cardLuong);
        pnlGrid.add(cardBangTin);
        pnlGrid.add(cardNghi);

        pnlCenter.add(pnlChamCong);
        pnlCenter.add(pnlGrid);

        page.add(pnlBanner, BorderLayout.NORTH);
        page.add(pnlCenter, BorderLayout.CENTER);
        return page;
    }

    /** Banner — placeholder chỗ để ảnh sau này */
    private JPanel buildBanner() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Đổ bóng nhẹ
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(4, 6, getWidth()-8, getHeight()-4, 26, 26);
                // Gradient tím placeholder (sẽ thay bằng ảnh thật sau)
                g2.setPaint(new GradientPaint(0, 0, new Color(100, 70, 200),
                        getWidth(), getHeight(), new Color(55, 35, 140)));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-4, 24, 24);
                // Watermark chờ ảnh
                g2.setColor(new Color(255, 255, 255, 30));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                String hint = "[ Banner — sẽ thêm ảnh sau ]";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(hint,
                        (getWidth() - fm.stringWidth(hint)) / 2,
                        getHeight() / 2 + fm.getAscent() / 2);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        return banner;
    }

    /** Nút Chấm công — chiếm toàn bộ chiều rộng, nổi bật */
    private JPanel buildChamCongCard() {
        JPanel card = new JPanel(new BorderLayout(18, 0)) {
            boolean hovered = false;
            boolean pressed = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); setCursor(new Cursor(Cursor.HAND_CURSOR)); }
                    public void mouseExited(MouseEvent e)  { hovered = false; pressed = false; repaint(); }
                    public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
                    public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
                    public void mouseClicked(MouseEvent e) {
                        ChamCongDialog dlg = new ChamCongDialog(
                                StaffMainForm.this,
                                account,
                                timestamp -> {
                                    JOptionPane.showMessageDialog(
                                            StaffMainForm.this,
                                            "✅ Chấm công thành công!\nThời gian: " + timestamp,
                                            "Chấm công", JOptionPane.INFORMATION_MESSAGE);
                                });
                        // Gọi startCheckin() TRƯỚC setVisible()
                        // vì setVisible(true) trên modal dialog block EDT
                        // → startCheckin chạy ngay, camera mở rồi mới hiện dialog
                        dlg.startCheckin();
                        dlg.setVisible(true);
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Bóng
                g2.setColor(new Color(0, 0, 0, pressed ? 8 : (hovered ? 22 : 14)));
                g2.fillRoundRect(3, pressed ? 3 : 5, getWidth()-6, getHeight()-4, 22, 22);
                // Nền trắng
                g2.setColor(pressed ? new Color(245, 245, 255) : WHITE);
                g2.fillRoundRect(0, pressed ? 2 : 0, getWidth()-1, getHeight()-5, 20, 20);
                // Viền tím nhạt khi hover
                if (hovered) {
                    g2.setColor(new Color(PURPLE_DARK.getRed(), PURPLE_DARK.getGreen(), PURPLE_DARK.getBlue(), 60));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-5, 20, 20);
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 28, 18, 28));

        // Icon ⏱ trong vòng tròn tím
        JLabel lblIcon = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(237, 233, 254));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                FontMetrics fm = g2.getFontMetrics();
                String s = "⏱";
                g2.drawString(s, (getWidth()-fm.stringWidth(s))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        lblIcon.setPreferredSize(new Dimension(68, 68));

        // Text
        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 6));
        pnlText.setOpaque(false);

        JLabel lblTitle = new JLabel("Chấm công");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSub = new JLabel("Hôm nay: " +
                LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                "   •   Nhấn để chấm công ngay");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(TEXT_GRAY);

        pnlText.add(lblTitle);
        pnlText.add(lblSub);

        // Nút bấm bên phải
        JLabel lblBtn = new JLabel("Chấm công ›") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PURPLE_DARK, getWidth(), 0, PURPLE_LIGHT));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBtn.setForeground(WHITE);
        lblBtn.setHorizontalAlignment(SwingConstants.CENTER);
        lblBtn.setPreferredSize(new Dimension(140, 42));
        lblBtn.setOpaque(false);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(pnlText, BorderLayout.CENTER);
        card.add(lblBtn,  BorderLayout.EAST);
        return card;
    }

    /** Card chức năng */
    private JPanel createFuncCard(String icon, String title, String sub,
                                  Color bgColor, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, hovered ? 20 : 10));
                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-4, 20, 20);
                Color bg = hovered ? bgColor.brighter() : bgColor;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-5, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 22, 20, 22));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon vòng tròn
        JLabel lblIcon = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 45));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(icon, (getWidth()-fm.stringWidth(icon))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        lblIcon.setPreferredSize(new Dimension(44, 44));

        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlText.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSub = new JLabel("<html>" + sub + "</html>");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(TEXT_GRAY);

        pnlText.add(lblTitle); pnlText.add(lblSub);

        card.add(lblIcon,  BorderLayout.NORTH);
        card.add(pnlText,  BorderLayout.CENTER);
        return card;
    }

    /** Section công việc */
    private JPanel buildTaskSection() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(WHITE);
        card.setBorder(new EmptyBorder(20, 24, 20, 24));
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc: 18; border: 1,1,1,1, #E2E8F0,, 18;");

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Công việc cần làm");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblMore = new JLabel("Xem thêm ›");
        lblMore.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMore.setForeground(PURPLE_DARK);
        lblMore.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(lblMore,  BorderLayout.EAST);

        // Nội dung — placeholder, sau sẽ load từ API
        JPanel pnlBody = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pnlBody.setOpaque(false);

        JLabel lblFlag = new JLabel("🚩");
        lblFlag.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel pnlMsg = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlMsg.setOpaque(false);

        JLabel lbl1 = new JLabel("Chưa có công việc nào được giao");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl1.setForeground(TEXT_DARK);

        JLabel lbl2 = new JLabel("Kiểm tra lại sau hoặc liên hệ quản lý để được phân công.");
        lbl2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl2.setForeground(TEXT_GRAY);

        pnlMsg.add(lbl1); pnlMsg.add(lbl2);
        pnlBody.add(lblFlag); pnlBody.add(pnlMsg);

        card.add(pnlHeader, BorderLayout.NORTH);
        card.add(pnlBody,   BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════
    //  TRANG THÔNG BÁO
    // ══════════════════════════════════════════════════════
    private JPanel buildNotifPage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblHead = new JLabel("THÔNG BÁO");
        lblHead.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHead.setForeground(TEXT_DARK);
        lblHead.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel pnlEmpty = new JPanel(new GridBagLayout());
        pnlEmpty.setBackground(WHITE);
        pnlEmpty.putClientProperty(FlatClientProperties.STYLE,
                "arc: 18; border: 1,1,1,1, #E2E8F0,, 18;");

        JLabel lbl = new JLabel("<html><center>🔔<br><br>" +
                "<b style='font-size:14px'>Chưa có thông báo</b><br>" +
                "<span style='color:gray'>Bạn sẽ nhận được thông báo từ quản lý tại đây</span>" +
                "</center></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        pnlEmpty.add(lbl);

        page.add(lblHead,   BorderLayout.NORTH);
        page.add(pnlEmpty,  BorderLayout.CENTER);
        return page;
    }

    // ══════════════════════════════════════════════════════
    //  TRANG TÀI KHOẢN
    // ══════════════════════════════════════════════════════
    private JPanel buildProfilePage() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblHead = new JLabel("TÀI KHOẢN");
        lblHead.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHead.setForeground(TEXT_DARK);
        lblHead.setBorder(new EmptyBorder(0, 0, 20, 0));
        page.add(lblHead, BorderLayout.NORTH);

        // Card thông tin
        JPanel card = new JPanel(new BorderLayout(30, 0));
        card.setBackground(WHITE);
        card.setBorder(new EmptyBorder(32, 36, 32, 36));
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc: 20; border: 1,1,1,1, #E2E8F0,, 20;");

        // Avatar lớn
        JLabel lblAv = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PURPLE_DARK, getWidth(), getHeight(), PURPLE_LIGHT));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
                FontMetrics fm = g2.getFontMetrics();
                String init = account.getFullname() != null && !account.getFullname().isEmpty()
                        ? String.valueOf(account.getFullname().charAt(0)).toUpperCase() : "U";
                g2.drawString(init, (getWidth()-fm.stringWidth(init))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        lblAv.setPreferredSize(new Dimension(110, 110));

        // Thông tin text
        JPanel pnlInfo = new JPanel(new GridLayout(4, 1, 0, 10));
        pnlInfo.setOpaque(false);

        JLabel lblName = new JLabel(account.getFullname() != null ? account.getFullname() : account.getUsername());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblName.setForeground(TEXT_DARK);

        JLabel lblRole = new JLabel(account.getRole() != null ? account.getRole() : "Nhân viên");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblRole.setForeground(PURPLE_DARK);

        JLabel lblUser = new JLabel("@" + account.getUsername());
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(TEXT_GRAY);

        JLabel lblEmail = new JLabel(account.getEmail() != null ? "✉  " + account.getEmail() : "✉  Chưa cập nhật email");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEmail.setForeground(TEXT_GRAY);

        pnlInfo.add(lblName);
        pnlInfo.add(lblRole);
        pnlInfo.add(lblUser);
        pnlInfo.add(lblEmail);

        card.add(lblAv,    BorderLayout.WEST);
        card.add(pnlInfo,  BorderLayout.CENTER);

        // Nút đổi mật khẩu
        JButton btnChangePass = new JButton("🔐  Đổi mật khẩu");
        btnChangePass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnChangePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangePass.putClientProperty(FlatClientProperties.STYLE,
                "background: #EDE9FE; foreground: #583DAC;" +
                        "arc: 12; borderWidth: 0; focusWidth: 0; margin: 8,24,8,24;");
        btnChangePass.addActionListener(e -> {
            DoiMatKhauDialog dlg = new DoiMatKhauDialog(StaffMainForm.this, account);
            dlg.setVisible(true);
        });

        // Nút đăng xuất
        JButton btnLogout = new JButton("🚪  Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.putClientProperty(FlatClientProperties.STYLE,
                "background: #FEE2E2; foreground: #EF4444;" +
                        "arc: 12; borderWidth: 0; focusWidth: 0; margin: 8,24,8,24;");
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 16));
        pnlSouth.setOpaque(false);
        pnlSouth.add(btnChangePass);
        pnlSouth.add(Box.createHorizontalStrut(12));
        pnlSouth.add(btnLogout);

        page.add(card,    BorderLayout.CENTER);
        page.add(pnlSouth, BorderLayout.SOUTH);
        return page;
    }

    // ══════════════════════════════════════════════════════
    //  BOTTOM NAVIGATION
    // ══════════════════════════════════════════════════════
    private JPanel buildBottomNav() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(226, 232, 240));
                g2.drawLine(0, 0, getWidth(), 0);

                // Thanh tròn giống BottomTaskbar của E0bManagerTwo
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(45, 52, 71));
                g2.fillRoundRect(15, 2, getWidth()-30, getHeight()-6, 35, 35);
                g2.dispose();
            }
        };
        nav.setOpaque(false);
        nav.setPreferredSize(new Dimension(0, 70));

        btnNavHome    = createNavBtn("🏠", "Trang chủ");
        btnNavNotif   = createNavBtn("🔔", "Thông báo");
        btnNavTask    = createNavBtn("📋", "Công việc");
        btnNavProfile = createNavBtn("👤", "Tài khoản");

        btnNavHome.addActionListener(e    -> { cardLayout.show(pnlContent, "HOME");     setNavActive(btnNavHome); });
        btnNavNotif.addActionListener(e   -> { cardLayout.show(pnlContent, "NOTIF");    setNavActive(btnNavNotif); });
        btnNavTask.addActionListener(e    -> { cardLayout.show(pnlContent, "CONGVIEC"); setNavActive(btnNavTask); });
        btnNavProfile.addActionListener(e -> { cardLayout.show(pnlContent, "PROFILE");  setNavActive(btnNavProfile); });

        nav.add(Box.createHorizontalStrut(120));
        nav.add(btnNavHome);
        nav.add(Box.createHorizontalStrut(30));
        nav.add(btnNavNotif);
        nav.add(Box.createHorizontalStrut(30));
        nav.add(btnNavTask);
        nav.add(Box.createHorizontalStrut(30));
        nav.add(btnNavProfile);
        nav.add(Box.createHorizontalStrut(120));
        return nav;
    }

    private JButton createNavBtn(String icon, String label) {
        JButton btn = new JButton("<html><center>" + icon + "<br>" +
                "<small>" + label + "</small></center></html>");
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        btn.setForeground(new Color(180, 185, 200));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 54));
        return btn;
    }

    private void setNavActive(JButton active) {
        for (JButton btn : new JButton[]{btnNavHome, btnNavNotif, btnNavTask, btnNavProfile}) {
            btn.setForeground(btn == active
                    ? new Color(241, 196, 15)
                    : new Color(180, 185, 200));
        }
    }

    private String getGreeting() {
        int h = LocalTime.now().getHour();
        if      (h < 12) return "Chào buổi sáng ☀️";
        else if (h < 18) return "Chào buổi chiều 🌤";
        else             return "Chào buổi tối 🌙";
    }
}