/* 

   Date: March 21, 2015
   version 0.2
   All source under GPL version 3 or latter
   (GNU General Public License - http://www.gnu.org/)
   contact traxplayer@gmail.com for more information about this code
*/

package org.traxgame.main;

import java.io.*;
import java.util.*;

public class Openingbook {

    private String inputfile;
    private static HashMap<BookKey, BookValue> theBook;

    public Openingbook() { 
	theBook = new HashMap<BookKey, BookValue>(); 
    }

    public int size() { return theBook.size(); }

    public void setInputfile(String inputfile) { this.inputfile = inputfile; }

    public void generateBook() throws IOException { this.generateBook(false,false); }

    public boolean searchNeverPlay(Traxboard tb) 
    {
         BookKey bk = new BookKey(tb.getBorder(), tb.whoToMove());
         BookValue bv = search(tb);
         if (bv == null) { 
	     return false; 
	 }
	 return bv.neverPlay;
    }

    public boolean searchAlwaysPlay(Traxboard tb) 
    {
         BookKey bk = new BookKey(tb.getBorder(), tb.whoToMove());
         BookValue bv = search(tb);
         if (bv == null) { return false; }
	 return bv.alwaysPlay;
    }

    private void generateNeverAlwaysBook(boolean always, boolean never) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(new File(inputfile)));
        String s;
        while ((s = reader.readLine()) != null) {
            Traxboard tb = new Traxboard();
            try {
                for (String move : s.split("\\s")) {
                    if (!move.equals("")) tb.makeMove(move);
                }
		for (int k=1; k<=2; k++) {
		  for (int j=1; j<=4; j++) {
                    for (int i=1; i<=2; i++) {
		      BookKey bk = new BookKey(tb.getBorder(), tb.whoDidLastMove());
		      theBook.remove(bk);
		      BookValue bv = new BookValue();
		      bv.alwaysPlay = always;
		      bv.neverPlay = never;
		      theBook.put(bk, bv);
		      tb=Traxboard.mirrorBoard(tb);
                    }
		    tb=Traxboard.rotate(tb);
		  }
		  if (k==1) tb=Traxboard.changeColours(tb);
		}

            } catch (IllegalMoveException e) { System.err.println("'"+s+"' : REJECTED."); } 
        }
        reader.close();
        return;
    }

    public void generateBook(boolean always, boolean never) throws IOException
    {
        String s;
        Traxboard tb;
        if (always && never) {
            throw new IllegalArgumentException("Always and never can not both be true");
        }
        if (always || never) {
            generateNeverAlwaysBook(always,never);
            return;
        }
        BufferedReader reader = new BufferedReader(new FileReader(new File(inputfile)));
        while ((s = reader.readLine()) != null) {
            //lineno++;
            tb = new Traxboard();
            boolean useless = false;
            boolean resign = false;
            try {
                for (String move : s.split("\\s")) {
                    if (move.equalsIgnoreCase("resign")) {
                        resign=true;
                        break;
                    }
                    if (move.equalsIgnoreCase("draw")) {
                        break;
                    }
                    tb.makeMove(move);
                }
            } catch (IllegalMoveException e) {
                System.err.println("'"+s+"' : REJECTED.");  
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
                    if (move.equalsIgnoreCase("resign")) { break; }
                    tb.makeMove(move);
                } catch (IllegalMoveException e) {
                    // This should never happen
                    throw new RuntimeException(e);
                }
                BookKey bk = new BookKey(tb.getBorder(), tb.whoToMove());
                BookValue bv = search(tb);
                if (bv == null) { bv=new BookValue(); }
                updateBook(tb,bv,gameOverValue);
            }
        }
    }

    private void updateBook(Traxboard tb, BookValue bv, int gameOverValue) 
    { 
        /* Need to find the right bookKey if any exist */
        BookKey bk1;
        BookKey bk2;
           
            
        for (int i=1; i<=4; i++) {
            bk1=new BookKey(tb.getBorder(), tb.whoToMove());
            if (theBook.get(bk1)!=null) {
                theBook.put(bk1,bv);
                return;

            }
            if (i<4) tb=Traxboard.rotate(tb);
        }
        String newBorder=TraxUtil.reverseBorder(tb.getBorder());
        // How to handle tb.whoToMove()==Traxboard.NOPLAYER ? Is that a problem ?
        int newWTM=(tb.whoToMove()==Traxboard.WHITE)?Traxboard.BLACK:Traxboard.WHITE; 
        for (int i=1; i<=4; i++) {
            bk2=new BookKey(newBorder, newWTM);
            if (theBook.get(bk2)!=null) {
                theBook.put(bk2,bv);
                return;
            }
            if (i<4) {
                tb=Traxboard.rotate(tb);
                newBorder=TraxUtil.reverseBorder(tb.getBorder());
            }
        }
        theBook.put(new BookKey(tb.getBorder(),tb.whoToMove()),bv);
    }

    public void loadBook() { loadBook("url"); }

    public void loadBook(String from)  
    {
        String s;
        BufferedReader reader;

	try {
          if (from.equals("url")) {
                reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResource("games/book.bin").openStream()));
           } 
          else { 
	      reader = new BufferedReader(new FileReader(new File("games/book.bin"))); 
	  }
	}
	catch ( Exception e ) { return; }
        theBook = new HashMap<BookKey, BookValue>();
	try {
        while ((s = reader.readLine()) != null) {
            String[] elems = s.split("\\s");
            if (elems.length == 4) {
                BookKey bk = new BookKey(elems[1], elems[0]);
                BookValue bv = new BookValue(elems[2], elems[3]);
                theBook.put(bk, bv);
            }
        }
        reader.close();
	}
	catch (Exception e) { return; }
    }

    private void saveBookJSON(String num) throws IOException { saveBookJSON(Integer.parseInt(num)); }
    private void saveBookJSON() throws IOException { saveBookJSON(8); }
    private void saveBookJSON(int threshold) throws IOException 
    {
        BufferedWriter writer;
        boolean first=true;
        writer=new BufferedWriter(new FileWriter(new File("games/book.js")));
        writer.write("var bookdata = {\n");
        for (Map.Entry<BookKey, BookValue> entry : theBook.entrySet()) {
            if ((entry.getValue().neverPlay) || (entry.getValue().alwaysPlay)) {
                if (first) {
                    writer.write(entry.getKey().toStringJSON() + entry.getValue().toStringJSON());
                    first=false;
                }
                else {
                    writer.write(",\n"+entry.getKey().toStringJSON() + entry.getValue().toStringJSON());
                }
            }
        }
	writer.write("\n};");
	writer.close();
    }

    private void saveBook() throws IOException { saveBook(8); }
    private void saveBook(String num) throws IOException { saveBook(Integer.parseInt(num)); }
    private void saveBook(int threshold) throws IOException 
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("games/book.bin")));
        for (Map.Entry<BookKey, BookValue> entry : theBook.entrySet()) {
            if ((entry.getValue().neverPlay) || (entry.getValue().alwaysPlay)) {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        }
        writer.close();
    }

    public String toStringJSON() {
        StringBuffer result = new StringBuffer();

        for (Map.Entry<BookKey, BookValue> entry : theBook.entrySet()) {
            result.append("{\n");
            result.append("  "+entry.getKey()+"\n");
        }
				return result.toString();
    }

    public String toString() 
    {
        StringBuffer result = new StringBuffer();

        for (Map.Entry<BookKey, BookValue> entry : theBook.entrySet()) {
            result.append(entry.getKey().toString() + " " + entry.getValue().toString() + "\n");
        }

        return result.toString(); 
    }

    public BookValue search(Traxboard tb) { 
        BookValue result;

        result=theBook.get(new BookKey(tb.getBorder(),tb.whoDidLastMove()));
        return result;
    }

    public static void main(String[] args) 
    {
        Openingbook o = new Openingbook();
        try {
	    o.setInputfile("games/alwaysplay.trx");
            o.generateBook(true,false);
            o.setInputfile("games/neverplay.trx");
            o.generateBook(false,true);
            if (args.length>0) {        
                o.saveBook(args[0]);
                o.saveBookJSON(args[0]);
            } else { 
		o.saveBook(); 
		o.saveBookJSON(); 
	    }
        } catch (Exception e) { System.err.println(e); }
        try {
            o.loadBook("disk");
						//o.saveBook();
        } catch (Exception e) { throw new RuntimeException(e); }
        System.out.println("size of book: " + o.size());
    }

    public class BookValue 
    {
        public boolean alwaysPlay;
        public boolean neverPlay;

        public int score(int wtm) 
        {
            if (alwaysPlay) return Integer.MAX_VALUE; 
            //if (neverPlay) return Integer.MIN_VALUE;
	    return Integer.MIN_VALUE;
        }

        public BookValue() { this(false,false); }

        public BookValue(boolean alwaysPlay, boolean neverPlay)
        {
            this.alwaysPlay = alwaysPlay;
            this.neverPlay = neverPlay;
        }

        public BookValue(String alwaysPlay, String neverPlay)
        {
            this.alwaysPlay = (alwaysPlay.equals("false") ? false : true);
            this.neverPlay = (neverPlay.equals("false") ? false : true);
        }

        public String toStringJSON() {
            return ("{ \n"+"\"always\": "+(alwaysPlay ? "true" : "false")+",\n"+
                    "\"never\": "+(neverPlay ? "true" : "false")+",\n"+"}");
        }

        public String toString() {
            return ((alwaysPlay ? "true" : "false") + " " + (neverPlay ? "true" : "false"));
        }
    }
    private class BookKey 
    {
        public int wtm;
        public String border;

        public String toString() { return wtm + " " + border; }
				public String toStringJSON() { return "\""+wtm+border+"\": "; }

        public BookKey reverse() 
        {
            return new BookKey(TraxUtil.reverseBorder(border),wtm==Traxboard.WHITE?Traxboard.BLACK:Traxboard.WHITE);
        }

        public BookKey(String border, int wtm) 
        {
            this.wtm = wtm;
            this.border = border;
        }

        public BookKey(String border, String wtm) { this(border, Integer.parseInt(wtm)); }

        public BookKey(Traxboard tb) { this(tb.getBorder(), tb.whoToMove()); }

        public int hashCode() { return border.hashCode() + wtm; }

        public boolean equals(Object obj) 
        {
            if (this == obj) { return true; }
            if ((obj == null) || (obj.getClass() != this.getClass())) { return false; }
            BookKey oe = (BookKey) obj;
            return (oe.wtm == this.wtm && oe.border.equals(this.border));
        }
    }
}
