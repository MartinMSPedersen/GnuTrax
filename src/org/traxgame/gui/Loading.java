package org.traxgame.gui;

import java.awt.Label;

import javax.swing.JFrame;

public class Loading extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2221062376560774087L;

	public Loading() {
		this.add(new Label("AI is thinking. Please wait...."));
		this.pack();
	}
}
