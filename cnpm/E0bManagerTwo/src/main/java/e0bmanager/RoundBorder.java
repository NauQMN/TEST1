package e0bmanager;

import javax.swing.border.Border;
import java.awt.*;

public class RoundBorder implements Border {
    private int radius;
    public RoundBorder(int radius) {
        this.radius = radius;
    }
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
    }
    public boolean isBorderOpaque() {
        return true;
    }
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(44, 62, 80)); // Màu viền nút
        g2d.drawRoundRect(x, y, width-1, height-1, radius, radius);
    }
}