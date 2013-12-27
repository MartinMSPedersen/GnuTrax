package org.traxgame.gui;
import javax.swing.JPanel;
import java.awt.image.*;
import java.awt.*;

public class imagePanel extends JPanel {
    BufferedImage image;

    imagePanel(BufferedImage image) {
        this.image = image;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

    public static final long serialVersionUID = 34362462L;
}


