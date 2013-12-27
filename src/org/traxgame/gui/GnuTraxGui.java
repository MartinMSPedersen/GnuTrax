package org.traxgame.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.*;
import org.traxgame.*;

public class GnuTraxGui extends JFrame {

	// TODO Store the images in another structure where image and some metadata
	// can be combined.
	private BufferedImage[] image;
	private JPanel outerPanel;
	private java.util.List<ImagePanel> board;
	private GnuTrax gnuTraxGame;
	
	public GnuTraxGui() {
		super("GnuTrax 1.0");
		setResizable(false);
		setMinimumSize(new Dimension(640, 640));
		board = new ArrayList<ImagePanel>();
		this.gnuTraxGame = new GnuTrax("simple");
		this.gnuTraxGame.userNew();
	}

	public void setMove(int x, int y, BufferedImage image) {
		board.get(x * 8 + y).setImage(image);
		this.repaint();
	}

	public java.util.List<BufferedImage> getPossibleTilesForPosition(int x, int y) {
		java.util.List<BufferedImage> possibleMoves = new ArrayList<BufferedImage>();
		java.util.List<String> theMoves = this.gnuTraxGame.getPossibleMoves();
		possibleMoves.add(image[Traxboard.NS]);
		possibleMoves.add(image[Traxboard.EN]);
		possibleMoves.add(image[Traxboard.ES]);
		return possibleMoves;
	}

	public void addComponentsToPane(final Container pane) {
		image = new BufferedImage[8];
		try {
			image[Traxboard.NS] = ImageIO.read(getClass().getClassLoader()
					.getResource("images/large/ns.gif")); // 80x80 gif
			image[Traxboard.WE] = ImageIO.read(getClass().getClassLoader()
					.getResource("images/large/we.gif")); // 80x80 gif
			image[Traxboard.NW] = ImageIO.read(getClass().getClassLoader()
					.getResource("images/large/nw.gif")); // 80x80 gif
			image[Traxboard.NE] = ImageIO.read(getClass().getClassLoader()
					.getResource("images/large/ne.gif")); // 80x80 gif
			image[Traxboard.WS] = ImageIO.read(getClass().getClassLoader()
					.getResource("images/large/ws.gif")); // 80x80 gif
			//
			image[Traxboard.SE] = ImageIO.read(getClass().getClassLoader()
					.getResource("images/large/se.gif")); // 80x80 gif
			image[Traxboard.INVALID] = ImageIO.read(getClass().getClassLoader()
					.getResource("images/large/invalid.gif")); // 80x80 gif
			image[Traxboard.EMPTY] = ImageIO.read(getClass().getClassLoader()
					.getResource("images/large/blank.gif")); // 80x80 gif
		} catch (IOException e) {
			e.printStackTrace();
		}
		outerPanel = new JPanel();
		outerPanel.setLayout(new GridLayout(8, 8));
		ImagePanel innerPanel;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				innerPanel = new ImagePanel(image[Traxboard.EMPTY], this, i, j);
				outerPanel.add(innerPanel);
				board.add(innerPanel);
			}
		}
		pane.add(outerPanel);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method is invoked
	 * from the event dispatch thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		GnuTraxGui frame = new GnuTraxGui();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Set up the content pane.
		frame.addComponentsToPane(frame.getContentPane());
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public static final long serialVersionUID = 2488472L;

}
