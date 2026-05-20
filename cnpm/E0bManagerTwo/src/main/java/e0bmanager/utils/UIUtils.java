package e0bmanager.utils;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;

public class UIUtils {

    // Chuỗi Style mặc định cho Menu Button
    public static final String MENU_BUTTON_STYLE =
            "arc: 20; background: #ffffff; border: 1,1,1,1,#e0e0e0; margin: 10,10,10,10; focusWidth: 0";

    // Chuỗi Style khi Hover
    public static final String MENU_BUTTON_HOVER =
            "arc: 20; background: #f0f7ff; border: 1,1,1,1,#3498db; margin: 10,10,10,10; focusWidth: 0";

    /**
     * Áp dụng Style bo góc và viền cho bất kỳ Component nào (Button, Panel, TextField)
     */
    public static void applyStyle(JComponent comp, String style) {
        comp.putClientProperty(FlatClientProperties.STYLE, style);
    }

    /**
     * Tạo hiệu ứng Hover chuẩn FlatLaf
     */
    public static void addHoverEffect(JButton btn, String normalStyle, String hoverStyle) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                applyStyle(btn, hoverStyle);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                applyStyle(btn, normalStyle);
            }
        });
    }
}