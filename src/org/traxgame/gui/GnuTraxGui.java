package org.traxgame.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.imageio.*;
import org.traxgame.*;

public class GnuTraxGui extends JFrame {

	private Tile[] tiles;
	private JPanel outerPanel;
	private java.util.List<ImagePanel> board;
	private GnuTrax gnuTraxGame;
	private Loading loading;
	private boolean isGameDone;

	public GnuTraxGui() {
		super("GnuTrax 1.0");
		setResizable(false);
		setMinimumSize(new Dimension(720, 720));
		loading = new Loading(this);
		loading.setVisible(false);
		board = new ArrayList<ImagePanel>();
		newGame("simple");
	}

	private void newGame(String ai) {
		this.gnuTraxGame = new GnuTrax(ai);
		this.gnuTraxGame.userNew();
		isGameDone = false;
		if (board != null && board.size() > 0) {
			for (int i = 0; i < 81; i++) {
				board.get(i).setImage(tiles[Traxboard.INVALID].getImage());
			}
			board.get(0).setImage(tiles[Traxboard.EMPTY].getImage());
			this.repaint();
		}
	}

	private String position(int x, int y, int tileType) {
		StringBuilder sb = new StringBuilder();
		// System.out.println("POS: x: " + x + " Y: " + y);
		switch (x) {
		case 0:
			sb.append("@");
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			sb.append(Character.toString((char) (x - 1 + 65)));
			break;
		}
		sb.append(y);
		switch (tileType) {
		case Traxboard.NS:
		case Traxboard.EW:
			sb.append("+");
			break;
		case Traxboard.SE:
		case Traxboard.WN:
			sb.append("/");
			break;
		case Traxboard.NE:
		case Traxboard.WS:
			sb.append("\\");
			break;
		}
		// System.out.println(sb.toString());
		return sb.toString();
	}

	private void clearBoard() {
		for (ImagePanel ip : board) {
			ip.setImage(tiles[Traxboard.EMPTY].getImage());
		}
	}

	// TODO Auto extend board when needed. See also init function and set move
	private void drawBoard() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board.get(i * 9 + j).setImage(
						tiles[this.gnuTraxGame.getTileAt(i, j)].getImage());
			}
		}
	}

	private boolean checkForWinner() {
		if (this.gnuTraxGame.isGameOver() != Traxboard.NOPLAYER) {
			// Show message box with winner and if you want to
			// play again
			switch (this.gnuTraxGame.isGameOver()) {
			case Traxboard.BLACK:
				showNewGameDialog("black");
				break;
			case Traxboard.WHITE:
				showNewGameDialog("white");
				break;
			default:
				showNewGameDialog("everyone");
			}
			isGameDone = true;
			return true;
		}
		return false;
	}

	private void showNewGameDialog(String winner) {
		JOptionPane.showMessageDialog(this, "Good game. The winner was "
				+ winner, "Game Over", JOptionPane.INFORMATION_MESSAGE);
		showAndChooseAi();
	}

	private void makeAiMove() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						loading.setVisible(true);
					}
				});

				try {
					String aiMove = gnuTraxGame.makeComputerMove();
					gnuTraxGame.gotAMove(aiMove);
				} catch (IllegalMoveException ime) {
					System.out
							.println("AI made an illegal move... very strange");
				}

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						loading.setVisible(false);
						clearBoard();
						drawBoard();
						repaint();
						checkForWinner();
					}
				});
			}

		}).start();
	}

	public void setMove(int x, int y, Tile tile) {
		boolean aiMayMove = false;
		String theMove = position(x, y, tile.getTileType());
		try {
			this.gnuTraxGame.gotAMove(theMove);
			board.get(x * 9 + y).setImage(tile.getImage());
			clearBoard();
			drawBoard();
			aiMayMove = true;
			this.repaint();
			if (checkForWinner())
				return;
		} catch (IllegalMoveException ime) {
			// Show message to the user and say try again
			System.out.println("ERROR " + ime.getMessage() + "\nThe move: "
					+ theMove);
		}
		if (aiMayMove) {
			makeAiMove();
			if (checkForWinner())
				return;
		}
		// System.out.println(this.gnuTraxGame.getTheBoard());
	}

	public java.util.List<Tile> getPossibleTilesForPosition(int x, int y) {
		java.util.List<Tile> possibleMoves = new ArrayList<Tile>();
		java.util.List<String> theMoves = this.gnuTraxGame.getPossibleMoves();
		for (String s : theMoves) {
			String[] data = s.split("");
			switch (data[3]) {
			case "+":
				possibleMoves.add(tiles[Traxboard.NS]);
				possibleMoves.add(tiles[Traxboard.SN]);
				possibleMoves.add(tiles[Traxboard.EW]);
				possibleMoves.add(tiles[Traxboard.WE]);
				break;
			case "/":
				possibleMoves.add(tiles[Traxboard.ES]);
				possibleMoves.add(tiles[Traxboard.SE]);
				possibleMoves.add(tiles[Traxboard.WN]);
				possibleMoves.add(tiles[Traxboard.NW]);
				break;
			case "\\":
				possibleMoves.add(tiles[Traxboard.EN]);
				possibleMoves.add(tiles[Traxboard.NE]);
				possibleMoves.add(tiles[Traxboard.SW]);
				possibleMoves.add(tiles[Traxboard.WS]);
				break;
			}
		}
		// TODO How to make the moves unique s.t. not to many are shown?
		HashSet hs = new HashSet<Tile>();
		hs.addAll(possibleMoves);
		return new ArrayList<Tile>(hs);
	}

	public void addComponentsToPane(final Container pane) {
		tiles = new Tile[8];
		try {

			tiles[Traxboard.NS] = new Tile(ImageIO.read(getClass()
					.getClassLoader().getResource("images/large/ns.gif")),
					Traxboard.NS); // 80x80 gif
			tiles[Traxboard.WE] = new Tile(ImageIO.read(getClass()
					.getClassLoader().getResource("images/large/we.gif")),
					Traxboard.WE); // 80x80 gif
			tiles[Traxboard.NW] = new Tile(ImageIO.read(getClass()
					.getClassLoader().getResource("images/large/nw.gif")),
					Traxboard.NW); // 80x80 gif
			tiles[Traxboard.NE] = new Tile(ImageIO.read(getClass()
					.getClassLoader().getResource("images/large/ne.gif")),
					Traxboard.NE); // 80x80 gif
			tiles[Traxboard.WS] = new Tile(ImageIO.read(getClass()
					.getClassLoader().getResource("images/large/ws.gif")),
					Traxboard.WS); // 80x80 gif
			tiles[Traxboard.SE] = new Tile(ImageIO.read(getClass()
					.getClassLoader().getResource("images/large/se.gif")),
					Traxboard.SE); // 80x80 gif
			tiles[Traxboard.INVALID] = new Tile(ImageIO.read(getClass()
					.getClassLoader().getResource("images/large/invalid.gif")),
					Traxboard.INVALID); // 80x80 gif
			tiles[Traxboard.EMPTY] = new Tile(ImageIO.read(getClass()
					.getClassLoader().getResource("images/large/blank.gif")),
					Traxboard.EMPTY); // 80x80 gif
		} catch (IOException e) {
			e.printStackTrace();
		}
		outerPanel = new JPanel();
		outerPanel.setLayout(new GridLayout(9, 9));
		ImagePanel innerPanel;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				innerPanel = new ImagePanel(
						tiles[Traxboard.INVALID].getImage(), this, j, i);
				outerPanel.add(innerPanel);
				board.add(innerPanel);
			}
		}
		board.get(0).setImage(tiles[Traxboard.EMPTY].getImage());
		pane.add(outerPanel);

		showAndChooseAi();
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

	private void showAndChooseAi() {
		Object[] possibilities = { "simple (easy)", "uct (hard)" };
		String s = (String) JOptionPane.showInputDialog(this, "Choose AI:",
				"New game", JOptionPane.PLAIN_MESSAGE, null, possibilities,
				"simple (easy)");
		if ((s != null) && (s.length() > 0)) {
			newGame(s.split(" ")[0]);
		} else {
			JOptionPane.showMessageDialog(this, "Please choose a correct AI",
					"Wrong chose", JOptionPane.INFORMATION_MESSAGE);
		}
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
