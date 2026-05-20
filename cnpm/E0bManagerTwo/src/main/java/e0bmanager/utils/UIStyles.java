package e0bmanager.utils;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UIStyles {
    // --- Colors ---
    public static final Color PRIMARY_COLOR = new Color(52, 152, 219);    // Blue
    public static final Color SECONDARY_COLOR = new Color(45, 52, 71);  // Dark Blue
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);    // Green
    public static final Color DANGER_COLOR = new Color(231, 76, 60);     // Red
    public static final Color WARNING_COLOR = new Color(241, 196, 15);    // Yellow
    public static final Color BACKGROUND_LIGHT = new Color(245, 246, 250);
    public static final Color TEXT_DARK = new Color(44, 62, 80);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(220, 221, 225);

    // --- Fonts ---
    public static final Font FONT_BOLD_LARGE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_BOLD_MEDIUM = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_REGULAR_MEDIUM = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_REGULAR_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    // --- Component Factory ---

    public static void applyRoundedStyle(JComponent component, int arc) {
        component.putClientProperty(FlatClientProperties.STYLE, "arc: " + arc);
    }

    public static JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 5,10,5,10");
        return field;
    }

    public static JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 5,10,5,10; showRevealButton: true");
        return field;
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(TEXT_LIGHT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderShadowWidth: 2");
        return btn;
    }

    public static JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #ffffff");
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        return card;
    }
}
