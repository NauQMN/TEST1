package e0bmanager.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class CircleAvatar extends JLabel {
    private Icon icon;
    private int borderSize = 2;
    private Color borderColor = new Color(60, 60, 150);

    public CircleAvatar() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getIcon() == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int diameter = Math.min(width, height);

        // Vẽ viền tròn
        g2.setColor(borderColor);
        g2.fillOval(0, 0, diameter, diameter);

        // Cắt ảnh thành hình tròn
        BufferedImage mask = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gMask = mask.createGraphics();
        gMask.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gMask.fill(new Ellipse2D.Double(borderSize, borderSize, diameter - borderSize * 2, diameter - borderSize * 2));
        gMask.dispose();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gImg = image.createGraphics();
        getIcon().paintIcon(this, gImg, 0, 0);
        gImg.setComposite(AlphaComposite.DstIn);
        gImg.drawImage(mask, 0, 0, null);
        gImg.dispose();

        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }
}
