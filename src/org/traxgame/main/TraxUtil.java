/* 
   
   Date: 6th of April 2015
   version 0.2
   All source under GPL version 3 or latter
   (GNU General Public License - http://www.gnu.org/)
   contact traxplayer@gmail.com for more information about this code
   
*/

package org.traxgame.main;

import java.util.Random;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public abstract class TraxUtil
{

    static boolean LOG=false;
    static Random random_generator;
    static Openingbook book;

    static
    {
	random_generator = new Random(System.currentTimeMillis());
	book=new Openingbook();
	book.loadBook();
    }
    
    public static int getRandom(int limit) {
	return random_generator.nextInt (limit);
    }

    public static void setSeed(int seed) {
	random_generator=new Random(seed);
    }

    public static void startLog() { LOG=true; }
    public static void stopLog() { LOG=false; }

    public static void log(String msg) {
	if (LOG) {
	    System.err.println(msg);
	}
    }

    public static ArrayList<String> notBadMoves(Traxboard t) {
	ArrayList <String> result=new ArrayList<String>();
	if (t.isGameOver()!=Traxboard.NOPLAYER) return result;

	ArrayList <String> moves=t.uniqueMoves();
	if (moves.size()==1) return moves;

	for (String move : moves) {
	    Traxboard t_copy=new Traxboard(t);
	    try {
		t_copy.makeMove(move);
	    } catch (IllegalMoveException e) {
		// This should never happen.
		throw new RuntimeException("This should never happen.");
	    }
	    if (book.searchNeverPlay(t_copy)==true) {
		// Losing move found
		continue;
	    }
	    if (book.searchAlwaysPlay(t_copy)) {
		// Winning move found
		result=new ArrayList<String>(1);
		result.add(move);
		return result;
	    }
	    int gameOverValue=t_copy.isGameOver();
	    switch (gameOverValue) {
		case Traxboard.WHITE:
		case Traxboard.BLACK:
		if (t_copy.whoDidLastMove()==gameOverValue) { // Winning move found
		    result=new ArrayList<String>(1);
		    result.add(move);
		    return result;
		}
		// Losing move found
		break;
		case Traxboard.DRAW:
		case Traxboard.NOPLAYER:
		   result.add(move);
		   break;
	  	default:
		// This should never happen
		throw new RuntimeException("This should never happen.");
	    }
	}
	if (result.size()==0) { // There are only bad moves ...
	    result.add(moves.get(0));
	}
	return result;
    }
    
    public static String getRandomMove (Traxboard t) {
	String move;
	int losingMoves = 0;
	
	if (t.isGameOver()!=Traxboard.NOPLAYER) { return new String (""); }

	ArrayList<String> moves=notBadMoves(t);
	return moves.get(random_generator.nextInt(moves.size()));
    }
    
    public static ArrayList <String> getInput() {
	ArrayList < String > result = new ArrayList < String > (10);
	String line;
	try {
	    line=new BufferedReader (new InputStreamReader (System.in)).readLine ();
	    if (line!=null) {
		StringTokenizer st = new StringTokenizer (line);
		while (st.hasMoreTokens ()) result.add (st.nextToken ());
	    }
	}
	catch (IOException e) {
	    e.printStackTrace ();
	}
	return result;
    }

    public static String reverseBorder(String border) {
      StringBuffer result=new StringBuffer(border.length());
      for (int i=0; i<border.length(); i++) {
	  switch (border.charAt(i)) {
	      case 'W': result.append('B'); break;
	      case 'B': result.append('W'); break;
	      case '+': result.append('+'); break;
	      case '-': result.append('-'); break;
	      default:
	        // This should never happen
		throw new RuntimeException("This should never happen.");
	  }
      }
      return result.toString();
    }
    
    public static String normalizeMove(Traxboard t, String move) throws IllegalMoveException {
	ArrayList <String> moves;
	Traxboard target;

	target=new Traxboard(t);
	target.makeMove(move);
	for (String m : t.uniqueMoves_with_mirrors()) {
	    Traxboard t_copy=new Traxboard(t);
	    try {
		t_copy.makeMove(m);
	    }
	    catch (IllegalMoveException e) {
		// This should never happen
		throw new RuntimeException("This should never happen.");
	    }
	    if (target.equals(t_copy)) return m;
	}
	return move;
    }


    public static void main (String[]args) {
	startLog();
	
	Traxboard tb=new Traxboard();
	String move;
	try {
	    tb.makeMove("a1c");
	    tb.makeMove("b1d");
	    move=getRandomMove(tb);
	    System.out.println(tb+move);
	    tb.makeMove(move);
	    System.out.println(tb);
	    tb=new Traxboard();
	    tb.makeMove("a1c");
	    tb.makeMove("a1u");
	    System.out.println(tb.getBorder());
	    System.out.println(reverseBorder(tb.getBorder()));
	    move=getRandomMove(tb);
	    System.out.println(tb+move);
	    tb.makeMove(move);
	    System.out.println(tb);
	    System.out.println(tb.getBorder());
	    System.out.println(reverseBorder(tb.getBorder()));
	}
	catch (IllegalMoveException e) {
	    throw new AssertionError(e.getMessage());
	}
    }
}
