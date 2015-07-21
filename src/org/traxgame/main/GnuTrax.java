/* 
   
   Date: 26th of August 2014
   version 0.1
   All source under GPL version 3 or latter
   (GNU General Public License - http://www.gnu.org/)
   contact traxplayer@gmail.com for more information about this code
   
*/

package org.traxgame.main;

import java.util.*;
import java.io.*;

public class GnuTrax {

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
        moveHistory = new ArrayList<String>();


        if (computer_algorithme.equals("simple")) {
            cp = new ComputerPlayerSimple();
        }
        if (computer_algorithme.equals("uct")) {
            cp = new ComputerPlayerUct();
        }
        if (computer_algorithme.equals("iterative")) {
            cp = new ComputerPlayerIterative();
        }
        if (computer_algorithme.equals("alphabeta")) {
            cp = new ComputerPlayerAlphaBeta();
        }
    }

    private boolean analyzeMode, autodisplay, logv, learning;
    private boolean ponder, alarmv;
    private int noise, display, searchDepth, searchTime, computerColour;
    private String playerName;
    private Traxboard tb;
    private ComputerPlayer cp;
    private ArrayList<String> moveHistory;

    public void userAnalyze() {
        analyzeMode = true;
        computerColour = Traxboard.NOPLAYER;
    }

    public String makeComputerMove() {
        return cp.computerMove(tb);
    }

    public Traxboard getBoard() {
        return tb;
    }

    public void setComputerColor(int color) {
        computerColour = color;
    }

    public int getComputerColor() {
        return this.computerColour;
    }

    public ComputerPlayer getComputerPlayer() {
        return cp;
    }

    private void userBack(ArrayList<String> command) {
        int end;

        if (moveHistory.size() == 0) return;
        if (command.size() == 0) { // remove last element
            end = 1;
        } else {
            try {
                end = Integer.parseInt(command.get(0));
            } catch (NumberFormatException e) {
                return;
            }
        }
        for (int i = 0; i < end; i++) {
            if (moveHistory.size() == 0) return;
            moveHistory.remove(moveHistory.size() - 1);
        }
        tb = new Traxboard();
        for (String s : moveHistory) {
            try {
                tb.makeMove(s);
            } catch (IllegalMoveException e) {
                throw new RuntimeException("This should never happen.");
            }
        }
    }

    private void userGo() {
        computerColour = tb.whoToMove();
    }

    private void userLog(ArrayList<String> command) {
        if (command.size() > 1) {
            if (command.get(1).equals("on")) {
                TraxUtil.startLog();
                return;
            }
            if (command.get(1).equals("off")) {
                TraxUtil.stopLog();
            }
        }
    }

    public void userNew() {
        tb = new Traxboard();
        computerColour = Traxboard.BLACK;
        moveHistory = new ArrayList<String>();
    }


    public int getTileAt(int i, int j) {
        return tb.getAt(i,j);
    }

    public List<Integer> getPossibleMoves(int i, int j) {
        return tb.getLegalTiles(i, j);
    }

    boolean checkForBookFile() {
        return false;
    }

    private void checkForWin() {
        int gameValue;

        gameValue = tb.isGameOver();
        if (gameValue == Traxboard.NOPLAYER)
            return;
    }

    public List<String> getMoveHistory() {
        return moveHistory;
    }

    public int isGameOver() {
        return tb.isGameOver();
    }

    public int getBoardCols() {
        return tb.getColSize();
    }

    public int getBoardRows() {
        return tb.getRowSize();
    }

    public void gotAMove(String theMove) throws IllegalMoveException {
        moveHistory.add(TraxUtil.normalizeMove(tb, theMove));
        tb.makeMove(theMove);
    }



}
