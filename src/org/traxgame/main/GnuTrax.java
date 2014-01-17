package org.traxgame.main;

/* 

 Date: 16th of September 2009
 version 0.1
 All source under GPL version 2 
 (GNU General Public License - http://www.gnu.org/)
 contact traxplayer@gmail.com for more information about this code

 */
import java.util.ArrayList;
import java.util.List;

public class GnuTrax {

	private String newestBoard;
	private boolean analyzeMode, autodisplay, logv, learning;
	private boolean ponder, alarmv;
	private int noise, display, searchDepth, searchTime, computerColour;
	private String playerName;
	private Traxboard tb;
	private ComputerPlayer cp;

	public ComputerPlayer getComputerPlayer() {
		return cp;
	}
	
	public int getComputerColor() {
		return computerColour;
	}
	public void setComputerColor(int value) {
		computerColour = value;
	}
	
	
	public GnuTrax(String computer_algorithme) {
		noise = 100;
		autodisplay = true;
		alarmv = true;
		logv = false;
		display = 1;
		computerColour = Traxboard.BLACK;
		learning = true;
		searchDepth = 4;
		searchTime = 180;
		analyzeMode = false;
		playerName = "";
		ponder = true;
		tb = new Traxboard();
		if (computer_algorithme.equals("simple")) {
			cp = new ComputerPlayerSimple();
		} else if (computer_algorithme.equals("uct")) {
			cp = new ComputerPlayerUct();
		} else {
			// cp = new ComputerPlayerSimple ();
			cp = new ComputerPlayerUct();
		}
	}

	public Traxboard getBoard() {
		return tb;
	}
	
	public int getBoardCols() {
		return tb.getColSize();
	}
	
	public int getBoardRows() {
		return tb.getRowSize();
	}
	
	public void userAnalyze() {
		analyzeMode = true;
		computerColour = Traxboard.NOPLAYER;
	}

	public int getTileAt(int x, int y) {
		return this.tb.getAt(x, y);
	}


	public int isGameOver() {
		return tb.isGameOver();
	}


	public String getTheBoard() {
		return this.tb.toString();
	}

	public String makeComputerMove() {
		String line;
		line = cp.computerMove(tb);
		return line;
	}


	public void userNew() {
		tb = new Traxboard();
		computerColour = Traxboard.BLACK;
		/*
		 * delete(moveHistory); moveHistory=new String[] ;
		 */
	}

	public List<Integer> getPossibleMoves(int x, int y) {
		return tb.getLegalTiles(y, x);
	}

	boolean checkForBookFile() {
		return false;
	}

	public String getLatestBoard() {
		return newestBoard;
	}
	
	public void gotAMove(String theMove) throws IllegalMoveException {
		tb.makeMove(theMove);
		newestBoard = tb.toString();
		// System.out.println(tb);
		/* (*moveHistory).push_back(theMove); */
		// checkForWin();
	}


}
