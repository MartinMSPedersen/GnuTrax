/* 

Date: Februar 1 - 2014
version 0.1
All source under GPL version 2 
(GNU General Public License - http://www.gnu.org/)
contact traxplayer@gmail.com for more information about this code
*/

package org.traxgame.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;

public class Openingbook
{
  private String inputfile;
  private HashMap<bookKey,bookValue> theBook;

  public Openingbook() { 
    theBook=new HashMap<bookKey,bookValue>();
  }

  public void setInputfile(String inputfile) {
    this.inputfile=inputfile;
  }

  public void generateBook() throws IOException {
    this.generateBook(false);
  }

  public void generateBook(boolean winning) throws IOException {
    String s;
    Traxboard tb;
    BufferedReader reader=new BufferedReader(new FileReader(new File(inputfile)));
    if (winning==false) {
	    while ( (s=reader.readLine()) != null) {
		    tb=new Traxboard();
		    boolean useless=false;
		    try {
			    for (String move : s.split("\\s")) { tb.makeMove(move); } 
		    } catch (IllegalMoveException e) { useless=true; }
		    if (!useless && tb.isGameOver()!=Traxboard.NOPLAYER) {
			    int gameOverValue=tb.isGameOver();
			    tb=new Traxboard();
			    for (String move : s.split("\\s")) {
				    try {
					    tb.makeMove(move);
				    }
				    catch (IllegalMoveException e) { 
					    // This should never happen
					    throw new RuntimeException(e);
				    }
				    bookKey bk=new bookKey(tb.getBorder(),tb.whoToMove());
				    bookValue bv=search(bk);
				    if (bv==null) { bv=new bookValue(); }
				    switch (gameOverValue) {
					    case Traxboard.DRAW:  bv.draw++; break;
					    case Traxboard.WHITE: bv.white++; break;
					    case Traxboard.BLACK: bv.black++; break;
					    default:
								  // This should never happen
								  throw new RuntimeException("\n"+tb+"tb.isGameOver()="+tb.isGameOver());
				    }
				    theBook.put(bk,bv);
			    }
		    }
	    }
	}	
	else {
	   while ( (s=reader.readLine()) != null) {
		    tb=new Traxboard();
		    try {
			    for (String move : s.split("\\s")) { tb.makeMove(move); } 
			    bookKey bk=new bookKey(tb.getBorder(),tb.whoDidLastMove());

			    theBook.remove(bk);
			    bookValue bv=new bookValue(); 
			    bv.alwaysPlay=true;
			    theBook.put(bk,bv);
		    } catch (IllegalMoveException e) { ; }
	  }
       }
    }

  private void saveBook() throws IOException {
    BufferedWriter writer=new BufferedWriter(new FileWriter(new File("./book.bin")));
    for (Map.Entry<bookKey, bookValue> entry : theBook.entrySet()) {
      if ((entry.getValue().alwaysPlay) || (entry.getValue().white+entry.getValue().black+entry.getValue().draw>2)) {
        writer.write(entry.getKey()+" "+entry.getValue()+"\n");
      }
    }
    writer.close();
  }

  public bookValue search(String border, int wtm) {
          return this.search(new bookKey(border,wtm));
  }

  public bookValue search(bookKey bk) {
          return theBook.get(bk);
  }

  public String info() {
	  StringBuffer result=new StringBuffer();

	  return result.toString();
  }

  public static void main(String[] args) {
    Openingbook o=new Openingbook();
    try {
      o.setInputfile("alwaysplay.trx");
      o.generateBook(true);
      o.setInputfile("t");
      o.generateBook();
      o.saveBook();
    }
    catch (Exception e) { System.err.println(e); }
  }

  private class bookValue {
    public int black, white, draw;
    public boolean alwaysPlay;
    public int score() {
      if (alwaysPlay) return 1000;
      return 0;
    }
    
    public bookValue() { this(false); }

    public bookValue(boolean alwaysPlay) { 
	this.alwaysPlay=alwaysPlay; 
        black=0; white=0; draw=0;
    }
     
    public String toString() {
      return ((alwaysPlay?"true":"false")+" "+black+" "+white+" "+draw);
    }
  }

  private class bookKey {
    public int wtm;
    public String border;

    public String toString() {
      return wtm+" "+border;
    }

    public bookKey(String border, int wtm) {
      this.wtm=wtm;
      this.border=border;
    }

    public bookKey(Traxboard tb) {
      this(tb.getBorder(),tb.whoToMove());
    }

    public int hashCode() { return border.hashCode()+wtm; }
   
    public boolean equals(Object obj) {
      if (this==obj) return true;
      if ((obj == null) || (obj.getClass() != this.getClass())) return false;
      bookKey oe=(bookKey)obj;
      return (oe.wtm==this.wtm && oe.border.equals(this.border));
    }
  }
}
