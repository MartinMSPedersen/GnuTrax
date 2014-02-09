/* 

 Date: Januar 25 - 2014
 version 0.1
 All source under GPL version 2 
 (GNU General Public License - http://www.gnu.org/)
 contact traxplayer@gmail.com for more information about this code
 */

package org.traxgame.main;

import java.io.*;
import java.util.*;

public class Openingbook {

    private String inputfile;
    private HashMap<bookKey, bookValue> theBook;

    public Openingbook() {
       theBook = new HashMap<bookKey, bookValue>();
    }

    public int size() {
        return theBook.size();
    }

    public void setInputfile(String inputfile) {
        this.inputfile = inputfile;
    }

    public void generateBook() throws IOException {
        this.generateBook(false);
    }

    public void generateBook(boolean winning) throws IOException {
        String s;
        Traxboard tb;
        BufferedReader reader = new BufferedReader(new FileReader(new File(inputfile)));
        if (winning == false) {
            while ((s = reader.readLine()) != null) {
                tb = new Traxboard();
                boolean useless = false;
                boolean resign = false;
                try {
                    for (String move : s.split("\\s")) {
                        if (move.equals("RESIGN")) {
                            break;
                        }
                        tb.makeMove(move);
                    }
                } catch (IllegalMoveException e) {
                    useless = true;
                }
                if (!resign && tb.isGameOver() == Traxboard.NOPLAYER) {
                    useless = true;
                }
                if (useless) {
                    continue;
                }
                int gameOverValue;
                if (resign) {
                    gameOverValue = tb.whoDidLastMove();
                } else {
                    gameOverValue = tb.isGameOver();
                }
                tb = new Traxboard();
                for (String move : s.split("\\s")) {
                    try {
                        if (move.equals("RESIGN")) {
                            break;
                        }
                        tb.makeMove(move);
                    } catch (IllegalMoveException e) {
                        // This should never happen
                        throw new RuntimeException(e);
                    }
                    bookKey bk = new bookKey(tb.getBorder(), tb.whoToMove());
                    bookValue bv = search(bk);
                    if (bv == null) {
			bk=bk.reverse();
			switch (gameOverValue) {
			    case Traxboard.WHITE: gameOverValue=Traxboard.BLACK; break;
			    case Traxboard.BLACK: gameOverValue=Traxboard.WHITE; break;
			}
			bv=search(bk);
	                if (bv==null) bv = new bookValue();
		    }
                    switch (gameOverValue) {
                        case Traxboard.DRAW:
                            bv.draw++;
                            break;
                        case Traxboard.WHITE:
                            bv.white++;
                            break;
                        case Traxboard.BLACK:
                            bv.black++;
                            break;
                        default:
                            // This should never happen
                            throw new RuntimeException("\n" + tb + "tb.isGameOver()=" + tb.isGameOver());
                    }
                    theBook.put(bk, bv);
                }
            }
        } else {
            while ((s = reader.readLine()) != null) {
                tb = new Traxboard();
                try {
                    for (String move : s.split("\\s")) {
                        tb.makeMove(move);
                    }
                    bookKey bk = new bookKey(tb.getBorder(), tb.whoDidLastMove());

                    theBook.remove(bk);
                    bookValue bv = new bookValue();
                    bv.alwaysPlay = true;
                    theBook.put(bk, bv);
                } catch (IllegalMoveException e) {;
                }
            }
        }
    }

    public void loadBook() throws IOException {
        loadBook("url");
    }

    public void loadBook(String from) throws IOException {
        String s;
        BufferedReader reader;

        if (from.equals("url")) {
            reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResource("games/book.bin").openStream()));
        } else {
            reader = new BufferedReader(new FileReader(new File("games/book.bin")));
        }
        theBook = new HashMap<bookKey, bookValue>();
        while ((s = reader.readLine()) != null) {
            String[] elems = s.split("\\s");
            if (elems.length == 6) {
                bookKey bk = new bookKey(elems[1], elems[0]);
                bookValue bv = new bookValue(elems[2], elems[3], elems[4], elems[5]);
                theBook.put(bk, bv);
		// System.out.println("elems: ");
		// for (String e : elems) System.out.print(e+" ");
		// System.out.println(", key: "+bk+", value:"+bv);
            }
        }
    }

    private void saveBook() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("games/book.bin")));
        for (Map.Entry<bookKey, bookValue> entry : theBook.entrySet()) {
            if ((entry.getValue().alwaysPlay) || (entry.getValue().white + entry.getValue().black + entry.getValue().draw > 2)) {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        }
        writer.close();
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        for (Map.Entry<bookKey, bookValue> entry : theBook.entrySet()) {
            result.append(entry.getKey() + " " + entry.getValue() + "\n");
        }
        return result.toString();
    }

    public bookValue search(String border, int wtm) {
        return this.search(new bookKey(border, wtm));
    }

    public bookValue search(bookKey bk) {
	bookValue result=theBook.get(bk);
	if (bk!=null) return result;
        return theBook.get(bk.reverse());
    }

    public bookValue search(Traxboard tb) {
	return this.search(tb.getBorder(), tb.whoToMove());
    }

    public String info() {
        StringBuffer result = new StringBuffer();

        return result.toString();
    }

    public static void main(String[] args) {
        Openingbook o = new Openingbook();
        try {
            o.setInputfile("games/alwaysplay.trx");
            o.generateBook(true);
            o.setInputfile("games/Trax.trx");
            o.generateBook();
            o.saveBook();
        } catch (Exception e) {
            System.err.println(e);
        }
        try {
            o.loadBook("disk");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("size of book: " + o.size());
        System.out.println(o);
    }

    public class bookValue {

        public int black, white, draw;
        public boolean alwaysPlay;

        public int score(int wtm) {
	    //System.out.println("SCORE: "+white+" "+black+" "+draw);
            if (alwaysPlay) return Integer.MAX_VALUE; 
	    if (wtm==Traxboard.WHITE) return (TraxUtil.getRandom(50)+1000*black/(black+white+draw+1));
	    return (TraxUtil.getRandom(50)+1000*white/(black+white+draw+1));
	}

        public bookValue() { this(false); }

        public bookValue(boolean alwaysPlay, int black, int white, int draw) {
            this.alwaysPlay = alwaysPlay;
            this.black = black;
            this.white = white;
            this.draw = draw;
        }

        public bookValue(String alwaysPlay, String black, String white, String draw) {
            this(alwaysPlay, Integer.parseInt(black), Integer.parseInt(white), Integer.parseInt(draw));
        }

        public bookValue(String alwaysPlay, int black, int white, int draw) {
            this.alwaysPlay = (alwaysPlay.equals("false") ? false : true);
            this.black = black;
            this.white = white;
            this.draw = draw;
        }

        public bookValue(boolean alwaysPlay) {
            this(alwaysPlay, 0, 0, 0);
        }

        public String toString() {
            return ((alwaysPlay ? "true" : "false") + " " + black + " " + white + " " + draw);
        }
    }

    private class bookKey {

        public int wtm;
        public String border;

        public String toString() {
            return wtm + " " + border;
        }

	public bookKey reverse() {
	    return new bookKey(TraxUtil.reverseBorder(border),wtm==Traxboard.WHITE?Traxboard.BLACK:Traxboard.WHITE);
	}

        public bookKey(String border, int wtm) {
            this.wtm = wtm;
            this.border = border;
        }

        public bookKey(String border, String wtm) {
            this(border, Integer.parseInt(wtm));
        }

        public bookKey(Traxboard tb) {
            this(tb.getBorder(), tb.whoToMove());
        }

        public int hashCode() {
            return border.hashCode() + wtm;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || (obj.getClass() != this.getClass())) {
                return false;
            }
            bookKey oe = (bookKey) obj;
            return (oe.wtm == this.wtm && oe.border.equals(this.border));
        }
    }
}
