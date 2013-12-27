package org.traxgame.gui;
import javax.swing.JPanel;
import java.awt.event.MouseListener;
import java.awt.image.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    BufferedImage image;

    ImagePanel(BufferedImage image) {
	setImage(image);
    }

    ImagePanel() {
	image=null;
    }

    public void setImage(BufferedImage image) {
	this.image=image;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
    
    public synchronized void addMouseListener(MouseListener l) {
    	System.out.println("image" + image);
    	super.addMouseListener(l);
    }

    public static final long serialVersionUID = 14362462L;
}


