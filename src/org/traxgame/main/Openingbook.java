/* 

Date: Januar 28 - 2014
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
import java.util.HashSet;

public class Openingbook
{
  private String inputfile;
  private int maxply;
  private HashSet<OpeningbookEntry> theBook=new HashSet<OpeningbookEntry>();

  public Openingbook(String inputfile) { 
	  this(inputfile,-1);
  }

  public Openingbook(String inputfile, int maxply) { 
    this.inputfile=inputfile;
    this.maxply=maxply;
  }

  public void generateBook() throws IOException {
    String s;
    Traxboard tb;
    BufferedReader reader=new BufferedReader(new FileReader(new File(inputfile)));
    BufferedWriter writer=new BufferedWriter(new FileWriter(new File("./book.bin")));
    while ( (s=reader.readLine()) != null) {
        tb=new Traxboard();
        boolean useless=false;
        try {
          for (String move : s.split("\\s")) {
            tb.makeMove(move);
          }
        } 
        catch (IllegalMoveException e) { useless=true; }
        if (!useless && tb.isGameOver()!=Traxboard.NOPLAYER) {
          tb=new Traxboard();
          for (String move : s.split("\\s")) {
            try {
              tb.makeMove(move);
            }
            catch (IllegalMoveException e) { 
              // This should never happen
              throw new RuntimeException(e);
            }
            OpeningbookEntry oe=search(tb.getBorder(),tb.whoToMove());
            if (oe==null) {
              oe=new OpeningbookEntry(tb);
              switch (tb.isGameOver()) {
              case Traxboard.DRAW:    
                oe.incrementDraw();
                break;
              case Traxboard.WHITE:
                oe.incrementWhite();
                break;
              case Traxboard.BLACK:
                oe.incrementBlack();
                break;
              default:
                // This should never happen
                throw new RuntimeException();
              }
            }
          }
        }
    }
  }

  public OpeningbookEntry search(String border, int wtm) {
          return this.search(new OpeningbookEntry(border,wtm));
  }

  public OpeningbookEntry search(OpeningbookEntry oe) {
          if (!theBook.contains(oe)) return null;
          return oe;
  }

  public String info() {
	  StringBuffer result=new StringBuffer();

	  return result.toString();
  }

  public static void main(String[] args) {
    Openingbook o=new Openingbook("doby3_games.txt");
    try {
      o.generateBook();
    }
    catch (Exception e) { System.err.println(e); }
  }

private class OpeningbookEntry
{
  private int wtm;
  private String border;
  private int black, white, draw;
  private boolean alwaysPlay;

  public int hashCode() { return border.hashCode()+wtm; }
   
  public boolean equals(Object obj) {
    if (this==obj) return true;
    if ((obj == null) || (obj.getClass() != this.getClass())) return false;
    OpeningbookEntry oe=(OpeningbookEntry)obj;
    return (oe.whoToMove()==this.wtm && oe.getBorder().equals(this.getBorder()));
  }

  public OpeningbookEntry(String border, int wtm, boolean alwaysPlay) { 
	  this.alwaysPlay=alwaysPlay;
	  this.border=border;
	  this.wtm=wtm;
  }

  public OpeningbookEntry(String border, int wtm) { this(border,wtm,false); }

  public OpeningbookEntry(Traxboard tb, boolean alwaysPlay) {
	 this.alwaysPlay=alwaysPlay;
	 this.border=tb.getBorder(false);
	 this.wtm=tb.whoToMove();
  }

  public OpeningbookEntry(Traxboard tb) { this(tb,false); }

  public String getBorder() { return border; } 
  public int whoToMove()    { return wtm; }
  public void incrementDraw() { draw++; }
  public void incrementBlack() { white++; }
  public void incrementWhite() { black++; }
  
  public int score() {
	  if (alwaysPlay) return 1000;
	  return 0;
  }

}
}

