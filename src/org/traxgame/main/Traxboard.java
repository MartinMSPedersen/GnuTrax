/* 
Tue Mar 2 14:50:12 CET 2015
version 0.8
All source under GPL version 3 or latter
(GNU General Public License - http://www.gnu.org/)
contact traxplayer@gmail.com for more information about this code

*/

package org.traxgame.main;

import java.util.ArrayList;

public class Traxboard
{
    // Piece description...
    //
    //   0()     1(NS)    2(WE)     3(NW) 4 5 6
    //
    // .  .  .  .   o  .  .  x  .  .  o  .  .  o  .  .  x  .  .  x  .
    //
    // .     .  x      x  o     o  o  /  x  x  \  o  o  \  x  x  / o
    //
    // .  .  .  .   o  .  .  x  .  .  x  . . x . . o . . o .
    // the direction of the white lines and their number

    private boolean boardEmpty;
    private int wtm;
    private int[][] board;
    private int gameover;
    private int num_of_tiles;
    private int firstrow, lastrow, firstcol, lastcol;
    private int whiteCorners, blackCorners;
    private int whiteThreats, blackThreats;
    private int whiteThreats_save, blackThreats_save;
    private int whiteCorners_save, blackCorners_save;
    private String border;
    private String border_save;
    private static final boolean DEBUG=false;

    private boolean boardEmpty_save;
    private int wtm_save;
    private int[][] board_save;
    private int gameover_save;
    private int num_of_tiles_save;
    private int firstrow_save, lastrow_save, firstcol_save, lastcol_save;
    public boolean debug = false;

    public static final int EMPTY = 0, INVALID = 7,
            NS = 1, SN = 1, WE = 2, EW = 2, NW = 3, WN = 3, NE = 4, EN = 4, WS = 5,
            SW = 5, SE = 6, ES = 6;
    public static final int WHITE = 0, BLACK = 1, DRAW = 2, NOPLAYER = 3, NORESULT = 3;

    private static String[][] col_row_array;

    static
    {
        StringBuffer str;
        col_row_array = new String[9][9];
        for (char i = '@'; i <= 'H'; i++) {
            for (char j = '0'; j <= '8'; j++) {
                str = new StringBuffer();
                str.append(i);
                str.append(j);
                col_row_array[i - '@'][j - '0'] = new String(str);
            }
        }
    }

    public boolean blank(int piece) { return (piece == EMPTY); }

    /**
     * Returns the numbers of used tiles. Can be used to determine if we are in
     * the opening, middle or end-phase of the game
     *
     * @return the number of used tiles
     */
    public int getNumOfTiles() { return num_of_tiles; }


    @Override
    public int hashCode() { return wtm+board.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj==null) || (obj.getClass() != this.getClass())) { return false; }
        Traxboard t=(Traxboard) obj;
        if (t.wtm!=this.wtm) return false;
        if (t.getColSize()!=this.getColSize()) return false;
        if (t.getRowSize()!=this.getRowSize()) return false;
        for (int i=1; i<=t.getRowSize(); i++) {
            for (int j=1; j<=t.getColSize(); j++) {
                if (t.getAt(i,j)!=this.getAt(i,j)) return false;
            }
        }
        return true;
    }

    public static Traxboard rotate(Traxboard tb)
    {
        Traxboard result=new Traxboard(tb);
        for (int i=0; i<17; i++) {
            for (int j=0; j<17; j++) {
                switch (tb.board[16-j][i]) {
                    case NS:
                        result.board[i][j]=WE;
                        break;
                    case WE:
                        result.board[i][j]=NS;
                        break;
                    case EMPTY:
                        result.board[i][j]=EMPTY;
                        break;
                    case NW:
                        result.board[i][j]=NE;
                        break;
                    case NE:
                        result.board[i][j]=SE;
                        break;
                    case SE:
                        result.board[i][j]=SW;
                        break;
                    case SW:
                        result.board[i][j]=NW;
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
            }
        }
        result.setCorners();
        result.border=null;
        return result;
    }

    public int getNumberOfWhiteThreats() {
        if (whiteThreats==-1) count2times();
        return whiteThreats;
    }

    public int getNumberOfBlackThreats() {
        if (blackThreats==-1) count2times();
        return blackThreats;
    }

    public int getNumberOfWhiteCorners() {
        if (whiteCorners==-1) count2times();
        return whiteCorners;
    }

    public int getNumberOfBlackCorners() {
        if (blackCorners==-1) count2times();
        return blackCorners;
    }

    private void count2times() {
        Traxboard t_copy=new Traxboard(rotate(this));

        this.count();
        t_copy.count();

        this.whiteCorners=Math.max(this.whiteCorners,t_copy.whiteCorners);
        this.whiteThreats=Math.max(this.whiteThreats,t_copy.whiteThreats);
        this.blackCorners=Math.max(this.blackCorners,t_copy.blackCorners);
        this.blackThreats=Math.max(this.blackThreats,t_copy.blackThreats);
    }

    private void setCorners()
    {
        firstrow=-1;
        firstcol=-1;
        lastcol=-1;
        lastrow=-1;
        for (int i=0; i<17; i++) {
            for (int j=0; j<17; j++) {
                if ((firstrow<0) && (board[i][j]!=EMPTY)) firstrow=i;
                if ((lastrow<0) && (board[16-i][j]!=EMPTY)) lastrow=16-i;
                if ((firstcol<0) && (board[j][i]!=EMPTY)) firstcol=i;
                if ((lastcol<0) && (board[j][16-i]!=EMPTY)) lastcol=16-i;
            }
        }
    }

    private void saveState()
    {
        wtm_save = wtm;
        boardEmpty_save = boardEmpty;
        gameover_save = gameover;
        num_of_tiles_save = num_of_tiles;
        firstrow_save = firstrow;
        firstcol_save = firstcol;
        lastrow_save = lastrow;
        lastcol_save = lastcol;
        whiteThreats_save = whiteThreats;
        blackThreats_save = blackThreats;
        whiteCorners_save = whiteCorners;
        blackCorners_save = blackCorners;
        border_save = border;
        for (int i = 0; i < 17; i++) {
            System.arraycopy(board[i], 0, board_save[i], 0, 17);
        }
    }

    private void restoreState()
    {
        wtm = wtm_save;
        boardEmpty = boardEmpty_save;
        gameover = gameover_save;
        num_of_tiles = num_of_tiles_save;
        firstrow = firstrow_save;
        firstcol = firstcol_save;
        lastrow = lastrow_save;
        lastcol = lastcol_save;
        whiteThreats = whiteThreats_save;
        blackThreats = blackThreats_save;
        whiteCorners = whiteCorners_save;
        blackCorners = blackCorners_save;
        border = border_save;
        for (int i = 0; i < 17; i++) {
            System.arraycopy(board_save[i], 0, board[i], 0, 17);
        }
    }

    public Traxboard()
    {
        int i, j;

        wtm = WHITE;
        gameover = NOPLAYER;
        num_of_tiles = 0;
        whiteCorners=0;
        blackCorners=0;
        whiteThreats=0;
        blackThreats=0;
        border="";

        board = new int[17][17];
        board_save = new int[17][17];
        for (i = 0; i < 17; i++)
            for (j = 0; j < 17; j++)
                board[i][j] = EMPTY;
        boardEmpty = true;

    }

    public Traxboard(Traxboard org)
    {
        int i, j;

        wtm = org.wtm;
        gameover = org.gameover;
        num_of_tiles = org.num_of_tiles;
        board = new int[17][17];
        board_save = new int[17][17];
        for (i = 0; i < 17; i++) {
            for (j = 0; j < 17; j++) {
                this.board[i][j] = org.board[i][j];
                this.board_save[i][j] = org.board_save[i][j];
            }
        }
        firstrow = org.firstrow;
        firstcol = org.firstcol;
        lastrow = org.lastrow;
        lastcol = org.lastcol;
        firstrow_save = org.firstrow_save;
        firstcol_save = org.firstcol_save;
        lastrow_save = org.lastrow_save;
        lastcol_save = org.lastcol_save;
        boardEmpty = org.boardEmpty;
        whiteThreats_save = org.whiteThreats_save;
        blackThreats_save = org.blackThreats_save;
        whiteThreats = org.whiteThreats;
        blackThreats = org.blackThreats;
        whiteCorners_save = org.whiteCorners;
        blackCorners_save = org.blackCorners;
        whiteCorners = org.whiteCorners;
        blackCorners = org.blackCorners;
        border = org.border;
    }

    public int getRowSize() { return ((getNumOfTiles() == 0)?0:1+(lastrow-firstrow)); }
    public int getColSize() { return ((getNumOfTiles() == 0)?0:1+(lastcol-firstcol)); }

    /**
     * Given a row and column, the method checks if that (row,col) is occupied
     * or free
     *
     * @param row
     *            the row number 1-8
     * @param col
     *            the column number 1-8
     * @return true if the place (row,col) is free or false otherwise
     */
    public boolean isBlank(int row, int col) { return (getAt(row, col) == EMPTY); }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder(1000);
        int i, j, k;
        int leftpiece, uppiece, upleftpiece;
        String cols = "     A     B     C     D     E     F     G     H     ";
        String rows = "1 2 3 4 5 6 7 8 ";

        if (boardEmpty)
            return "";
        result.append(cols.substring(0, 5 + 6 * getColSize()));
        // result.append(cols,0,5+6*getColSize());
        result.append('\n');
        for (i = 1; i <= getRowSize(); i++) {
            for (k = 0; k < 4; k++) {
                if (k == 2) {
                    result.append(rows.substring(i * 2 - 2, i * 2));
                    // result.append(rows,i*2-2,2);
                } else {
                    result.append("  ");
                }
                for (j = 1; j <= getColSize(); j++) {
                    switch (getAt(i, j)) {
                        case NS:
                            switch (k) {
                                case 0:
                                    result.append("+--o--");
                                    break;
                                case 1:
                                    result.append("|  o  ");
                                    break;
                                case 2:
                                    result.append("######");
                                    break;
                                case 3:
                                    result.append("|  o  ");
                                    break;
                            }
                            break;
                        case WE:
                            switch (k) {
                                case 0:
                                    result.append("+--#--");
                                    break;
                                case 1:
                                    result.append("|  #  ");
                                    break;
                                case 2:
                                    result.append("ooo#oo");
                                    break;
                                case 3:
                                    result.append("|  #  ");
                                    break;
                            }
                            break;
                        case NW:
                            switch (k) {
                                case 0:
                                    result.append("+--o--");
                                    break;
                                case 1:
                                    result.append("| o   ");
                                    break;
                                case 2:
                                    result.append("oo   #");
                                    break;
                                case 3:
                                    result.append("|   # ");
                                    break;
                            }
                            break;
                        case NE:
                            switch (k) {
                                case 0:
                                    result.append("+--o--");
                                    break;
                                case 1:
                                    result.append("|   o ");
                                    break;
                                case 2:
                                    result.append("##   o");
                                    break;
                                case 3:
                                    result.append("| #   ");
                                    break;
                            }
                            break;
                        case SW:
                            switch (k) {
                                case 0:
                                    result.append("+--#--");
                                    break;
                                case 1:
                                    result.append("|   # ");
                                    break;
                                case 2:
                                    result.append("oo   #");
                                    break;
                                case 3:
                                    result.append("| o   ");
                                    break;
                            }
                            break;
                        case SE:
                            switch (k) {
                                case 0:
                                    result.append("+--#--");
                                    break;
                                case 1:
                                    result.append("| #   ");
                                    break;
                                case 2:
                                    result.append("##   o");
                                    break;
                                case 3:
                                    result.append("|   o ");
                                    break;
                            }
                            break;
                        case EMPTY:
                            uppiece = getAt(i - 1, j);
                            leftpiece = getAt(i, j - 1);
                            upleftpiece = getAt(i - 1, j - 1);
                            switch (k) {
                                case 0:
                                    if ((uppiece == SN) || (uppiece == SW)
                                            || (uppiece == SE)) {
                                        result.append("+--o--");
                                        break;
                                    }
                                    if ((uppiece == WE) || (uppiece == WN)
                                            || (uppiece == EN)) {
                                        result.append("+--#--");
                                        break;
                                    }
                                    if ((upleftpiece != EMPTY) || (leftpiece != EMPTY)) {
                                        result.append("+     ");
                                        break;
                                    }
                                    result.append("      ");
                                    break;
                                case 1:
                                    if (leftpiece == EMPTY)
                                        result.append("      ");
                                    else
                                        result.append("|     ");
                                    break;
                                case 2:
                                    if (leftpiece == EMPTY) result.append("      ");
                                    if ((leftpiece == EW) || (leftpiece == EN) || (leftpiece == ES)) result.append("o     ");
                                    if ((leftpiece == NS) || (leftpiece == NW) || (leftpiece == SW)) result.append("#     ");
                                    break;
                                case 3:
                                    if (leftpiece == EMPTY) result.append("      ");
                                    else
                                        result.append("|     ");
                                    break;
                            }
                            break;
                    } // switch (getAt(i,j));
                } // for (j)

                upleftpiece = getAt(i - 1, j - 1);
                leftpiece = getAt(i, j - 1);
                switch (k) {
                    case 0:
                        if ((upleftpiece != EMPTY) || (leftpiece != EMPTY)) result.append("+");
                        break;
                    case 1:
                        if (leftpiece != EMPTY) result.append("|");
                        break;
                    case 2:
                        if ((leftpiece == EW) || (leftpiece == EN) || (leftpiece == ES)) result.append("o");
                        if ((leftpiece == NS) || (leftpiece == NW) || (leftpiece == SW)) result.append("#");
                        break;
                    case 3:
                        if (leftpiece != EMPTY) result.append("|");
                        break;
                }
                result.append("\n");
            }
        }
        result.append("  ");
        for (j = 1; j <= getColSize(); j++) {
            leftpiece = getAt(getRowSize(), j - 1);
            uppiece = getAt(getRowSize(), j);
            if ((uppiece == EMPTY) && (leftpiece == EMPTY)) result.append("      ");
            if ((uppiece == EMPTY) && (leftpiece != EMPTY)) result.append("+     ");
            if ((uppiece == SN) || (uppiece == SW) || (uppiece == SE)) result.append("+--o--");
            if ((uppiece == WE) || (uppiece == WN) || (uppiece == NE)) result.append("+--#--");
        }
        if (getAt(getRowSize(), getColSize()) != EMPTY) result.append("+");
        result.append("\n");
        return result.toString();
    }

    /**
     * Try to make the specified moves. If all of them are legal, then update the board.
     * Accepts upper-case and lower-case letters, old and new notation but not
     * the very old notation (used until 1986?) which is incompatibel with old
     * notation
     *
     * @param moves
     *            The moves separated with a space
     */
    public void makeMoves(String moves) throws IllegalMoveException
    {
        Traxboard t_copy=new Traxboard(this);

        for (String move : moves.split("\\s")) { t_copy.makeMove(move); }

	  /* All moves ok. */
        for (String move : moves.split("\\s")) {
            try {
                this.makeMove(move);
            }
            catch (IllegalMoveException e) {
                throw new RuntimeException("This should never happen.");
            }
        }
    }

    /**
     * Try to make the specified move. If it is legal, then update the board.
     * Accepts upper-case and lower-case letters, old and new notation but not
     * the very old notation (used until 1986?) which is incompatibel with old
     * notation
     *
     * @param move
     *            The move
     */
    public void makeMove(String move) throws IllegalMoveException
    {
        // updates the board etc. if it was a legal move
        // accepts upper-case & lower-case letters
        // and old&new notation but not the very old
        // notation (used until 1986?) which is incompatibel with old notation

        boolean oldNotation;
        int col, row, neighbor;
        char dir;
        int ohs_up = 0, ohs_down = 0, ohs_right = 0, ohs_left = 0, eks_up = 0, eks_down = 0, eks_right = 0, eks_left = 0;

        if (gameover != NOPLAYER) throw new IllegalMoveException("Game is over.");
        if (move.length() != 3) throw new IllegalMoveException("not a move.");

        move = move.toUpperCase();
        if (boardEmpty) {
            if (move.equals("A1C") || move.equals("@0/")) {
                putAt(1, 1, NW);
                border=null;
                switchPlayer();
                whiteCorners=1;
                blackCorners=1;
                return;
            }
            if (move.equals("A1S") || move.equals("@0+")) {
                putAt(1, 1, NS);
                border=null;
                switchPlayer();
                return;
            }
            throw new IllegalMoveException( "Only A1C,A1S,@0/ and @0+ accepted as first move. Got " + move);
        }

        // handle the old notation 1A special case (changing 1A -> A0)
        if (move.startsWith("1A")) {
            col = 1;
            row = 0;
        } else {
            col = move.charAt(0) - '@';
            row = move.charAt(1) - '0';
        }
        if ((col < 0) || (col > 8)) throw new IllegalMoveException("Illegal column.");
        if ((row < 0) || (row > 8)) throw new IllegalMoveException("Illegal row.");
        if (col == 0 && row == 0) throw new IllegalMoveException("no neighbors.");

        dir = move.charAt(2);
        switch (dir) {
            case 'C':
            case 'S':
            case 'U':
            case 'L':
            case 'R':
            case 'D':
                oldNotation = true;
                break;
            case '/':
            case '+':
            case '\\':
                oldNotation = false;
                break;
            default:
                throw new IllegalMoveException("unknown direction.");
        }

        if (oldNotation) {
            if (!isBlank(row, col)) {
                if (col == 1)
                    col--;
                else if (row == 1)
                    row--;
            }
        }

        if (col == 0 && row == 0) throw new IllegalMoveException("no neighbors.");
        if ((row == 0) && (!canMoveDown())) throw new IllegalMoveException("illegal row.");
        if ((col == 0) && (!canMoveRight())) throw new IllegalMoveException("illegal column.");
        if (!isBlank(row, col)) throw new IllegalMoveException("occupied." + move + " illegal");

        saveState();
        int up = getAt(row-1,col),down=getAt(row+1,col),left=getAt(row,col-1),right=getAt(row,col+1);

        if (up == SN || up == SE || up == SW) ohs_up = 1;
        if (up == EW || up == NW || up == NE) eks_up = 1;
        if (down == NS || down == NE || down == NW) ohs_down = 1;
        if (down == EW || down == SW || down == SE) eks_down = 1;
        if (left == EN || left == ES || left == EW) ohs_left = 1;
        if (left == WS || left == WN || left == NS) eks_left = 1;
        if (right == WN || right == WE || right == WS) ohs_right = 1;
        if (right == ES || right == NS || right == EN) eks_right = 1;
        neighbor = ohs_up + (2 * ohs_down) + (4 * ohs_left) + (8 * ohs_right)
                + (16 * eks_up) + (32 * eks_down) + (64 * eks_left) + (128 * eks_right);

        switch (neighbor) {
            case 0:
                throw new IllegalMoveException("no neighbors.");
            case 1:
                switch (dir) {
                    case '/':
                    case 'L':
                        putAt(row, col, NW);
                        break;
                    case '\\':
                    case 'R':
                        putAt(row, col, NE);
                        break;
                    case '+':
                    case 'S':
                        putAt(row, col, NS);
                        break;
                    case 'U':
                    case 'C':
                    case 'D':
                        throw new IllegalMoveException("illegal direction.");
                    default:
				/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case 2:
                switch (dir) {
                    case '/':
                    case 'R':
                        putAt(row, col, SE);
                        break;
                    case '\\':
                    case 'L':
                        putAt(row, col, SW);
                        break;
                    case '+':
                    case 'S':
                        putAt(row, col, NS);
                        break;
                    case 'C':
                    case 'U':
                    case 'D':
                        throw new IllegalMoveException("illegal direction.");
                    default:
				/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case 4:
                switch (dir) {
                    case '/':
                    case 'U':
                        putAt(row, col, WN);
                        break;
                    case '\\':
                    case 'D':
                        putAt(row, col, WS);
                        break;
                    case '+':
                    case 'S':
                        putAt(row, col, WE);
                        break;
                    case 'C':
                    case 'R':
                    case 'L':
                        throw new IllegalMoveException("illegal direction.");
                    default:
				/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case 8:
                switch (dir) {
                    case '/':
                    case 'D':
                        putAt(row, col, ES);
                        break;
                    case '\\':
                    case 'U':
                        putAt(row, col, EN);
                        break;
                    case '+':
                    case 'S':
                        putAt(row, col, EW);
                        break;
                    case 'C':
                    case 'R':
                    case 'L':
                        throw new IllegalMoveException("illegal direction.");
                    default:
				/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case 16:
                switch (dir) {
                    case '/':
                    case 'L':
                        putAt(row, col, SE);
                        break;
                    case '\\':
                    case 'R':
                        putAt(row, col, SW);
                        break;
                    case '+':
                    case 'S':
                        putAt(row, col, WE);
                        break;
                    case 'C':
                    case 'U':
                    case 'D':
                        throw new IllegalMoveException("illegal direction.");
                    default:
				/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case 18:
                switch (dir) {
                    case '/':
                    case 'R':
                        putAt(row, col, SE);
                        break;
                    case '\\':
                    case 'L':
                    case 'C':
                        putAt(row, col, SW);
                        break;
                    case '+':
                    case 'S':
                    case 'U':
                    case 'D':
                        throw new IllegalMoveException("illegal direction.");
                    default:
				/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case 20:
                switch (dir) {
                    case '/':
                    case 'L':
                    case 'U':
                        throw new IllegalMoveException("illegal direction.");
                    case '\\':
                    case 'C':
                    case 'R':
                    case 'D':
                        putAt(row, col, WS);
                        break;
                    case '+':
                    case 'S':
                        putAt(row, col, WE);
                        break;
                    default:
				/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case 24:
                switch (dir) {
                    case '/':
                    case 'L':
                    case 'C':
                    case 'D':
                        putAt(row, col, SE);
                        break;
                    case 'U':
                    case '\\':
                    case 'R':
                        throw new IllegalMoveException("illegal direction.");
                    case 'S':
                    case '+':
                        putAt(row, col, WE);
                        break;
                    default:
				/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case 32:
                switch (dir) {
                    case '/':
                    case 'R':
                        putAt(row, col, NW);
                        break;
                    case 'D':
                    case 'U':
                    case 'C':
                        throw new IllegalMoveException("illegal direction.");
                    case '\\':
                    case 'L':
                        putAt(row, col, NE);
                        break;
                    case 'S':
                    case '+':
                        putAt(row, col, WE);
                        break;
                    default:
				/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case 33:
                switch (dir) {
                    case '/':
                    case 'L':
                        putAt(row, col, NW);
                        break;
                    case 'R':
                    case '\\':
                        putAt(row, col, NE);
                        break;
                    case 'C':
                    case 'S':
                    case '+':
                    case 'D':
                    case 'U':
                        throw new IllegalMoveException("illegal direction.");
                    default:
				/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case 36:
                if (dir == '/') putAt(row, col, NW);
                if (dir == '\\') throw new IllegalMoveException("illegal direction.");
                if (dir == '+') putAt(row, col, WE);
                if (dir == 'S') putAt(row, col, WE);
                if (dir == 'C') putAt(row, col, WN);
                if (dir == 'L') throw new IllegalMoveException(move + " illegal direction.");
                if (dir == 'R') putAt(row, col, WN);
                if (dir == 'U') putAt(row, col, WN);
                if (dir == 'D') throw new IllegalMoveException("illegal direction.");
                break;
            case 40:
                if (dir == '/') throw new IllegalMoveException("illegal direction.");
                if (dir == '\\') putAt(row, col, EN);
                if (dir == '+') putAt(row, col, EW);
                if (dir == 'S') putAt(row, col, WE);
                if (dir == 'C') putAt(row, col, NE);
                if (dir == 'L') putAt(row, col, NE);
                if (dir == 'R') throw new IllegalMoveException("illegal direction.");
                if (dir == 'U') putAt(row, col, NE);
                if (dir == 'D') throw new IllegalMoveException("illegal direction.");
                break;
            case 64:
                if (dir == '/') putAt(row, col, ES);
                if (dir == '\\') putAt(row, col, EN);
                if (dir == '+') putAt(row, col, NS);
                if (dir == 'S') putAt(row, col, NS);
                if (dir == 'C') throw new IllegalMoveException("illegal direction.");
                if (dir == 'L') throw new IllegalMoveException("illegal direction.");
                if (dir == 'R') throw new IllegalMoveException("illegal direction.");
                if (dir == 'U') putAt(row, col, SE);
                if (dir == 'D') putAt(row, col, NE);
                break;
            case 65:
                if (dir == '/') throw new IllegalMoveException("illegal direction.");
                if (dir == '\\') putAt(row, col, NE);
                if (dir == '+') putAt(row, col, NS);
                if (dir == 'S') putAt(row, col, NS);
                if (dir == 'C') putAt(row, col, NE);
                if (dir == 'L') throw new IllegalMoveException("illegal direction.");
                if (dir == 'R') putAt(row, col, NE);
                if (dir == 'U') throw new IllegalMoveException("illegal direction.");
                if (dir == 'D') putAt(row, col, NE);
                break;
            case 66:
                if (dir == '/') putAt(row, col, SE);
                if (dir == '\\') throw new IllegalMoveException("illegal direction.");
                if (dir == '+') putAt(row, col, SN);
                if (dir == 'S') putAt(row, col, SN);
                if (dir == 'C') putAt(row, col, SE);
                if (dir == 'L') throw new IllegalMoveException("illegal direction.");
                if (dir == 'R') putAt(row, col, SE);
                if (dir == 'U') putAt(row, col, SE);
                if (dir == 'D') throw new IllegalMoveException("illegal direction.");
                break;
            case 72:
                if (dir == '/') putAt(row, col, ES);
                if (dir == '\\') putAt(row, col, EN);
                if (dir == '+') throw new IllegalMoveException("illegal direction.");
                if (dir == 'S') throw new IllegalMoveException("illegal direction.");
                if (dir == 'C') throw new IllegalMoveException("illegal direction.");
                if (dir == 'L') throw new IllegalMoveException("illegal direction.");
                if (dir == 'R') throw new IllegalMoveException("illegal direction.");
                if (dir == 'U') putAt(row, col, NE);
                if (dir == 'D') putAt(row, col, SE);
                break;
            case 128:
                if (dir == '/') putAt(row, col, WN);
                if (dir == '\\') putAt(row, col, WS);
                if (dir == '+') putAt(row, col, NS);
                if (dir == 'S') putAt(row, col, NS);
                if (dir == 'C') throw new IllegalMoveException("illegal direction.");
                if (dir == 'L') throw new IllegalMoveException("illegal direction.");
                if (dir == 'R') throw new IllegalMoveException("illegal direction.");
                if (dir == 'U') putAt(row, col, WS);
                if (dir == 'D') putAt(row, col, WN);
                break;
            case 129:
                if (dir == '/') putAt(row, col, NW);
                if (dir == '\\') throw new IllegalMoveException("illegal direction.");
                if (dir == '+') putAt(row, col, NS);
                if (dir == 'S') putAt(row, col, NS);
                if (dir == 'C') putAt(row, col, NW);
                if (dir == 'L') putAt(row, col, NW);
                if (dir == 'R') throw new IllegalMoveException("illegal direction.");
                if (dir == 'U') throw new IllegalMoveException("illegal direction.");
                if (dir == 'D') putAt(row, col, NW);
                break;
            case 130:
                if (dir == '/') throw new IllegalMoveException("illegal direction.");
                if (dir == '\\') putAt(row, col, SW);
                if (dir == '+') putAt(row, col, SN);
                if (dir == 'S') putAt(row, col, SN);
                if (dir == 'C') putAt(row, col, SW);
                if (dir == 'L') putAt(row, col, SW);
                if (dir == 'R') throw new IllegalMoveException("illegal direction.");
                if (dir == 'U') putAt(row, col, SW);
                if (dir == 'D') throw new IllegalMoveException("illegal direction.");
                break;
            case 132:
                if (dir == '/') putAt(row, col, WN);
                if (dir == '\\') putAt(row, col, WS);
                if (dir == '+') throw new IllegalMoveException("illegal direction.");
                if (dir == 'S') throw new IllegalMoveException("illegal direction.");
                if (dir == 'C') throw new IllegalMoveException("illegal direction.");
                if (dir == 'L') throw new IllegalMoveException("illegal direction.");
                if (dir == 'R') throw new IllegalMoveException("illegal direction.");
                if (dir == 'U') putAt(row, col, WN);
                if (dir == 'D') putAt(row, col, WS);
                break;
            default:
			/* This should never happen */
                throw new RuntimeException("This should never happen.");
        }
        if (row == 0) row++;
        if (col == 0) col++;
        if (!forcedMove(row - 1, col)) {
            restoreState();
            throw new IllegalMoveException("illegal filled cave.");
        }
        if (!forcedMove(row + 1, col)) {
            restoreState();
            throw new IllegalMoveException("illegal filled cave.");
        }
        if (!forcedMove(row, col - 1)) {
            restoreState();
            throw new IllegalMoveException("illegal filled cave.");
        }
        if (!forcedMove(row, col + 1)) {
            restoreState();
            throw new IllegalMoveException("illegal filled cave.");
        }
        whiteCorners=-1;
        blackCorners=-1;
        whiteThreats=-1;
        blackThreats=-1;
        border=null;
		/* note that switchPlayer() _must_ come before isGameOver() */
        switchPlayer();
        isGameOver(); // updates the gameOver attribute

    }

    private void switchPlayer()
    {
        switch (wtm) {
            case WHITE:
                wtm = BLACK;
                break;
            case BLACK:
                wtm = WHITE;
                break;
            default:
			/* This should never happen */
                throw new RuntimeException("This should never happen.");
        }
    }

    public int isGameOver()
    {
        boolean WhiteWins = false, BlackWins = false;
        int sp;

        if (gameover != NOPLAYER) return gameover;
        if (num_of_tiles < 4) {
            gameover = NOPLAYER;
            return gameover;
        }

        // check for line win.
        // check left-right line
        if (getColSize() == 8) {
            // check left-right line
            for (int row = 1; row <= 8; row++) {
                if (checkLine(row, 1, 'r', 'h')) {
                    // Line win
                    sp = getAt(row, 1);
                    if (sp == NS || sp == NE || sp == ES)
                        BlackWins = true;
                    else
                        WhiteWins = true;
                }
            }
        }
        // check up-down line
        if (getRowSize() == 8) {
            for (int col = 1; col <= 8; col++) {
                if (checkLine(1, col, 'd', 'v')) {
                    // Line win
                    sp = getAt(1, col);
                    if (sp == WE || sp == WS || sp == SE)
                        BlackWins = true;
                    else
                        WhiteWins = true;
                }
            }
        }

        // if (need_loop_check==true) {
        // Now check loop wins
        for (int i = 1; i < 8; i++) {
            for (int j = 1; j < 8; j++) {
                switch (getAt(i, j)) {
                    case NW:
                        if (checkLine(i, j, 'u', 'l')) BlackWins = true;
                        break;
                    case SE:
                        if (checkLine(i, j, 'u', 'l')) WhiteWins = true;
                        break;
                    case EMPTY:
                    case NS:
                    case WE:
                    case NE:
                    case WS:
                        break;
                    default:
					/* This should never happen */
                        throw new RuntimeException("This should never happen.");
                }
            }
        }
        // }

        if (WhiteWins && BlackWins) {
            gameover = whoDidLastMove();
            return gameover;
        }
        if (WhiteWins) {
            gameover = WHITE;
            return gameover;
        }
        if (BlackWins) {
            gameover = BLACK;
            return gameover;
        }
        if (uniqueMoves().size() == 0) {
            gameover = DRAW;
            return gameover;
        }
        return NOPLAYER;
    }

    public ArrayList<String> uniqueMoves_with_mirrors() { return uniqueMoves(false); }
    public ArrayList<String> uniqueMoves() { return uniqueMoves(true); }

    private ArrayList<String> uniqueMoves(boolean remove_mirror_moves)
    {
        // complex throw away a lot of equal moves
        // and symmetries (hopefully)

        ArrayList<String> Moves = new ArrayList<>(100); // 50 might be enough
        String AMove;
        int i, j, k;
        int dl, dr, ur, ul, rr, dd;
        int[][] neighbors = new int[10][10]; // which neighbors - default all
        // values 0
        boolean[][][] dirlist = new boolean[10][10][3]; // which directions for
        // move
        // 0 /, 1 \, 2 +
        // true means already used
        // default all values false
        int ohs_up, ohs_down, ohs_right, ohs_left, eks_up, eks_down, eks_right, eks_left;
        int up, down, left, right;
        int iBegin, jBegin, iEnd, jEnd;
        boolean lrsym, udsym, rsym;
        final String col = "@ABCDEFGH";
        final String row = "012345678";

        if (gameover != NOPLAYER) {
            return new ArrayList<>(0);
        }

        if (boardEmpty) { // empty board only these two moves
            Moves.add("@0/");
            Moves.add("@0+");
            Moves.trimToSize();
            return Moves;
        }
        if (getRowSize() * getColSize() == 1) {
            switch (getAt(1, 1)) {
                case NW:
                    Moves.add("@1+");
                    Moves.add("@1/");
                    Moves.add("@1\\");
                    Moves.add("B1+");
                    Moves.add("B1/");
                    Moves.add("B1\\");
                    if (!remove_mirror_moves) {
                        Moves.add("A0\\");
                        Moves.add("A0/");
                        Moves.add("A0+");
                        Moves.add("A2/");
                        Moves.add("A2\\");
                        Moves.add("A2+");
                    }
                    break;
                case NS:
                    Moves.add("@1+");
                    Moves.add("@1/");
                    Moves.add("A0/");
                    Moves.add("A0+");
                    if (!remove_mirror_moves) {
                        Moves.add("@1\\");
                        Moves.add("A0\\");
                        Moves.add("B1/");
                        Moves.add("B1\\");
                        Moves.add("A2/");
                        Moves.add("A2\\");
                    }
                    break;
                default:
				/* This should never happen */
                    throw new RuntimeException("This should never happen.");
            }
            Moves.trimToSize();
            return Moves;
        }

        for (i = 0; i < 10; i++)
            for (j = 0; j < 10; j++)
                for (k = 0; k < 3; k++)
                    dirlist[i][j][k] = false;

        lrsym = isLeftRightMirror();
        udsym = isUpDownMirror();
        rsym = isRotateMirror();
        iBegin = (canMoveDown()) ? 0 : 1;
        jBegin = (canMoveRight()) ? 0 : 1;
        iEnd = (getRowSize() < 8) ? getRowSize() + 1 : 8;
        jEnd = (getColSize() < 8) ? getColSize() + 1 : 8;
	if (remove_mirror_moves) {
		if (lrsym)
		    jEnd = (getColSize() + 1) / 2;
		if (rsym || udsym)
		    iEnd = (getRowSize() + 1) / 2;
        }

        for (i = iBegin; i <= iEnd; i++) {
            for (j = jBegin; j <= jEnd; j++) {
                if (!(isBlank(i, j))) {
                    neighbors[i][j] = 0;
                } else {
                    ohs_up = 0;
                    ohs_down = 0;
                    ohs_right = 0;
                    ohs_left = 0;
                    eks_up = 0;
                    eks_down = 0;
                    eks_right = 0;
                    eks_left = 0;
                    up = getAt(i - 1, j);
                    down = getAt(i + 1, j);
                    left = getAt(i, j - 1);
                    right = getAt(i, j + 1);

                    if (up == SN || up == SW || up == SE) {
                        ohs_up = 1;
                    } else if (up != EMPTY) {
                        eks_up = 1;
                    }

                    if (down == NS || down == NW || down == NE) {
                        ohs_down = 1;
                    } else if (down != EMPTY) {
                        eks_down = 1;
                    }

                    if (left == EN || left == ES || left == WE)
                        ohs_left = 1;
                    else if (left != EMPTY)
                        eks_left = 1;

                    if (right == WE || right == WS || right == WN)
                        ohs_right = 1;
                    else if (right != EMPTY)
                        eks_right = 1;

                    neighbors[i][j] = ohs_up + 2 * ohs_down + 4 * ohs_left
                            + 8 * ohs_right + 16 * eks_up + 32 * eks_down + 64
                            * eks_left + 128 * eks_right;
                }
            }
        }

        for (i = iBegin; i <= iEnd; i++) {
            for (j = jBegin; j <= jEnd; j++) {
                if (neighbors[i][j] != 0) {
                    dl = getAt(i + 1, j - 1);
                    dr = getAt(i + 1, j + 1);
                    ur = getAt(i - 1, j + 1);
                    ul = getAt(i - 1, j - 1);
                    rr = getAt(i, j + 2);
                    dd = getAt(i + 2, j);
                    switch (neighbors[i][j]) {
                        case 1: {
                            if (ur == SW || ur == SE || ur == SN)
                                dirlist[i][j + 1][0] = true;
                            if (dr == NS || dr == NW || dr == NE)
                                dirlist[i][j + 1][1] = true;
			    if (rr == WS || rr == WE || rr == WN)
				dirlist[i][j + 1][2] = true;
                            if (dr == WN || dr == WS || dr == WE)
                                dirlist[i + 1][j][1] = true;
                            if (dl == EW || dl == ES || dl == ES)
                                dirlist[i + 1][j][0] = true;
                            break;
                        }
                        case 2: {
                            if (dr == NS || dr == NW || dr == NE)
                                dirlist[i][j + 1][1] = true;
                            if (ur == SW || ur == SE || ur == SN)
                                dirlist[i][j + 1][0] = true;
                            break;
                        }
                        case 4: {
                            if (dl == ES || dl == EN || dl == EW)
                                dirlist[i + 1][j][0] = true;
                            if (dr == WN || dr == WS || dr == WE)
                                dirlist[i + 1][j][1] = true;
                            if (ur == SW || ur == SN || ur == SE)
                                dirlist[i][j + 1][0] = true;
                            if (dr == NS || dr == NE || dr == NW)
                                dirlist[i][j + 1][1] = true;
                            if (dd == NW || dd == NE || dd == NS)
                                dirlist[i + 1][j][2] = true;
                            break;
                        }
                        case 8: {
                            if (dl == ES || dl == EN || dl == EW)
                                dirlist[i + 1][j][0] = true;
                            if (dr == WN || dr == WE || dr == WS)
                                dirlist[i + 1][j][1] = true;
			    if (dd == NW || dd == NS || dd == NE)
				dirlist[i + 1][j][2] = true;
                            break;
                        }
                        case 16: {
                            if (ur == NW || ur == NE || ur == WE)
                                dirlist[i][j + 1][0] = true;
                            if (dr == SW || dr == SE || dr == WE)
                                dirlist[i][j + 1][1] = true;
			    if (rr == NE || rr == NS || rr == SE) 
				dirlist[i][j + 1][2] = true;
                            if (dr == SE || dr == SN || dr == EN)
                                dirlist[i + 1][j][1] = true;
                            if (dl == NW || dl == NS || dl == WS)
                                dirlist[i + 1][j][0] = true;
                            break;
                        }
                        case 18:
                        case 33: {
                            if (rr != EMPTY) dirlist[i][j + 1][2] = true;
                            if (dr != EMPTY || ur != EMPTY) {
                                dirlist[i][j + 1][1] = true;
                                dirlist[i][j + 1][0] = true;
                            }
                            dirlist[i][j][2] = true;
                            break;
                        }
                        case 20:
                            if (rr != EMPTY)
                                dirlist[i][j + 1][2] = true;
                            dirlist[i + 1][j][0] = true;
                            dirlist[i][j + 1][0] = true;
                            dirlist[i][j][0] = true;
			    if (dd == NE || dd == NW || dd == NS) 
				dirlist[i+1][j][2] = true;
                            break;
                        case 65: {
                            if (rr != EMPTY)
                                dirlist[i][j + 1][2] = true;
                            dirlist[i + 1][j][0] = true;
                            dirlist[i][j + 1][0] = true;
                            dirlist[i][j][0] = true;
			    if (dd == SW || dd == SE || dd == WE) 
				dirlist[i+1][j][2] = true;
                            break;
                        }
                        case 24:
                        case 129: {
                            dirlist[i + 1][j][1] = true;
                            dirlist[i][j][1] = true;
                            break;
                        }
                        case 32: {
                            if (dr == SE || dr == SW || dr == EW)
                                dirlist[i][j + 1][1] = true;
                            if (ur == NW || ur == NE || ur == WE)
                                dirlist[i][j + 1][0] = true;
                            break;
                        }
                        case 36: {
                            dirlist[i][j + 1][1] = true;
                            dirlist[i][j][1] = true;
                            break;
                        }
                        case 40:
                        case 130: {
                            dirlist[i][j][0] = true;
                            break;
                        }
                        case 64: {
                            if (dl == WN || dl == WS || dl == NS)
                                dirlist[i + 1][j][0] = true;
                            if (dr == EN || dr == ES || dr == NS)
                                dirlist[i + 1][j][1] = true;
                            if (ur == NW || ur == NE || ur == WE)
                                dirlist[i][j + 1][0] = true;
                            if (dr == SE || dr == SW || dr == EW)
                                dirlist[i][j + 1][1] = true;
                            if (dd == SW || dd == SE || dd == WE)
                                dirlist[i + 1][j][2] = true;
                            break;
                        }
                        case 66: {
                            dirlist[i][j + 1][1] = true;
                            dirlist[i][j][1] = true;
                            break;
                        }
                        case 72:
                        case 132: {
                            if (dl != EMPTY || dr != EMPTY) {
                                dirlist[i + 1][j][0] = true;
                                dirlist[i + 1][j][1] = true;
                            }
                            if (dd != EMPTY) dirlist[i + 1][j][2] = true;
                            dirlist[i][j][2] = true;
                            break;
                        }
                        case 128: {
                            if (dl == WS || dl == WN || dl == SN)
                                dirlist[i + 1][j][0] = true;
                            if (dr == EN || dr == ES || dr == NS)
                                dirlist[i + 1][j][1] = true;
                            break;
                        }
                        default:
                            // This should never happen
                            throw new RuntimeException("This should never happen.");
                    }
                }
            }
        }

        if (remove_mirror_moves) {
	    // remove left-right symmetry moves
	    if (lrsym && getColSize() % 2 == 1) {
		for (i = iBegin; i <= iEnd; i++) {
		    dirlist[i][jEnd][0] = true;
		}
	    }
	    // remove up-down symmetry moves
	    if (udsym && getRowSize() % 2 == 1) {
		for (j = jBegin; j <= jEnd; j++) {
		    dirlist[iEnd][j][1] = true;
		}
	    }
        }

        // collects the moves
        for (i = iBegin; i <= iEnd; i++) {
            for (j = jBegin; j <= jEnd; j++) {
                // remove rotation symmetry moves
                if (rsym && getRowSize() % 2 == 1) {
                    int jMiddle = (getColSize() + 1) / 2;
                    if (j > jMiddle && i == iEnd) {
                        continue;
                    }
                }
                if (neighbors[i][j] != 0) {
                    ohs_up = 0;
                    ohs_down = 0;
                    ohs_right = 0;
                    ohs_left = 0;
                    eks_up = 0;
                    eks_down = 0;
                    eks_right = 0;
                    eks_left = 0;
                    up = getAt(i - 1, j);
                    down = getAt(i + 1, j);
                    left = getAt(i, j - 1);
                    right = getAt(i, j + 1);

                    if (up == SN || up == SW || up == SE)
                        ohs_up = 1;
                    else if (up != EMPTY)
                        eks_up = 1;
                    if (down == NS || down == NW || down == NE)
                        ohs_down = 1;
                    else if (down != EMPTY)
                        eks_down = 1;
                    if (left == EN || left == ES || left == WE)
                        ohs_left = 1;
                    else if (left != EMPTY)
                        eks_left = 1;
                    if (right == WE || right == WS || right == WN)
                        ohs_right = 1;
                    else if (right != EMPTY)
                        eks_right = 1;

                    if (!dirlist[i][j][0]) {
                        saveState();
                        if ((ohs_up + ohs_left > 0)
                                || (eks_right + eks_down > 0))
                            putAt(i, j, NW);
                        if ((eks_up + eks_left > 0)
                                || (ohs_right + ohs_down > 0))
                            putAt(i, j, SE);
                        if (forcedMove(i - 1, j) && forcedMove(i + 1, j)
                                && forcedMove(i, j - 1) && forcedMove(i, j + 1)) {
                            Moves.add(col_row_array[j][i] + "/");
                        }
                        restoreState();
                    }
                    if (!dirlist[i][j][1]) {
                        saveState();
                        if ((ohs_up + ohs_right > 0)
                                || (eks_left + eks_down > 0))
                            putAt(i, j, NE);
                        if ((eks_up + eks_right > 0)
                                || (ohs_left + ohs_down > 0))
                            putAt(i, j, SW);
                        if (forcedMove(i - 1, j) && forcedMove(i + 1, j)
                                && forcedMove(i, j - 1) && forcedMove(i, j + 1)) {
                            Moves.add(col_row_array[j][i] + "\\");
                        }
                        restoreState();
                    }
                    if (!dirlist[i][j][2]) {
                        saveState();
                        if ((ohs_up + ohs_down > 0)
                                || (eks_left + eks_right > 0))
                            putAt(i, j, NS);
                        if ((eks_up + eks_down > 0)
                                || (ohs_left + ohs_right > 0))
                            putAt(i, j, WE);
                        if (forcedMove(i - 1, j) && forcedMove(i + 1, j)
                                && forcedMove(i, j - 1) && forcedMove(i, j + 1)) {
                            Moves.add(col_row_array[j][i] + "+");
                        }
                        restoreState();
                    }
                }
            }
        }
        Moves.trimToSize();
        return Moves;
    }

    private boolean checkLine(int row, int col, char direction, char type)
    {
        return checkLine(0,0,false,row,col,direction,type);
    }

    private boolean checkLine(int searchTileRow, int searchTileCol, boolean winner, int row, int col, char direction, char type)
    {
        // type can be _h_orizontal , _v_ertical, _l_oop

        int start_row = row;
        int start_col = col;
        int ix = 0;
        String newdir;
        boolean tileFound = false;

        newdir = " uurllr" // 'u' 1.. 6
                + "ddlrrl" // 'd' 7..12
                + "llduud" // 'l' 13..18
                + "rruddu"; // 'r' 19..24

        if ((searchTileRow==row) && (searchTileCol==col)) {
            tileFound=true;
        }
        for (;;) {
            if (isBlank(row, col))
                return false; // no line starts with a empty space
            // or we are out of range
            switch (direction) {
                case 'u':
                    // newdir's first line
                    ix = 0;
                    break;
                case 'd':
                    // newdir's second line
                    ix = 6;
                    break;
                case 'l':
                    // newdir's third line
                    ix = 12;
                    break;
                case 'r':
                    // newdir's fourth line
                    ix = 18;
                    break;
            }
            ix += getAt(row, col);
            direction = newdir.charAt(ix);
            switch (direction) {
                case 'u':
                    row--;
                    break;
                case 'd':
                    row++;
                    break;
                case 'l':
                    col--;
                    break;
                case 'r':
                    col++;
                    break;
            }
            if ((searchTileRow==row) && (searchTileCol==col)) {
                tileFound=true;
            }
            if ((type == 'h') && (col == 9)) {
                return !winner || tileFound;
            }
            if ((type == 'v') && (row == 9)) {
                return !winner || tileFound;
            }
            if ((row == start_row) && (col == start_col)) {
                return type == 'l' && (!winner || tileFound);
            }
        }
    }

    private boolean isLeftRightMirror()
    {
        int piece, i, j, j2;

        for (i = 1; i <= getRowSize(); i++) {
            j2 = getColSize();
            for (j = 1; j <= ((getColSize() + 1) / 2); j++) {
                piece = getAt(i, j);
                switch (getAt(i, j2)) {
                    case NW:
                        if (piece != NE)
                            return false;
                        break;
                    case NE:
                        if (piece != NW)
                            return false;
                        break;
                    case SW:
                        if (piece != SE)
                            return false;
                        break;
                    case SE:
                        if (piece != SW)
                            return false;
                        break;
                    case NS:
                        if (piece != NS)
                            return false;
                        break;
                    case WE:
                        if (piece != WE)
                            return false;
                        break;
                    case EMPTY:
                        if (piece != EMPTY)
                            return false;
                        break;
                }
                j2--;
            }
        }
        return true;
    }

    private boolean isRightLeftMirror() { return isLeftRightMirror(); }

    private boolean isUpDownMirror()
    {
        int piece, i, j, i2;

        i2 = getRowSize();
        for (i = 1; i <= ((getRowSize() + 1) / 2); i++) {
            for (j = 1; j <= getColSize(); j++) {
                piece = getAt(i, j);
                switch (getAt(i2, j)) {
                    case NW:
                        if (piece != SW)
                            return false;
                        break;
                    case NE:
                        if (piece != SE)
                            return false;
                        break;
                    case SW:
                        if (piece != NW)
                            return false;
                        break;
                    case SE:
                        if (piece != NE)
                            return false;
                        break;
                    case NS:
                        if (piece != NS)
                            return false;
                        break;
                    case WE:
                        if (piece != WE)
                            return false;
                        break;
                    case EMPTY:
                        if (piece != EMPTY)
                            return false;
                        break;
                }
            }
            i2--;
        }
        return true;
    }

    private boolean isDownUpMirror() { return isUpDownMirror(); }

    // 90 degree rotation
    private boolean isRotateMirror()
    {
        int i, j, piece, i2, j2;

        i2 = getRowSize();
        for (i = 1; i <= ((getRowSize() + 1) / 2); i++) {
            j2 = getColSize();
            for (j = 1; j <= getColSize(); j++) {
                piece = getAt(i, j);
                switch (getAt(i2, j2)) {
                    case NW:
                        if (piece != SE)
                            return false;
                        break;
                    case NE:
                        if (piece != SW)
                            return false;
                        break;
                    case SW:
                        if (piece != NE)
                            return false;
                        break;
                    case SE:
                        if (piece != NW)
                            return false;
                        break;
                    case NS:
                        if (piece != NS)
                            return false;
                        break;
                    case WE:
                        if (piece != WE)
                            return false;
                        break;
                    case EMPTY:
                        if (piece != EMPTY)
                            return false;
                        break;
                }
                j2--;
            }
            i2--;
        }
        return true;
    }

    public int whoToMove() { return wtm; }

    public int whoDidLastMove() {
        if (boardEmpty)
            return NOPLAYER;
        switch (wtm) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            default:
                // This should never happen
                throw new RuntimeException("This should never happen.");
        }
    }

    public int getAt(int row, int col) {
        if ((row < 1) || (row > 8))
            return EMPTY;
        if ((col < 1) || (col > 8))
            return EMPTY;
        return board[firstrow + row - 1][firstcol + col - 1];
    }

    public boolean winnerTile(int row, int col) {
        if (this.isGameOver()==Traxboard.NOPLAYER) return false;
        if (this.isGameOver()==Traxboard.DRAW) return false;

        if (getColSize() == 8) {
            // check left-right line
            for (int r = 1; r <= 8; r++) {
                if (checkLine(row,col,true,r, 1, 'r', 'h')) return true;
            }
        }
        // check up-down line
        if (getRowSize() == 8) {
            for (int c = 1; c <= 8; c++) {
                if (checkLine(row,col,true,1, c, 'd', 'v')) return true;
            }
        }
        for (int i = 1; i < 8; i++) {
            for (int j = 1; j < 8; j++) {
                switch (getAt(i, j)) {
                    case NW:
                    case SE:
                        if (checkLine(row,col,true,i, j, 'u', 'l')) return true;
                        break;
                    case EMPTY:
                    case NS:
                    case WE:
                    case NE:
                    case WS:
                        break;
                    default:
					/* This should never happen */
                        throw new RuntimeException(
                                "This should never happen.");
                }
            }
        }

        return false;
    }


    private void putAt(int row, int col, int piece) {
        if (piece == EMPTY) {
            if (board[firstrow + row - 1][firstcol + col - 1] != EMPTY)
                num_of_tiles--;
            board[firstrow + row - 1][firstcol + col - 1] = piece;
            return;
        } else {
            if (boardEmpty) {
                boardEmpty = false;
                firstrow = 7;
                firstcol = 7;
                lastrow = 7;
                lastcol = 7;
                num_of_tiles = 1;
                board[firstrow][firstcol] = piece;
                return;
            }
            if (row == 0) {
                assert (firstrow > 0);
                firstrow--;
                row++;
            }
            if (col == 0) {
                assert (firstcol > 0);
                firstcol--;
                col++;
            }
            if (row > getRowSize()) {
                lastrow += row - getRowSize();
            }
            if (col > getColSize()) {
                lastcol += col - getColSize();
            }
            num_of_tiles++;
        }
        board[firstrow + row - 1][firstcol + col - 1] = piece;
    }

    private boolean canMoveDown() { return (getRowSize() < 8); }
    private boolean canMoveRight() { return (getColSize() < 8); }

    private boolean forcedMove(int brow, int bcol)
    {
        if (!isBlank(brow, bcol)) return true;
        if ((brow < 1) || (brow > 8) || (bcol < 1) || (bcol > 8)) return true;

        int up = getAt(brow - 1, bcol);
        int down = getAt(brow + 1, bcol);
        int left = getAt(brow, bcol - 1);
        int right = getAt(brow, bcol + 1);

        // boolean result=true;
        int neighbors = 0;

        if (!blank(up)) neighbors++;
        if (!blank(down)) neighbors++;
        if (!blank(left)) neighbors++;
        if (!blank(right)) neighbors++;

        if (neighbors < 2) return true; // Less than two pieces bordering

        int white_up = 0, black_up = 0, white_down = 0, black_down = 0, white_left = 0, black_left = 0, white_right = 0, black_right = 0;

        if (up == SN || up == SW || up == SE) white_up = 1;
        if (up == WE || up == NW || up == NE) black_up = 1;
        if (down == NS || down == NW || down == NE) white_down = 1;
        if (down == WE || down == SW || down == SE) black_down = 1;
        if (left == EW || left == EN || left == ES) white_left = 1;
        if (left == NS || left == NW || left == SW) black_left = 1;
        if (right == WE || right == WN || right == WS) white_right = 1;
        if (right == NS || right == NE || right == SE) black_right = 1;

        int white = white_up + white_down + white_left + white_right;
        int black = black_up + black_down + black_left + black_right;

        if ((white > 2) || (black > 2)) { return false; } // Illegal filled cave
        if ((white < 2) && (black < 2)) { return true; } // Done

        int piece = EMPTY;
        if (white == 2) {
            switch (white_up + 2 * white_down + 4 * white_left + 8 * white_right) {
                case 3:
                    piece = NS;
                    break;
                case 12:
                    piece = WE;
                    break;
                case 5:
                    piece = NW;
                    break;
                case 9:
                    piece = NE;
                    break;
                case 6:
                    piece = WS;
                    break;
                case 10:
                    piece = SE;
                    break;
            }
        } else { // right==2
            switch (black_up + 2 * black_down + 4 * black_left + 8 * black_right) {
                case 12:
                    piece = NS;
                    break;
                case 3:
                    piece = WE;
                    break;
                case 10:
                    piece = NW;
                    break;
                case 6:
                    piece = NE;
                    break;
                case 9:
                    piece = WS;
                    break;
                case 5:
                    piece = SE;
                    break;
            }
        }
        putAt(brow, bcol, piece);
        if (!forcedMove(brow - 1, bcol)) { return false; }
        if (!forcedMove(brow + 1, bcol)) { return false; }
        if (!forcedMove(brow, bcol - 1)) { return false; }
        return forcedMove(brow, bcol + 1);
    }

    private void updateLine(char colour, char entry, int row, int col)
    {
        int theNum;

        while (true) {
            theNum = 0;
            if (colour == 'w') theNum = 1024;
            switch (entry) {
                case 'w':
                    theNum += 512;
                    break;
                case 'e':
                    theNum += 256;
                    break;
                case 's':
                    theNum += 128;
                    break;
                case 'n':
                    theNum += 64;
                    break;
                default:
                    // This should never happen
                    throw new RuntimeException("This should never happen.");
            }
            switch (getAt(row, col)) {
                case NS:
                    theNum += 32;
                    break;
                case WE:
                    theNum += 16;
                    break;
                case NW:
                    theNum += 8;
                    break;
                case NE:
                    theNum += 4;
                    break;
                case SW:
                    theNum += 2;
                    break;
                case SE:
                    theNum += 1;
                    break;
                default:
                    // This should never happen
                    throw new RuntimeException("This should never happen.");
            }
            switch (theNum) {
                case 1024 + 512 + 16:
                    if (getAt(row, col + 1) == EMPTY) return;
                    col++;
                    break;
                case 1024 + 512 + 8:
                    if (getAt(row - 1, col) == EMPTY) return;
                    row--;
                    entry = 's';
                    break;
                case 1024 + 512 + 2:
                    if (getAt(row + 1, col) == EMPTY) return;
                    row++;
                    entry = 'n';
                    break;
                case 1024 + 256 + 16:
                    if (getAt(row, col - 1) == EMPTY) return;
                    col--;
                    break;
                case 1024 + 256 + 4:
                    if (getAt(row - 1, col) == EMPTY) return;
                    row--;
                    entry = 's';
                    break;
                case 1024 + 256 + 1:
                    if (getAt(row + 1, col) == EMPTY) return;
                    row++;
                    entry = 'n';
                    break;
                case 1024 + 128 + 32:
                    if (getAt(row - 1, col) == EMPTY) return;
                    row--;
                    break;
                case 1024 + 128 + 2:
                    if (getAt(row, col - 1) == EMPTY) return;
                    col--;
                    entry = 'e';
                    break;
                case 1024 + 128 + 1:
                    if (getAt(row, col + 1) == EMPTY) return;
                    col++;
                    entry = 'w';
                    break;
                case 1024 + 64 + 32:
                    if (getAt(row + 1, col) == EMPTY) return;
                    row++;
                    break;
                case 1024 + 64 + 8:
                    if (getAt(row, col - 1) == EMPTY) return;
                    col--;
                    entry = 'e';
                    break;
                case 1024 + 64 + 4:
                    if (getAt(row, col + 1) == EMPTY) return;
                    col++;
                    entry = 'w';
                    break;
                case 512 + 32:
                    if (getAt(row, col + 1) == EMPTY) return;
                    col++;
                    break;
                case 512 + 4:
                    if (getAt(row + 1, col) == EMPTY) return;
                    row++;
                    entry = 'n';
                    break;
                case 512 + 1:
                    if (getAt(row - 1, col) == EMPTY) return;
                    row--;
                    entry = 's';
                    break;
                case 256 + 32:
                    if (getAt(row - 1, col) == EMPTY) return;
                    row--;
                    break;
                case 256 + 8:
                    if (getAt(row, col + 1) == EMPTY) return;
                    row++;
                    entry = 'n';
                    break;
                case 256 + 2:
                    if (getAt(row - 1, col) == EMPTY) return;
                    row--;
                    entry = 's';
                    break;
                case 128 + 16:
                    if (getAt(row - 1, col) == EMPTY) return;
                    row--;
                    break;
                case 128 + 8:
                    if (getAt(row, col + 1) == EMPTY) return;
                    col++;
                    entry = 'w';
                    break;
                case 128 + 4:
                    if (getAt(row, col - 1) == EMPTY) return;
                    col--;
                    entry = 'e';
                    break;
                case 64 + 16:
                    if (getAt(row + 1, col) == EMPTY) return;
                    row++;
                    break;
                case 64 + 2:
                    if (getAt(row, col + 1) == EMPTY) return;
                    col++;
                    entry = 'w';
                    break;
                case 64 + 1:
                    if (getAt(row, col - 1) == EMPTY) return;
                    col--;
                    entry = 'e';
                    break;
                default:
				/* This should never happen */
                    throw new RuntimeException("This should never happen.");
            }
        }
    }

    public String getBorder() { return this.getBorder(false); }
    public String getBorder(boolean needNumbers)
    {
        if (border!=null) return border;
        String result = "";
        char[] dummy = new char[4];
        int i, j, k, starti, startj, icopy, jcopy;
        char direction;
        int[][] wnum = new int[9][9]; // every white line gets a number
        int[][] bnum = new int[9][9];
        int currentWNum = 1;
        int currentBNum = 1;

        if (whoDidLastMove() == NOPLAYER) {
            border="";
            return border;
        }
        for (i = 1; i < 9; i++) {
            for (j = 1; j < 9; j++) {
                for (k = 0; k < 4; k++) {
                    wnum[i][j] = 0;
                    bnum[i][j] = 0;
                }
            }
        }
        starti = 1;
        startj = 1;
        while (getAt(starti, startj) == EMPTY)
            starti++;

        direction = 'd';
        i = starti;
        j = startj;
        while (true) {
            switch (direction) {
                case 'd':
                    switch (getAt(i, j)) {
                        case NW:
                        case SW:
                        case WE:
                            result += 'W';
                            break;
                        case NS:
                        case NE:
                        case SE:
                            result += 'B';
                            break;
                        default:
					/* This should never happen */
                            throw new RuntimeException(
                                    "This should never happen.");
                    }
                    if (getAt(i + 1, j - 1) != EMPTY) {
                        direction = 'l';
                        result += '+';
                        i++;
                        j--;
                        break;
                    }
                    if (getAt(i + 1, j) == EMPTY) {
                        direction = 'r';
                        result += '-';
                        break;
                    }
                    i++;
                    break;
                case 'u':
                    switch (getAt(i, j)) {
                        case EW:
                        case NE:
                        case ES:
                            result += 'W';
                            break;
                        case WS:
                        case SN:
                        case NW:
                            result += 'B';
                            break;
                        default:
					/* This should never happen */
                            throw new RuntimeException(
                                    "This should never happen.");
                    }
                    if (getAt(i - 1, j + 1) != EMPTY) {
                        direction = 'r';
                        result += '+';
                        i--;
                        j++;
                        break;
                    }
                    if (getAt(i - 1, j) == EMPTY) {
                        direction = 'l';
                        result += '-';
                        break;
                    }
                    i--;
                    break;
                case 'l':
                    switch (getAt(i, j)) {
                        case NW:
                        case SN:
                        case NE:
                            result += 'W';
                            break;
                        case WE:
                        case SE:
                        case SW:
                            result += 'B';
                            break;
                        default:
					/* This should never happen */
                            throw new RuntimeException(
                                    "This should never happen.");
                    }
                    if (getAt(i - 1, j - 1) != EMPTY) {
                        direction = 'u';
                        result += '+';
                        i--;
                        j--;
                        break;
                    }
                    if (getAt(i, j - 1) == EMPTY) {
                        if ((i == starti) && (j == startj)) {
                            border=result;
                            return result;
                        }
                        result += '-';
                        direction = 'd';
                        break;
                    }
                    j--;
                    break;
                case 'r':
                    switch (getAt(i, j)) {
                        case NS:
                        case SE:
                        case SW:
                            result += 'W';
                            break;
                        case NE:
                        case NW:
                        case WE:
                            result += 'B';
                            break;
                        default:
					/* This should never happen */
                            throw new RuntimeException(
                                    "This should never happen.");
                    }
                    if (getAt(i + 1, j + 1) != EMPTY) {
                        direction = 'd';
                        result += '+';
                        i++;
                        j++;
                        break;
                    }
                    if (getAt(i, j + 1) == EMPTY) {
                        direction = 'u';
                        result += '-';
                        break;
                    }
                    j++;
                    break;
                default:
				/* This should never happen */
                    throw new RuntimeException("This should never happen.");
            }
        }
    }



    private boolean checkThreatPosition(int row, int col, char direction, int west, int east, int north, int south) {
        // Check if the threat is working. left, right, up, down based on going N

        if (DEBUG) {
            System.err.println("checkThreatPosition:row="+row+", col="+col+", direction="+direction);
            System.err.println("checkThreatPosition:west="+west+", east="+east+", north="+north+", south="+south);
        }
        switch (direction) {
            case 'N':
                if (getRowSize()+south>8 || getRowSize()+north>8) {
                    if (row+south>8) return false;
                    if (row-north<1) return false;
                }
                if (getColSize()+east>8 || getColSize()+west>8) {
                    if (col+east>8) return false;
                    if (col-west<1) return false;
                }
                break;
            case 'S':
                if (getRowSize()+south>8 || getRowSize()+north>8) {
                    if (row-south<1) return false;
                    if (row+north>8) return false;
                }
                if (getColSize()+east>8 || getColSize()+west>8) {
                    if (col-east<1) return false;
                    if (col+west>8) return false;
                }
                break;
            case 'W':
                if (getRowSize()+west>8  || getRowSize()+east>8) {
                    if (row+west>8) return false;
                    if (row-east<1) return false;
                }
                if (getColSize()+south>8 || getColSize()+north>8) {
                    if (col+south>8) return false;
                    if (col-north<1) return false;
                }
                break;
            case 'E':
                if (getRowSize()+west>8 || getRowSize()+east>8) {
                    if (row-west<1) return false;
                    if (row+east>8) return false;
                }
                if (getColSize()+south>8 || getColSize()+north>8) {
                    if (col-south<1) return false;
                    if (col+north>8) return false;
                }
                break;
            default:
                // This should never happen
                throw new RuntimeException("This should never happen.");
        }
        return true;
    }


    private void countCornersSimple() {
        switch (num_of_tiles) {
            case 0:
                blackCorners=0; whiteCorners=0;
                return;
            case 1:
                if (getAt(1,1)==Traxboard.NS) {
                    blackCorners=0; whiteCorners=0;
                    return;
                }
                blackCorners=1; whiteCorners=1;
                return;
            default:
                // This should never happen
                throw new RuntimeException("This should never happen.");
        }
    }

    private void checkThreat(int row, int col, char direction, String threat) {
        if (DEBUG) System.err.println("checkThreat: row="+row+", col="+col+", direction="+direction+", threat="+threat);
        switch (threat) {
            case "W+W-BB-W+W":
            case "W+W-BBB-W+W":
                if (!checkThreatPosition(row,col,direction,1,0,0,0)) break;
                whiteCorners++;
                whiteThreats++;
                break;
            case "B+B-WW-B+B":
            case "B+B-WWW-B+B":
                if (!checkThreatPosition(row,col,direction,1,0,0,0)) break;
                blackCorners++;
                blackThreats++;
                break;
            case "BW-B+WWW+B-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'N':
                        if (getAt(row,col)!=Traxboard.NW) break;
                        blackCorners++;
                        if (getAt(row+4,col)==Traxboard.WE) blackThreats++;
                        break;
                    case 'S':
                        if (getAt(row,col)!=Traxboard.SW) break;
                        blackCorners++;
                        if (getAt(row-4,col)==Traxboard.WE) blackThreats++;
                        break;
                    case 'W':
                        if (getAt(row,col)!=Traxboard.SW) break;
                        blackCorners++;
                        if (getAt(row,col+4)==Traxboard.NS) blackThreats++;
                        break;
                    case 'E':
                        if (getAt(row,col)!=Traxboard.NE) break;
                        blackCorners++;
                        if (getAt(row,col-4)==Traxboard.NS) blackThreats++;
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-W+BBB+W-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'N':
                        if (getAt(row,col)!=Traxboard.SE) break;
                        whiteCorners++;
                        if (getAt(row+4,col)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'S':
                        if (getAt(row,col)!=Traxboard.NW) break;
                        whiteCorners++;
                        if (getAt(row-4,col)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'W':
                        if (getAt(row,col)!=Traxboard.NE) break;
                        whiteCorners++;
                        if (getAt(row,col+4)==Traxboard.WE) whiteThreats++;
                        break;
                    case 'E':
                        if (getAt(row,col)!=Traxboard.SW) break;
                        whiteCorners++;
                        if (getAt(row,col-4)==Traxboard.WE) whiteThreats++;
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-B+WWW+B-WB":
            case "B-B+WW+B-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)!=Traxboard.SE) break;
                        blackCorners++;
                        blackThreats++;
                        break;
                    case 'N':
                        if (getAt(row,col)!=Traxboard.NW) break;
                        blackCorners++;
                        blackThreats++;
                        break;
                    case 'W':
                        if (getAt(row,col)!=Traxboard.SW) break;
                        blackCorners++;
                        blackThreats++;
                        break;
                    case 'E':
                        if (getAt(row,col)!=Traxboard.NE) break;
                        blackCorners++;
                        blackThreats++;
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-W+BB+W-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                switch (direction) {
                    case 'N':
                        if (getAt(row+3,col)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'S':
                        if (getAt(row-3,col)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'W':
                        if (getAt(row,col+3)==Traxboard.WE) whiteThreats++;
                        break;
                    case 'E':
                        if (getAt(row,col-3)==Traxboard.WE) whiteThreats++;
                        break;
                }
                break;
            case "WB-W+BBB+W-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                switch (direction) {
                    case 'N':
                        if (getAt(row+4,col)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'S':
                        if (getAt(row-4,col)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'W':
                        if (getAt(row,col+4)==Traxboard.WE) whiteThreats++;
                        break;
                    case 'E':
                        if (getAt(row,col+4)==Traxboard.WE) whiteThreats++;
                        break;
                }
                break;
            case "W-W+BB+W-W":
            case "W-W+BBB+W-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                whiteThreats++;
                break;
            case "W-W+BB+W-BW":
            case "W-W+BBB+W-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)!=Traxboard.SE) break;
                        whiteCorners++;
                        whiteThreats++;
                        break;
                    case 'N':
                        if (getAt(row,col)!=Traxboard.NW) break;
                        whiteCorners++;
                        whiteThreats++;
                        break;
                    case 'W':
                        if (getAt(row,col)!=Traxboard.SW) break;
                        whiteCorners++;
                        whiteThreats++;
                        break;
                    case 'E':
                        if (getAt(row,col)!=Traxboard.NE) break;
                        whiteCorners++;
                        whiteThreats++;
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-W+BB+W-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'N':
                        if (getAt(row,col)!=Traxboard.SE) break;
                        whiteCorners++;
                        if (getAt(row+4,col)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'S':
                        if (getAt(row,col)!=Traxboard.NW) break;
                        whiteCorners++;
                        if (getAt(row-4,col)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'W':
                        if (getAt(row,col)!=Traxboard.NE) break;
                        whiteCorners++;
                        if (getAt(row,col+4)==Traxboard.WE) whiteThreats++;
                        break;
                    case 'E':
                        if (getAt(row,col)!=Traxboard.SW) break;
                        whiteCorners++;
                        if (getAt(row-4,col)==Traxboard.WE) whiteThreats++;
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-B+WW+B-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'N':
                        if (getAt(row,col)!=Traxboard.NW) break;
                        blackCorners++;
                        if (getAt(row+4,col)==Traxboard.WE) blackThreats++;
                        break;
                    case 'S':
                        if (getAt(row,col)!=Traxboard.SE) break;
                        blackCorners++;
                        if (getAt(row-4,col)==Traxboard.WE) blackThreats++;
                        break;
                    case 'W':
                        if (getAt(row,col)!=Traxboard.SW) break;
                        blackCorners++;
                        if (getAt(row,col+4)==Traxboard.NS) blackThreats++;
                        break;
                    case 'E':
                        if (getAt(row,col)!=Traxboard.NE) break;
                        blackCorners++;
                        if (getAt(row-4,col)==Traxboard.NS) blackThreats++;
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-B+WWW+B-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                blackCorners++;
                switch (direction) {
                    case 'N':
                        if (getAt(row+4,col)==Traxboard.WE) blackThreats++;
                        break;
                    case 'S':
                        if (getAt(row-4,col)==Traxboard.WE) blackThreats++;
                        break;
                    case 'W':
                        if (getAt(row,col+4)==Traxboard.NS) blackThreats++;
                        break;
                    case 'E':
                        if (getAt(row,col+4)==Traxboard.NS) blackThreats++;
                        break;
                }
                break;
            case "BW-B+WW+B-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                blackCorners++;
                switch (direction) {
                    case 'N':
                        if (getAt(row+3,col)==Traxboard.WE) blackThreats++;
                        break;
                    case 'S':
                        if (getAt(row-3,col)==Traxboard.WE) blackThreats++;
                        break;
                    case 'W':
                        if (getAt(row,col+3)==Traxboard.NS) blackThreats++;
                        break;
                    case 'E':
                        if (getAt(row,col-3)==Traxboard.NS) blackThreats++;
                        break;
                }
                break;
            case "B-B+WW+B-B":
            case "B-B+WWW+B-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                blackCorners++;
                blackThreats++;
                break;
            case "WB-BWBBWB-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) { break; }
                switch (direction) {
                    case 'N':
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (checkThreatPosition(row,col,direction,6,1,0,1)) { whiteThreats++; }
                        }
                        break;
                    case 'E':
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (checkThreatPosition(row,col,direction,6,1,0,1)) { whiteThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-BWBBBWB-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) { break; }
                switch (direction) {
                    case 'N':
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (checkThreatPosition(row,col,direction,7,1,0,1)) { whiteThreats++; }
                        }
                        break;
                    case 'E':
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (checkThreatPosition(row,col,direction,7,1,0,1)) { whiteThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-WBBBWB-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                switch (direction) {
                    case 'N':
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (checkThreatPosition(row,col,direction,6,1,0,1)) whiteThreats++;
                        }
                        break;
                    case 'E':
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (checkThreatPosition(row,col,direction,6,1,0,1)) whiteThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-WBBWB-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                switch (direction) {
                    case 'N':
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (checkThreatPosition(row,col,direction,5,1,0,1)) whiteThreats++;
                        }
                        break;
                    case 'E':
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (checkThreatPosition(row,col,direction,5,1,0,1)) whiteThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-B+WWBW-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'E':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (getAt(row-4,col-1)==Traxboard.WE && checkThreatPosition(row,col,direction,0,0,0,2)) { blackThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (getAt(row+4,col+1)==Traxboard.WE && checkThreatPosition(row,col,direction,0,0,0,2)) { blackThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (getAt(row+1,col-4)==Traxboard.NS && checkThreatPosition(row,col,direction,0,0,0,2)) { blackThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (getAt(row-1,col-4)==Traxboard.NS && checkThreatPosition(row,col,direction,0,0,0,2)) { blackThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-W+BBWB-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'E':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (getAt(row-4,col-1)==Traxboard.NS && checkThreatPosition(row,col,direction,0,0,0,2)) { whiteThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (getAt(row+4,col+1)==Traxboard.NS && checkThreatPosition(row,col,direction,0,0,0,2)) { whiteThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (getAt(row+1,col-4)==Traxboard.WE && checkThreatPosition(row,col,direction,0,0,0,2)) { whiteThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (getAt(row-1,col-4)==Traxboard.WE && checkThreatPosition(row,col,direction,0,0,0,2)) { whiteThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-W+BBW-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,0,0,0,2)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+3,col+1)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-3,col-1)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'S':
                        if (getAt(row-1,col+3)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+1,col-3)==Traxboard.WE) { whiteThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-B+WWBW-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) { break; }
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,0,3,0,0)) { break; }
                switch (direction) {
                    case 'W':
                        if (getAt(row+4,col+1)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-4,col-2)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'S':
                        if (getAt(row-2,col+4)==Traxboard.NS) { blackThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+2,col-4)==Traxboard.NS) { blackThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-WBWWBW-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,2)) break;
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,6,1,0,2)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+5,col+1)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-5,col-1)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'S':
                        if (getAt(row-1,col+5)==Traxboard.NS) { blackThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+1,col-5)==Traxboard.NS) { blackThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-B+WBW-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) { break; }
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,0,3,0,0)) { break; }
                switch (direction) {
                    case 'W':
                        if (getAt(row+3,col+2)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-3,col-2)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'S':
                        if (getAt(row-2,col+3)==Traxboard.NS) { blackThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+2,col-3)==Traxboard.NS) { blackThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-WBWWWB-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+5,col)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-5,col)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'S':
                        if (getAt(row,col+5)==Traxboard.NS) { blackThreats++; }
                        break;
                    case 'N':
                        if (getAt(row,col-5)==Traxboard.NS) { blackThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-WBWWB-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,5,1,0,1)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+4,col)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-4,col)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'S':
                        if (getAt(row,col+4)==Traxboard.NS) { blackThreats++; }
                        break;
                    case 'N':
                        if (getAt(row,col-4)==Traxboard.NS) { blackThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-B+WWB-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,0,0,0,2)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+3,col+1)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-3,col-1)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'S':
                        if (getAt(row-1,col+3)==Traxboard.NS) { blackThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+1,col-3)==Traxboard.NS) { blackThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-BWBBW-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,5,1,0,1)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+4,col)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-4,col)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'S':
                        if (getAt(row,col+4)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'N':
                        if (getAt(row,col-4)==Traxboard.WE) { whiteThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-BWBBBW-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+5,col)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-5,col)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'S':
                        if (getAt(row,col+5)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'N':
                        if (getAt(row,col-5)==Traxboard.WE) { whiteThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-W":
                if (checkThreatPosition(row,col,direction,0,1,0,1)) {
                    whiteCorners++;
                }
                break;
            case "WB-BW":
                if (checkThreatPosition(row,col,direction,0,1,0,2)) {
                    whiteCorners++;
                }
                break;
            case "WB-W+BBWB-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) { break; }
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,0,3,0,0)) { break; }
                switch (direction) {
                    case 'S':
                        if (getAt(row-2,col+4)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+2,col-4)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'W':
                        if (getAt(row+4,col+2)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-4,col-2)==Traxboard.NS) { whiteThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-W+BWB-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) { break; }
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,0,3,0,0)) { break; }
                switch (direction) {
                    case 'S':
                        if (getAt(row-2,col+3)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+2,col-3)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'W':
                        if (getAt(row+3,col+2)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-3,col-2)==Traxboard.NS) { whiteThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-BWBBBWB-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,2)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,7,1,0,2)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row-1,col+6)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+1,col-6)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'W':
                        if (getAt(row+6,col+1)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-6,col-1)==Traxboard.WE) { whiteThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-BWBBWB-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,2)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,6,1,0,2)) break;
                whiteCorners++;
                switch (direction) {
                    case 'S':
                        if (getAt(row-1,col+5)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+1,col-5)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'W':
                        if (getAt(row+5,col+1)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-5,col-1)==Traxboard.WE) { whiteThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-W+BWB-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (getAt(row-1,col+3)==Traxboard.WE && checkThreatPosition(row,col,direction,0,0,0,2)) { whiteThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (getAt(row+1,col-3)==Traxboard.WE && checkThreatPosition(row,col,direction,0,0,0,2)) { whiteThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (getAt(row+3,col+1)==Traxboard.NS && checkThreatPosition(row,col,direction,0,0,0,2)) { whiteThreats++; }
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (getAt(row-3,col-1)==Traxboard.NS && checkThreatPosition(row,col,direction,0,0,0,2)) { whiteThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-BWBBBWB-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,7,1,0,1)) break;
                            if (getAt(row,col+6)==Traxboard.WE) { whiteThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,7,1,0,1)) break;
                            if (getAt(row,col-6)==Traxboard.WE) { whiteThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,7,1,0,1)) break;
                            if (getAt(row+6,col)==Traxboard.NS) { whiteThreats++; }
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,7,1,0,1)) break;
                            if (getAt(row-6,col)==Traxboard.NS) { whiteThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-WBWWBW-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) { break; }
                switch (direction) {
                    case 'N':
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (checkThreatPosition(row,col,direction,6,1,0,1)) { blackThreats++; }
                        }
                        break;
                    case 'E':
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (checkThreatPosition(row,col,direction,6,1,0,1)) { blackThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-WBWWWBW-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) { break; }
                switch (direction) {
                    case 'N':
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (checkThreatPosition(row,col,direction,7,1,0,1)) { blackThreats++; }
                        }
                        break;
                    case 'E':
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (checkThreatPosition(row,col,direction,7,1,0,1)) { blackThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-WBWWWBW-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,2)) break;
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,7,1,0,2)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row-1,col+6)==Traxboard.NS) { blackThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+1,col-6)==Traxboard.NS) { blackThreats++; }
                        break;
                    case 'W':
                        if (getAt(row+6,col+1)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-6,col-1)==Traxboard.WE) { blackThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-WBWWWBW-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                switch (direction) {
                    case 'S': {
                        if (getAt(row,col)==Traxboard.NS)
                            blackCorners++;
                        if (!checkThreatPosition(row,col,direction,7,1,0,1)) break;
                        if (getAt(row,col+6)==Traxboard.NS) { blackThreats++; }
                    }
                    break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,7,1,0,1)) break;
                            if (getAt(row,col-6)==Traxboard.NS) { blackThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,7,1,0,1)) break;
                            if (getAt(row+6,col)==Traxboard.WE) { blackThreats++; }
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,7,1,0,1)) break;
                            if (getAt(row-6,col)==Traxboard.WE) { blackThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-WBWWBW-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                switch (direction) {
                    case 'S': {
                        if (getAt(row,col)==Traxboard.NS)
                            blackCorners++;
                        if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                        if (getAt(row,col+5)==Traxboard.NS) { blackThreats++; }
                    }
                    break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                            if (getAt(row,col-5)==Traxboard.NS) { blackThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                            if (getAt(row+5,col)==Traxboard.WE) { blackThreats++; }
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                            if (getAt(row-5,col)==Traxboard.WE) { blackThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-BWBBWB-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                            if (getAt(row,col+5)==Traxboard.WE) { whiteThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                            if (getAt(row,col-5)==Traxboard.WE) { whiteThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                            if (getAt(row+5,col)==Traxboard.NS) { whiteThreats++; }
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                            if (getAt(row-5,col)==Traxboard.WE) { whiteThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-B+W-BWB-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,0)) break;
                            if (getAt(row+3,col-1)==Traxboard.NS) { whiteThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,0)) break;
                            if (getAt(row-1,col-3)==Traxboard.WE) { whiteThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,0)) break;
                            if (getAt(row+1,col+3)==Traxboard.WE)  { whiteThreats++; }
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,0)) break;
                            if (getAt(row-3,col+1)==Traxboard.NS) { whiteThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-WBW+B-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'E':
                        if (getAt(row,col)==Traxboard.NE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,5)) break;
                            if (getAt(row-1,col-4)==Traxboard.NS) { blackThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.SW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,5)) break;
                            if (getAt(row+1,col+4)==Traxboard.NS) { blackThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.NW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,5)) break;
                            if (getAt(row+4,col-1)==Traxboard.WE) { blackThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col)==Traxboard.SE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,5)) break;
                            if (getAt(row-4,col+1)==Traxboard.WE) { blackThreats++; }
                        }
                        break;
                }
                break;
            case "B-WBWW+B-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'E':
                        if (getAt(row,col)==Traxboard.NE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            if (getAt(row-1,col-5)==Traxboard.NS) { blackThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.SW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            if (getAt(row+1,col+5)==Traxboard.NS) { blackThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.NW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            if (getAt(row+5,col-1)==Traxboard.WE) { blackThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col)==Traxboard.SE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            if (getAt(row-5,col+1)==Traxboard.WE) { blackThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen");
                }
                break;
            case "W-BWBB+W-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'E':
                        if (getAt(row,col)==Traxboard.SW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            if (getAt(row-1,col-5)==Traxboard.WE) { whiteThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.NE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            if (getAt(row+1,col+5)==Traxboard.WE) { whiteThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.SE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            if (getAt(row+5,col-1)==Traxboard.NS) { whiteThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col)==Traxboard.NW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            if (getAt(row-5,col+1)==Traxboard.NS) { whiteThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-BWB+W-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'E':
                        if (getAt(row,col)==Traxboard.SW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,5)) break;
                            if (getAt(row-1,col-4)==Traxboard.WE) { whiteThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.NE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,5)) break;
                            if (getAt(row+1,col+4)==Traxboard.WE) { whiteThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.SE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,5)) break;
                            if (getAt(row+4,col-1)==Traxboard.NS) { whiteThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col)==Traxboard.NW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,5)) break;
                            if (getAt(row-4,col+1)==Traxboard.NS) { whiteThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-WB+W-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)==Traxboard.NW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.SE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.SW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.NE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                            whiteThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-WBB+W-BW":
            case "WB-BWB+W-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)==Traxboard.NW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,5)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.SE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,5)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.SW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,5)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.NE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,5)) break;
                            whiteThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-BWBB+W-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)==Traxboard.NW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.SE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.SW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.NE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            whiteThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-BW":
                if (checkThreatPosition(row,col,direction,0,1,0,1)) {
                    switch (direction) {
                        case 'S':
                            if (getAt(row,col)==Traxboard.NW) { whiteCorners++; }
                            break;
                        case 'N':
                            if (getAt(row,col)==Traxboard.SE) { whiteCorners++; }
                            break;
                        case 'E':
                            if (getAt(row,col)==Traxboard.SW) { whiteCorners++; }
                            break;
                        case 'W':
                            if (getAt(row,col)==Traxboard.NE) { whiteCorners++; }
                            break;
                        default:
                            // This should never happen
                            throw new RuntimeException("This should never happen.");
                    }
                }
                break;
            case "W-W+BWB-W":
            case "W-W+BBWB-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                    case 'N':
                        if (getAt(row,col)==Traxboard.WE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,2)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'E':
                    case 'W':
                        if (getAt(row,col)==Traxboard.NS) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,2)) break;
                            whiteThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-W":
                if (checkThreatPosition(row,col,direction,0,1,0,1)) {
                    switch (direction) {
                        case 'S':
                        case 'N':
                            if (getAt(row,col)==Traxboard.WE) { whiteCorners++; }
                            break;
                        case 'E':
                        case 'W':
                            if (getAt(row,col)==Traxboard.NS) { whiteCorners++; }
                            break;
                        default:
                            // This should never happen
                            throw new RuntimeException("This should never happen.");
                    }
                }
                break;
            case "WB-BWBBBWB-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,7,1,0,1)) break;
                whiteThreats++;
                break;
            case "WB-BWBBBW-W":
            case "WB-BWBBWB-BW":
            case "W-WBBBWB-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,6,1,0,1)) break;
                whiteThreats++;
                break;
            case "WB-BWB+W-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                whiteThreats++;
                break;
            case "W-BWBB+W-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,0,1,0,5)) break;
                switch (direction) {
                    case 'N':
                        if (getAt(row+4,col-1)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'S':
                        if (getAt(row-4,col+1)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'W':
                        if (getAt(row+1,col+4)==Traxboard.WE) whiteThreats++;
                        break;
                    case 'E':
                        if (getAt(row-1,col-4)==Traxboard.WE) whiteThreats++;
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-BWB+W-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                switch (direction) {
                    case 'N':
                        if (getAt(row+3,col-1)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'S':
                        if (getAt(row-3,col+1)==Traxboard.NS) whiteThreats++;
                        break;
                    case 'W':
                        if (getAt(row+1,col+3)==Traxboard.WE) whiteThreats++;
                        break;
                    case 'E':
                        if (getAt(row-1,col-3)==Traxboard.WE) whiteThreats++;
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-WBBBW-W":
            case "WB-BWBB+W-W":
            case "WB-BWBBW-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,5,1,0,1)) break;
                whiteThreats++;
                break;
            case "W-WBB+W-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                whiteThreats++;
                break;
            case "W-WBBW-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,4,1,0,1)) break;
                whiteThreats++;
                break;
            case "W-WB+W-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,0,1,0,3)) break;
                whiteThreats++;
                break;
            case "W-W+BBWB-BW":
            case "W-W+BWB-BW":
            case "W-W+BW-W":
            case "W-W+BBW-W":
            case "W-WBBWB-BW":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,0,0,0,2)) break;
                whiteThreats++;
                break;
            case "B-B":
                if (checkThreatPosition(row,col,direction,0,1,0,1)) {
                    blackCorners++;
                }
                break;
            case "B-B+WB-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,0,0,0,2)) break;
                blackThreats++;
                break;
            case "B-BW+B-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,0,1,0,3)) break;
                blackThreats++;
                break;
            case "B-B+WBW-WB":
            case "B-B+WWB-B":
            case "B-BWW+B-B":
            case "B-BWWB-B":
            case "B-B+WWBW-WB":
            case "B-BWWBW-WB":
            case "B-BWWWBW-WB":
            case "B-BWWWB-B":
            case "B-WBW+B-B":
            case "B-WBWW+B-B":
            case "BW-WBW+B-B":
            case "BW-WBWWB-B":
            case "BW-WBWW+B-B":
            case "BW-WBWWWBW-WB":
            case "BW-WBWWBW-WB":
            case "BW-WBWWWB-B":
                //if (checkThreatPosition(row,col,direction,0,1,0,1)) break;
                //if (isOutside(row,col) && (getRowSize()==8) || (getColSize()==8)) { break; }
                blackCorners++;
                blackThreats++;
                break;
            case "B-BW+B-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)==Traxboard.SE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                            blackThreats++;
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.NW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                            blackThreats++;
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.NE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                            blackThreats++;
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.SW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,4)) break;
                            blackThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-BWW+B-WB":
            case "BW-WBW+B-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)==Traxboard.SE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,5)) break;
                            blackThreats++;
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.NW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,5)) break;
                            blackThreats++;
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.NE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,5)) break;
                            blackThreats++;
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.SW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,5)) break;
                            blackThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-WBWW+B-WB":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                        if (getAt(row,col)==Traxboard.SE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            blackThreats++;
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.NW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            blackThreats++;
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.NE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            blackThreats++;
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.SW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,1,0,6)) break;
                            blackThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-WB":
                if (checkThreatPosition(row,col,direction,0,1,0,1)) {
                    switch (direction) {
                        case 'S':
                            if (getAt(row,col)==Traxboard.SE) { blackCorners++; }
                            break;
                        case 'N':
                            if (getAt(row,col)==Traxboard.NW) { blackCorners++; }
                            break;
                        case 'E':
                            if (getAt(row,col)==Traxboard.NE) { blackCorners++; }
                            break;
                        case 'W':
                            if (getAt(row,col)==Traxboard.SW) { blackCorners++; }
                            break;
                        default:
                            // This should never happen
                            throw new RuntimeException("This should never happen.");
                    }
                }
                break;
            case "BW-WB":
                if (checkThreatPosition(row,col,direction,0,1,0,2)) {
                    blackCorners++;
                }
                break;
            case "BW-B":
                if (checkThreatPosition(row,col,direction,0,1,0,1)) {
                    switch (direction) {
                        case 'S':
                        case 'N':
                            if (getAt(row,col)==Traxboard.NS) { blackCorners++; }
                            break;
                        case 'E':
                        case 'W':
                            if (getAt(row,col)==Traxboard.WE) { blackCorners++; }
                            break;
                        default:
                            // This should never happen
                            throw new RuntimeException("This should never happen.");
                    }
                }
                break;
            case "B-W+B-WBW-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,0)) break;
                            if (getAt(row+3,col-1)==Traxboard.WE) { blackThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,0)) break;
                            if (getAt(row-1,col-3)==Traxboard.NS) { blackThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,0)) break;
                            if (getAt(row+1,col+3)==Traxboard.NS)  { blackThreats++; }
                        }
                        break;
                    case 'E':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,0)) break;
                            if (getAt(row-3,col+1)==Traxboard.WE) { blackThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-B+WWBW-B":
            case "B-B+WBW-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'S':
                    case 'N':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,2)) break;
                            blackThreats++;
                        }
                        break;
                    case 'E':
                    case 'W':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,0,0,0,2)) break;
                            blackThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-BWWBW-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                switch (direction) {
                    case 'S':
                    case 'N':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (checkThreatPosition(row,col,direction,5,1,0,1)) blackThreats++;
                        }
                        break;
                    case 'E':
                    case 'W':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (checkThreatPosition(row,col,direction,5,1,0,1)) blackThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-BWWWBW-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                switch (direction) {
                    case 'S':
                    case 'N':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (checkThreatPosition(row,col,direction,6,1,0,1)) blackThreats++;
                        }
                        break;
                    case 'E':
                    case 'W':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (checkThreatPosition(row,col,direction,6,1,0,1)) blackThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-B+WB-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,0,0,0,2)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+2,col+1)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-2,col-1)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'S':
                        if (getAt(row-1,col+2)==Traxboard.WE) { blackThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+1,col-2)==Traxboard.WE) { blackThreats++; }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-BW-B+W-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'N':
                        if (getAt(row,col-1)==Traxboard.NW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,3,1,0,2)) break;
                            blackThreats++;
                        }
                        break;
                    case 'S':
                        if (getAt(row,col+1)==Traxboard.SE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,3,1,0,2)) break;
                            blackThreats++;
                        }
                        break;
                    case 'E':
                        if (getAt(row-1,col)==Traxboard.NE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,3,1,0,2)) break;
                            blackThreats++;
                        }
                        break;
                    case 'W':
                        if (getAt(row+1,col)==Traxboard.SW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,3,1,0,2)) break;
                            blackThreats++;
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-BWB-W+B-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'N':
                        if (getAt(row,col-1)==Traxboard.SE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,2)) break;
                            if (getAt(row+1,col-2)==Traxboard.SW) { whiteThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col-1)==Traxboard.NW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,2)) break;
                            if (getAt(row-1,col+2)==Traxboard.NE) { whiteThreats++; }
                        }
                        break;
                    case 'E':
                        if (getAt(row-1,col)==Traxboard.SW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,2)) break;
                            if (getAt(row-2,col-1)==Traxboard.NW) { whiteThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row+1,col)==Traxboard.NE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,2)) break;
                            if (getAt(row+2,col+1)==Traxboard.SE) { whiteThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-WBW-B+W-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'N':
                        if (getAt(row,col-1)==Traxboard.NW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,2)) break;
                            if (getAt(row+1,col-2)==Traxboard.NE) { blackThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col-1)==Traxboard.SE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,2)) break;
                            if (getAt(row-1,col+2)==Traxboard.SW) { blackThreats++; }
                        }
                        break;
                    case 'E':
                        if (getAt(row-1,col)==Traxboard.NE) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,2)) break;
                            if (getAt(row-2,col-1)==Traxboard.SE) { blackThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row+1,col)==Traxboard.SW) {
                            blackCorners++;
                            if (!checkThreatPosition(row,col,direction,4,0,0,2)) break;
                            if (getAt(row+2,col+1)==Traxboard.NW) { blackThreats++; }
                        }
                        break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "WB-W+BW-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,0,0,0,2)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+2,col+1)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-2,col-1)==Traxboard.NS) { whiteThreats++; }
                        break;
                    case 'N':
                        if (getAt(row+1,col-2)==Traxboard.WE) { whiteThreats++; }
                        break;
                    case 'S':
                        if (getAt(row-1,col+2)==Traxboard.WE) { whiteThreats++; }
                        break;
                    default:
                        //This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-B+W-BW-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                whiteCorners++;
                if (!checkThreatPosition(row,col,direction,3,0,0,0)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+1,col-1)==Traxboard.SE) { whiteThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-1,col+1)==Traxboard.NW) { whiteThreats++; }
                        break;
                    case 'N':
                        if (getAt(row-1,col-1)==Traxboard.SW) { whiteThreats++; }
                        break;
                    case 'S':
                        if (getAt(row+1,col+1)==Traxboard.NE) { whiteThreats++; }
                        break;
                    default:
                        //This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "B-W+B-WB-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,1)) break;
                blackCorners++;
                if (!checkThreatPosition(row,col,direction,3,0,0,0)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+1,col-1)==Traxboard.NW) { blackThreats++; }
                        break;
                    case 'E':
                        if (getAt(row-1,col+1)==Traxboard.SE) { blackThreats++; }
                        break;
                    case 'N':
                        if (getAt(row-1,col-1)==Traxboard.NE) { blackThreats++; }
                        break;
                    case 'S':
                        if (getAt(row+1,col+1)==Traxboard.SW) { blackThreats++; }
                        break;
                    default:
                        //This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "BW-B+WBW-B":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'E':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (getAt(row-3,col-1)==Traxboard.WE && checkThreatPosition(row,col,direction,0,0,0,2)) { blackThreats++; }
                        }
                        break;
                    case 'W':
                        if (getAt(row,col)==Traxboard.WE) {
                            blackCorners++;
                            if (getAt(row+3,col+1)==Traxboard.WE && checkThreatPosition(row,col,direction,0,0,0,2)) { blackThreats++; }
                        }
                        break;
                    case 'S':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (getAt(row-1,col+3)==Traxboard.NS && checkThreatPosition(row,col,direction,0,0,0,2)) { blackThreats++; }
                        }
                        break;
                    case 'N':
                        if (getAt(row,col)==Traxboard.NS) {
                            blackCorners++;
                            if (getAt(row+1,col-3)==Traxboard.NS && checkThreatPosition(row,col,direction,0,0,0,2)) { blackThreats++; }
                        }
                        break;
                    default:
                        //This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            case "W-WB-W+B-W":
                if (!checkThreatPosition(row,col,direction,0,1,0,0)) break;
                switch (direction) {
                    case 'W':
                        if (getAt(row+1,col)==Traxboard.NE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,3,1,0,2)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'E':
                        if (getAt(row-1,col)==Traxboard.SW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,3,1,0,2)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'N':
                        if (getAt(row,col-1)==Traxboard.SE) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,3,1,0,2)) break;
                            whiteThreats++;
                        }
                        break;
                    case 'S':
                        if (getAt(row,col+1)==Traxboard.NW) {
                            whiteCorners++;
                            if (!checkThreatPosition(row,col,direction,3,1,0,2)) break;
                            whiteThreats++;
                        }
                        break;
                    default:
                        //This should never happen
                        throw new RuntimeException("This should never happen.");
                }
                break;
            default:
                // This should never happen
                throw new RuntimeException("This should never happen. ["+threat+"]");
        }
    }

    private void count() {
        if (num_of_tiles<4) {
            blackThreats=0; whiteThreats=0;
        }
        if (num_of_tiles<2) {
            countCornersSimple();
            return;
        }
        int state=0;
        char c;
        blackCorners=0; whiteCorners=0;
        blackThreats=0; whiteThreats=0;
        char direction='S';
        int row=0;
        int col=1;
        String border=getBorder();
        border=border+"-";
        for (char c2 : border.toCharArray()) {
            if (c2=='-' || c2=='+') break;
            border=border+c2;
        }
        if (DEBUG) { System.err.println("border="+border); }

        while ((row<8) && (this.getAt(row,col)==Traxboard.EMPTY)) {
            row++;
        }
        row--;

        for (int i=0; i<border.length(); i++ ) {
            c=border.charAt(i);
            switch (direction) {
                case 'S':
                    switch (c) {
                        case 'B':
                        case 'W':
                            row++;
                            break;
                        case '+':
                            row++;
                            direction='W';
                            break;
                        case '-':
                            direction='E';
                            col--;
                            break;
                        default: /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 'W':
                    switch (c) {
                        case 'B':
                        case 'W':
                            col--;
                            break;
                        case '+':
                            direction='N';
                            col--;
                            break;
                        case '-':
                            direction='S';
                            row--;
                            break;
                        default: /* This should never happen */
                            throw new RuntimeException("585");
                    }
                    break;
                case 'E':
                    switch (c) {
                        case 'B':
                        case 'W':
                            col++;
                            break;
                        case '+':
                            col++;
                            direction='S';
                            break;
                        case '-':
                            direction='N';
                            row++;
                            break;
                        default: /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 'N':
                    switch (c) {
                        case 'B':
                        case 'W':
                            row--;
                            break;
                        case '+':
                            row--;
                            direction='E';
                            break;
                        case '-':
                            direction='W';
                            col++;
                            break;
                        default: /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                default:
		    /* This should never happen */
                    throw new RuntimeException("This should never happen.");
            }
            if (DEBUG) System.err.println("state="+state);

            switch (state) {
                case 0:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 1:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 2:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=291;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 3:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=290;
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 4:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=269;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 5:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=6;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 6:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=7;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 7:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-BW");
                            break;
                        case 'B':
                            state=261;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 8:
                    switch (c) {
                        case 'W':
                            state=9;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 9:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 10:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=11;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 11:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=12;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 12:
                    switch (c) {
                        case 'W':
                            state=13;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 13:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=14;
                            checkThreat(row,col,direction,"B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 14:
                    switch (c) {
                        case 'W':
                            state=224;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=15;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 15:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-W+B-WB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 16:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-WB");
                            break;
                        case '+':
                            state=17;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 17:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=18;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 18:
                    switch (c) {
                        case 'W':
                            state=19;
                            break;
                        case 'B':
                            state=208;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=12;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 19:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=205;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=20;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 20:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"WB-W+BW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 21:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"BW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 22:
                    switch (c) {
                        case 'W':
                            state=164;
                            break;
                        case 'B':
                            state=186;
                            break;
                        case '+':
                            state=28;
                            break;
                        case '-':
                            state=23;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 23:
                    switch (c) {
                        case 'W':
                            state=24;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 24:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-WB");
                            break;
                        case '+':
                            state=25;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 25:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=26;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 26:
                    switch (c) {
                        case 'W':
                            state=19;
                            break;
                        case 'B':
                            state=208;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=27;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 27:
                    switch (c) {
                        case 'W':
                            state=13;
                            checkThreat(row,col,direction,"W-WB-W+B-W");
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 28:
                    switch (c) {
                        case 'W':
                            state=29;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 29:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=30;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 30:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-WB+W-W");
                            break;
                        case 'B':
                            state=31;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 31:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-WB+W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 32:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=33;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 33:
                    switch (c) {
                        case 'W':
                            state=34;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 34:
                    switch (c) {
                        case 'W':
                            state=35;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 35:
                    switch (c) {
                        case 'W':
                            state=160;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=36;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 36:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=37;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 37:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=38;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 38:
                    switch (c) {
                        case 'W':
                            state=39;
                            break;
                        case 'B':
                            state=32;
                            checkThreat(row,col,direction,"B+B-WW-B+B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 39:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=40;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 40:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=41;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 41:
                    switch (c) {
                        case 'W':
                            state=42;
                            checkThreat(row,col,direction,"W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 42:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=44;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=43;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 43:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-B+W-BW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 44:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=54;
                            break;
                        case '+':
                            state=50;
                            break;
                        case '-':
                            state=45;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 45:
                    switch (c) {
                        case 'W':
                            state=46;
                            checkThreat(row,col,direction,"W-B+W-BWB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 46:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-WB");
                            break;
                        case '+':
                            state=47;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 47:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=48;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 48:
                    switch (c) {
                        case 'W':
                            state=19;
                            break;
                        case 'B':
                            state=208;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=49;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 49:
                    switch (c) {
                        case 'W':
                            state=13;
                            checkThreat(row,col,direction,"W-BWB-W+B-W");
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 50:
                    switch (c) {
                        case 'W':
                            state=51;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 51:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=52;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 52:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-BWB+W-W");
                            break;
                        case 'B':
                            state=53;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 53:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-BWB+W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 54:
                    switch (c) {
                        case 'W':
                            state=59;
                            break;
                        case 'B':
                            state=154;
                            break;
                        case '+':
                            state=55;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 55:
                    switch (c) {
                        case 'W':
                            state=56;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 56:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=57;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 57:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-BWBB+W-W");
                            break;
                        case 'B':
                            state=58;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 58:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-BWBB+W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 59:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=61;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=60;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 60:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-BWBBW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 61:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=62;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 62:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"W-BWBBWB-W");
                            break;
                        case 'B':
                            state=63;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 63:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"W-BWBBWB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 64:
                    switch (c) {
                        case 'W':
                            state=65;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 65:
                    switch (c) {
                        case 'W':
                            state=66;
                            break;
                        case 'B':
                            state=81;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 66:
                    switch (c) {
                        case 'W':
                            state=71;
                            break;
                        case 'B':
                            state=76;
                            break;
                        case '+':
                            state=67;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 67:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=68;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 68:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=69;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 69:
                    switch (c) {
                        case 'W':
                            state=70;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-B+WW+B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 70:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-B+WW+B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 71:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=72;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 72:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=73;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 73:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=74;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 74:
                    switch (c) {
                        case 'W':
                            state=75;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-B+WWW+B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 75:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-B+WWW+B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 76:
                    switch (c) {
                        case 'W':
                            state=78;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=77;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 77:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-B+WWB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 78:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=79;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 79:
                    switch (c) {
                        case 'W':
                            state=80;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"B-B+WWBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 80:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"B-B+WWBW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 81:
                    switch (c) {
                        case 'W':
                            state=83;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=82;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 82:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-B+WB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 83:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=84;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 84:
                    switch (c) {
                        case 'W':
                            state=85;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"B-B+WBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 85:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"B-B+WBW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 86:
                    switch (c) {
                        case 'W':
                            state=116;
                            break;
                        case 'B':
                            state=132;
                            break;
                        case '+':
                            state=112;
                            break;
                        case '-':
                            state=87;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 87:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=88;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 88:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=89;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 89:
                    switch (c) {
                        case 'W':
                            state=90;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 90:
                    switch (c) {
                        case 'W':
                            state=92;
                            break;
                        case 'B':
                            state=107;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=91;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 91:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=41;
                            checkThreat(row,col,direction,"B-BW-B+W-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 92:
                    switch (c) {
                        case 'W':
                            state=97;
                            break;
                        case 'B':
                            state=102;
                            break;
                        case '+':
                            state=93;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 93:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=94;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 94:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=95;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 95:
                    switch (c) {
                        case 'W':
                            state=96;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"BW-B+WW+B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 96:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"BW-B+WW+B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 97:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=98;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 98:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=99;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 99:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=100;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 100:
                    switch (c) {
                        case 'W':
                            state=101;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"BW-B+WWW+B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 101:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"BW-B+WWW+B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 102:
                    switch (c) {
                        case 'W':
                            state=104;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=103;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 103:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"BW-B+WWB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 104:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=105;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 105:
                    switch (c) {
                        case 'W':
                            state=106;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B+WWBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 106:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"BW-B+WWBW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 107:
                    switch (c) {
                        case 'W':
                            state=109;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=108;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 108:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"BW-B+WB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 109:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=110;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 110:
                    switch (c) {
                        case 'W':
                            state=111;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B+WBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 111:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"BW-B+WBW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 112:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=113;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 113:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=114;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 114:
                    switch (c) {
                        case 'W':
                            state=115;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-BW+B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 115:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-BW+B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 116:
                    switch (c) {
                        case 'W':
                            state=121;
                            break;
                        case 'B':
                            state=127;
                            break;
                        case '+':
                            state=117;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 117:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=118;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 118:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=119;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 119:
                    switch (c) {
                        case 'W':
                            state=120;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-BWW+B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 120:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-BWW+B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 121:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=122;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 122:
                    switch (c) {
                        case 'W':
                            state=124;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=123;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 123:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-BWWWB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 124:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=125;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 125:
                    switch (c) {
                        case 'W':
                            state=126;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"B-BWWWBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 126:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"B-BWWWBW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 127:
                    switch (c) {
                        case 'W':
                            state=129;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=128;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 128:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-BWWB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 129:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=130;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 130:
                    switch (c) {
                        case 'W':
                            state=131;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"B-BWWBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 131:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"B-BWWBW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 132:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=138;
                            break;
                        case '+':
                            state=134;
                            break;
                        case '-':
                            state=133;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 133:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 134:
                    switch (c) {
                        case 'W':
                            state=135;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 135:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=136;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 136:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"WB-BWB+W-W");
                            break;
                        case 'B':
                            state=137;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 137:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"WB-BWB+W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 138:
                    switch (c) {
                        case 'W':
                            state=143;
                            break;
                        case 'B':
                            state=148;
                            break;
                        case '+':
                            state=139;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 139:
                    switch (c) {
                        case 'W':
                            state=140;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 140:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=141;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 141:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"WB-BWBB+W-W");
                            break;
                        case 'B':
                            state=142;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 142:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"WB-BWBB+W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 143:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=145;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=144;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 144:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"WB-BWBBW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 145:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=146;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 146:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-BWBBWB-W");
                            break;
                        case 'B':
                            state=147;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 147:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"WB-BWBBWB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 148:
                    switch (c) {
                        case 'W':
                            state=149;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 149:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=151;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=150;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 150:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"WB-BWBBBW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 151:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=152;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 152:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-BWBBBWB-W");
                            break;
                        case 'B':
                            state=153;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 153:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"WB-BWBBBWB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 154:
                    switch (c) {
                        case 'W':
                            state=155;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 155:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=157;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=156;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 156:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-BWBBBW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 157:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=158;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 158:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"W-BWBBBWB-W");
                            break;
                        case 'B':
                            state=159;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 159:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"W-BWBBBWB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 160:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=161;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 161:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=162;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 162:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=163;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 163:
                    switch (c) {
                        case 'W':
                            state=39;
                            break;
                        case 'B':
                            state=32;
                            checkThreat(row,col,direction,"B+B-WWW-B+B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 164:
                    switch (c) {
                        case 'W':
                            state=170;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=166;
                            break;
                        case '-':
                            state=165;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 165:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 166:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=167;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 167:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=168;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 168:
                    switch (c) {
                        case 'W':
                            state=169;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"BW-WBW+B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 169:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"BW-WBW+B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 170:
                    switch (c) {
                        case 'W':
                            state=175;
                            break;
                        case 'B':
                            state=181;
                            break;
                        case '+':
                            state=171;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 171:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=172;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 172:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=173;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 173:
                    switch (c) {
                        case 'W':
                            state=174;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"BW-WBWW+B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 174:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"BW-WBWW+B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 175:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=176;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 176:
                    switch (c) {
                        case 'W':
                            state=178;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=177;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 177:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"BW-WBWWWB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 178:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=179;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 179:
                    switch (c) {
                        case 'W':
                            state=180;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-WBWWWBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 180:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"BW-WBWWWBW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 181:
                    switch (c) {
                        case 'W':
                            state=183;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=182;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 182:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"BW-WBWWB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 183:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=184;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 184:
                    switch (c) {
                        case 'W':
                            state=185;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-WBWWBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 185:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"BW-WBWWBW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 186:
                    switch (c) {
                        case 'W':
                            state=191;
                            break;
                        case 'B':
                            state=196;
                            break;
                        case '+':
                            state=187;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 187:
                    switch (c) {
                        case 'W':
                            state=188;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 188:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=189;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 189:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-WBB+W-W");
                            break;
                        case 'B':
                            state=190;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 190:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-WBB+W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 191:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=193;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=192;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 192:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-WBBW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 193:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=194;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 194:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"W-WBBWB-W");
                            break;
                        case 'B':
                            state=195;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 195:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"W-WBBWB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 196:
                    switch (c) {
                        case 'W':
                            state=197;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 197:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=199;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=198;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 198:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-WBBBW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 199:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=200;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 200:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"W-WBBBWB-W");
                            break;
                        case 'B':
                            state=201;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 201:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"W-WBBBWB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 202:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=203;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 203:
                    switch (c) {
                        case 'W':
                            state=204;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 204:
                    switch (c) {
                        case 'W':
                            state=92;
                            break;
                        case 'B':
                            state=107;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=40;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 205:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=206;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 206:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W+BWB-W");
                            break;
                        case 'B':
                            state=207;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 207:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"WB-W+BWB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 208:
                    switch (c) {
                        case 'W':
                            state=213;
                            break;
                        case 'B':
                            state=218;
                            break;
                        case '+':
                            state=209;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 209:
                    switch (c) {
                        case 'W':
                            state=210;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 210:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=211;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 211:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"WB-W+BB+W-W");
                            break;
                        case 'B':
                            state=212;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 212:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"WB-W+BB+W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 213:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=215;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=214;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 214:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"WB-W+BBW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 215:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=216;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 216:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W+BBWB-W");
                            break;
                        case 'B':
                            state=217;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 217:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"WB-W+BBWB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 218:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=219;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 219:
                    switch (c) {
                        case 'W':
                            state=220;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 220:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=221;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 221:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"WB-W+BBB+W-W");
                            break;
                        case 'B':
                            state=222;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 222:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"WB-W+BBB+W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 223:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"WB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 224:
                    switch (c) {
                        case 'W':
                            state=234;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=230;
                            break;
                        case '-':
                            state=225;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 225:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=226;
                            checkThreat(row,col,direction,"B-W+B-WBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 226:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=227;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 227:
                    switch (c) {
                        case 'W':
                            state=228;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 228:
                    switch (c) {
                        case 'W':
                            state=92;
                            break;
                        case 'B':
                            state=107;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=229;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 229:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=41;
                            checkThreat(row,col,direction,"B-WBW-B+W-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 230:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=231;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 231:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=232;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 232:
                    switch (c) {
                        case 'W':
                            state=233;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-WBW+B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 233:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-WBW+B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 234:
                    switch (c) {
                        case 'W':
                            state=239;
                            break;
                        case 'B':
                            state=245;
                            break;
                        case '+':
                            state=235;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 235:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=236;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 236:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=237;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 237:
                    switch (c) {
                        case 'W':
                            state=238;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-WBWW+B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 238:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-WBWW+B-WB");
                            break;
                        case '+':
                            state=10;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 239:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=240;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 240:
                    switch (c) {
                        case 'W':
                            state=242;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=241;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 241:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-WBWWWB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 242:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=243;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 243:
                    switch (c) {
                        case 'W':
                            state=244;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"B-WBWWWBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 244:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"B-WBWWWBW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 245:
                    switch (c) {
                        case 'W':
                            state=247;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=246;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 246:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-WBWWB-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 247:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=248;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 248:
                    switch (c) {
                        case 'W':
                            state=249;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"B-WBWWBW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 249:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=22;
                            checkThreat(row,col,direction,"B-WBWWBW-WB");
                            break;
                        case '+':
                            state=4;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 250:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 251:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=165;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 252:
                    switch (c) {
                        case 'W':
                            state=253;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=133;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 253:
                    switch (c) {
                        case 'W':
                            state=234;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=230;
                            break;
                        case '-':
                            state=254;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 254:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-W");
                            break;
                        case 'B':
                            state=226;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 255:
                    switch (c) {
                        case 'W':
                            state=256;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 256:
                    switch (c) {
                        case 'W':
                            state=116;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=112;
                            break;
                        case '-':
                            state=87;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 257:
                    switch (c) {
                        case 'W':
                            state=39;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 258:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=259;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=165;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 259:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=54;
                            break;
                        case '+':
                            state=50;
                            break;
                        case '-':
                            state=260;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 260:
                    switch (c) {
                        case 'W':
                            state=46;
                            checkThreat(row,col,direction,"WB-W");
                            break;
                        case 'B':
                            state=223;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 261:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=265;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=262;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 262:
                    switch (c) {
                        case 'W':
                            state=263;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 263:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-WB");
                            break;
                        case '+':
                            state=264;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 264:
                    switch (c) {
                        case 'W':
                            state=5;
                            checkThreat(row,col,direction,"W+W-BB-W+W");
                            break;
                        case 'B':
                            state=11;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 265:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=266;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 266:
                    switch (c) {
                        case 'W':
                            state=267;
                            break;
                        case 'B':
                            state=255;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 267:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=252;
                            checkThreat(row,col,direction,"B-WB");
                            break;
                        case '+':
                            state=268;
                            break;
                        case '-':
                            state=2;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 268:
                    switch (c) {
                        case 'W':
                            state=5;
                            checkThreat(row,col,direction,"W+W-BBB-W+W");
                            break;
                        case 'B':
                            state=11;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 269:
                    switch (c) {
                        case 'W':
                            state=270;
                            break;
                        case 'B':
                            state=275;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 270:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=272;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=271;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 271:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-W+BW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 272:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=273;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 273:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"W-W+BWB-W");
                            break;
                        case 'B':
                            state=274;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 274:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"W-W+BWB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 275:
                    switch (c) {
                        case 'W':
                            state=280;
                            break;
                        case 'B':
                            state=285;
                            break;
                        case '+':
                            state=276;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 276:
                    switch (c) {
                        case 'W':
                            state=277;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 277:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=278;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 278:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-W+BB+W-W");
                            break;
                        case 'B':
                            state=279;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 279:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-W+BB+W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 280:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=282;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=281;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 281:
                    switch (c) {
                        case 'W':
                            state=21;
                            checkThreat(row,col,direction,"W-W+BBW-W");
                            break;
                        case 'B':
                            state=202;
                            checkThreat(row,col,direction,"BW-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 282:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=283;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 283:
                    switch (c) {
                        case 'W':
                            state=16;
                            checkThreat(row,col,direction,"W-W+BBWB-W");
                            break;
                        case 'B':
                            state=284;
                            checkThreat(row,col,direction,"B-B");
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 284:
                    switch (c) {
                        case 'W':
                            state=86;
                            checkThreat(row,col,direction,"W-W+BBWB-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=64;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 285:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=286;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 286:
                    switch (c) {
                        case 'W':
                            state=287;
                            break;
                        case 'B':
                            state=32;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 287:
                    switch (c) {
                        case 'W':
                            state=1;
                            break;
                        case 'B':
                            state=293;
                            break;
                        case '+':
                            state=292;
                            break;
                        case '-':
                            state=288;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 288:
                    switch (c) {
                        case 'W':
                            state=3;
                            checkThreat(row,col,direction,"W-W+BBB+W-W");
                            break;
                        case 'B':
                            state=289;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 289:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-W+BBB+W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 290:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=186;
                            break;
                        case '+':
                            state=28;
                            break;
                        case '-':
                            state=23;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 291:
                    switch (c) {
                        case 'W':
                            state=258;
                            checkThreat(row,col,direction,"W-BW");
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=257;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 292:
                    switch (c) {
                        case 'W':
                            state=5;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=0;
                            break;
                        case '-':
                            state=0;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 293:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=133;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                case 294:
                    switch (c) {
                        case 'W':
                            state=251;
                            break;
                        case 'B':
                            state=294;
                            break;
                        case '+':
                            state=250;
                            break;
                        case '-':
                            state=8;
                            break;
                        default:
        /* This should never happen */
                            throw new RuntimeException("This should never happen.");
                    }
                    break;
                default:
  /* This should never happen */
                    throw new RuntimeException("This should never happen.");
            }
        }
    }


    private int neighbor_value(int x, int y)
    {
        int value = 0;
        int up = getAt(x - 1, y), down = getAt(x + 1, y), left = getAt(x, y - 1), right = getAt(
                x, y + 1);
        if (up == Traxboard.SN || up == Traxboard.SE || up == Traxboard.SW) {
            value += 1;
        } // ohs_up
        if (up == Traxboard.EW || up == Traxboard.NW || up == Traxboard.NE) {
            value += 16;
        } // eks_up
        if (down == Traxboard.NS || down == Traxboard.NE
                || down == Traxboard.NW) {
            value += 2;
        } // ohs_down
        if (down == Traxboard.EW || down == Traxboard.SW
                || down == Traxboard.SE) {
            value += 32;
        } // eks_down
        if (left == Traxboard.EN || left == Traxboard.ES
                || left == Traxboard.EW) {
            value += 4;
        } // ohs_left;
        if (left == Traxboard.WS || left == Traxboard.WN
                || left == Traxboard.NS) {
            value += 64;
        } // eks_left;
        if (right == Traxboard.WN || right == Traxboard.WE
                || right == Traxboard.WS) {
            value += 8;
        } // ohs.right
        if (right == Traxboard.ES || right == Traxboard.NS
                || right == Traxboard.EN) {
            value += 128;
        } // eks.right
        return value;
    }

    public ArrayList<Integer> getLegalTiles(int x, int y)
    {
        ArrayList<Integer> result = new ArrayList<>();
        if (this.boardEmpty) {
            result.add(Traxboard.NW);
            result.add(Traxboard.NS);
        }
        switch (neighbor_value(x, y)) {
            case 0:
                return result;
            case 1:
                result.add(Traxboard.NW);
                result.add(Traxboard.NS);
                result.add(Traxboard.NE);
                return result;
            case 128:
                result.add(Traxboard.WS);
                result.add(Traxboard.NS);
                result.add(Traxboard.WN);
                return result;
            case 2:
                result.add(Traxboard.SW);
                result.add(Traxboard.SE);
                result.add(Traxboard.SN);
                return result;
            case 32:
                result.add(Traxboard.WE);
                result.add(Traxboard.WN);
                result.add(Traxboard.NE);
                return result;
            case 8:
                result.add(Traxboard.EW);
                result.add(Traxboard.ES);
                result.add(Traxboard.EN);
                return result;
            case 4:
                result.add(Traxboard.WE);
                result.add(Traxboard.WS);
                result.add(Traxboard.WN);
                return result;
            case 64:
                result.add(Traxboard.NS);
                result.add(Traxboard.NE);
                result.add(Traxboard.SE);
                return result;
            case 16:
                result.add(Traxboard.WE);
                result.add(Traxboard.WS);
                result.add(Traxboard.SE);
                return result;
            case 36:
                result.add(Traxboard.WN);
                result.add(Traxboard.WE);
                return result;
            case 66:
                result.add(Traxboard.SN);
                result.add(Traxboard.SE);
                return result;
            case 132:
                result.add(Traxboard.WN);
                result.add(Traxboard.WS);
                return result;
            case 72:
                result.add(Traxboard.EN);
                result.add(Traxboard.ES);
                return result;
            case 65:
                result.add(Traxboard.NS);
                result.add(Traxboard.NE);
                return result;
            case 20:
                result.add(Traxboard.WE);
                result.add(Traxboard.WS);
                return result;
            case 33:
                result.add(Traxboard.NW);
                result.add(Traxboard.NE);
                return result;
            case 18:
                result.add(Traxboard.SW);
                result.add(Traxboard.SE);
                return result;
            case 129:
                result.add(Traxboard.NW);
                result.add(Traxboard.NS);
                return result;
            case 24:
                result.add(Traxboard.EW);
                result.add(Traxboard.ES);
                return result;
            case 40:
                result.add(Traxboard.EW);
                result.add(Traxboard.EN);
                return result;
            case 130:
                result.add(Traxboard.SN);
                result.add(Traxboard.SW);
                return result;
            default:
                // This should never happen
                throw new RuntimeException("This should never happen.");
        }
    }

    public static Traxboard changeColors(Traxboard tb) {
	return changeColours(tb);
    }

    public static Traxboard changeColours(Traxboard tb)
    {
        Traxboard result=new Traxboard(tb);
        for (int i=result.firstrow; i<=result.lastrow; i++) {
            for (int j=result.firstcol; j<=result.lastcol; j++) {
                switch (result.board[i][j]) {
                    case EMPTY: result.board[i][j]=EMPTY; break;
                    case NS: result.board[i][j]=WE; break;
                    case WE: result.board[i][j]=NS; break;
                    case NW: result.board[i][j]=SE; break;
                    case NE: result.board[i][j]=SW; break;
                    case SW: result.board[i][j]=NE; break;
                    case SE: result.board[i][j]=NW; break;
                    default: /* This should never happen */
                        throw new RuntimeException("This should never happen.)");
                }
            }
        }
        result.switchPlayer();
        if (tb.border!=null) {
            StringBuilder newBorder;
            newBorder = new StringBuilder(tb.border.length());
            for(char c : tb.border.toCharArray()) {
                switch (c) {
                    case 'W': newBorder.append("B"); break;
                    case 'B': newBorder.append("W"); break;
                    case '+':
                    case '-': break;
                    default:
                        // This should never happen
                        throw new RuntimeException("This should never happen.");
                }
            }
            result.border=newBorder.toString();
        }
        return result;
    }

    public static Traxboard mirrorBoard(Traxboard tb)
    {
        Traxboard result=new Traxboard(tb);
        for (int i=tb.firstrow; i<=tb.lastrow; i++) {
            for (int j=tb.firstcol; j<=tb.lastcol; j++) {
		//System.err.println("mirrorBoard, i="+i+", j="+j);
		//System.err.println("mirrorBoard, tb.board[i][j]="+tb.board[i][j]);
                switch (tb.board[i][j]) {
                    case EMPTY:
                    case NS:
                    case WE:
                        result.board[i][tb.lastcol-j+tb.firstcol]=tb.board[i][j];
                        break;
                    case NW:
                        result.board[i][tb.lastcol-j+tb.firstcol]=NE;
                        break;
                    case NE:
                        result.board[i][tb.lastcol-j+tb.firstcol]=NW;
                        break;
                    case SW:
                        result.board[i][tb.lastcol-j+tb.firstcol]=SE;
                        break;
                    case SE:
                        result.board[i][tb.lastcol-j+tb.firstcol]=SW;
                        break;
                    default:
                        // This should never happen 
                        throw new RuntimeException("This should never happen.");
                }
            }
        }
        result.border=null;
        return result;
    }
}
