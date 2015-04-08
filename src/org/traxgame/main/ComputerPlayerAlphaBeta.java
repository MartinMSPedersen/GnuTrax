/* 
   Date: 12th of October 2014
   version 0.1
   All source under GPL version 3 or latter
   (GNU General Public License - http://www.gnu.org/)
   contact traxplayer@gmail.com for more information about this code
*/

package org.traxgame.main;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

public class ComputerPlayerAlphaBeta extends ComputerPlayer
{
    class TranspositionValue {
	int depth;
	int eval;
    }

    private Openingbook book=new Openingbook();
    private static final int MAX_LEVEL=6;
    private static int evals;
    private static int alwaysHit;
    private static int neverHit;
    //private static HashMap<Traxboard,TranspositionValue> TranspositionHash;

    static {
	//TranspositionHash=new HashMap<Traxboard,TranspositionValue>();
	ComputerPlayer.searchDepth=4;
    }

    public ComputerPlayerAlphaBeta() {
	book.loadBook();
    }
   
    public String computerMove(Traxboard tb) {
        String simple=null;
	evals=0; alwaysHit=0; neverHit=0;

 	simple=simpleMove(tb);
	if (simple!=null) { 
	    return simple; 
	}
	return alphabeta(tb);
    }

    protected int eval(Traxboard tb) {
        evals++;
	switch (tb.isGameOver()) {
	    case Traxboard.DRAW: 
	      return 0;
	    case Traxboard.WHITE:
	      return Integer.MAX_VALUE;
	    case Traxboard.BLACK:
	      return Integer.MIN_VALUE;
	}
	 Openingbook.BookValue bv=book.search(tb);
	 if (bv!=null) {
	     if (bv.alwaysPlay) {
		 alwaysHit++;
		 return (tb.whoDidLastMove()==Traxboard.WHITE)?Integer.MAX_VALUE:Integer.MIN_VALUE;
	     }
	     if (bv.neverPlay) {
		 neverHit++;
		 return (tb.whoDidLastMove()==Traxboard.WHITE)?Integer.MIN_VALUE:Integer.MAX_VALUE;
	     }
	 }
	 if (tb.whoToMove()==Traxboard.WHITE) {
	   switch (ComputerPlayer.evalType) {
	     case 0:
	       return 200*(2*tb.getNumberOfWhiteCorners()-tb.getNumberOfBlackCorners())+
	             1000*(4*tb.getNumberOfWhiteThreats()-tb.getNumberOfBlackThreats());
             case 1:
	       return 100*(tb.getNumberOfWhiteCorners()-tb.getNumberOfBlackCorners())+
	             1000*(4*tb.getNumberOfWhiteThreats()-tb.getNumberOfBlackThreats());
	     default:
	       return 1000*(tb.getNumberOfWhiteCorners()-tb.getNumberOfBlackCorners())+
	              3000*(2*tb.getNumberOfWhiteThreats()-tb.getNumberOfBlackThreats());
	   }
         }
	 switch (ComputerPlayer.evalType) {
	   case 0:
	     return 200*(2*tb.getNumberOfBlackCorners()-tb.getNumberOfWhiteCorners())+
	           1000*(4*tb.getNumberOfBlackThreats()-tb.getNumberOfWhiteThreats());
           case 1:
	     return 100*(tb.getNumberOfBlackCorners()-tb.getNumberOfWhiteCorners())+
	           1000*(4*tb.getNumberOfBlackThreats()-tb.getNumberOfWhiteThreats());
	   default:
	     return 1000*(tb.getNumberOfBlackCorners()-tb.getNumberOfWhiteCorners())+
	            3000*(2*tb.getNumberOfBlackThreats()-tb.getNumberOfWhiteThreats());
	 }
    }
   
    private void outputStats() {
	System.err.println("evals: "+evals);
	System.err.println("alwaysplay: "+alwaysHit);
	System.err.println("neverplay: "+neverHit);
    }

    private String alphabeta(Traxboard tb) {
	int score;
	String bestmove=null;
	int winner_score,losing_score;

	if (ComputerPlayer.searchDepth>MAX_LEVEL || ComputerPlayer.searchDepth<0) {
	  ComputerPlayer.searchDepth=MAX_LEVEL;
	}
	ArrayList<String> candidateMoves=tb.uniqueMoves();
	Collections.shuffle(candidateMoves);
	ArrayList<String> rootMoves=new ArrayList<String>();
	if (candidateMoves.size()==1) return candidateMoves.get(0);

	winner_score=Integer.MAX_VALUE;
	losing_score=Integer.MIN_VALUE;
	if (tb.whoToMove()==Traxboard.BLACK) { 
	    winner_score=Integer.MIN_VALUE; 
	    losing_score=Integer.MAX_VALUE; 
	}
	for (String move : candidateMoves) {
	    Traxboard t_copy=new Traxboard(tb);
	    try { t_copy.makeMove(move); }
	    catch (IllegalMoveException e) { throw new RuntimeException("This should never happen."); }
	    int value=eval(t_copy);
	    if (value==winner_score) {
		// Winning move found
		return move;
            }
	    if (value==losing_score) {
		continue;
	    }
	    rootMoves.add(move); // not add losing moves.
	}
	if (rootMoves.size()==0) {
	    // Only bad moves
	    return TraxUtil.getRandomMove(tb);
	}
	int count=1;
	int best_score;
	boolean maxPlayer;
	if (tb.whoToMove()==Traxboard.BLACK) {
	    maxPlayer=false;
	    best_score=Integer.MAX_VALUE;
	} else { 
	    maxPlayer=true;
	    best_score=Integer.MIN_VALUE;
	}
	for (String move : rootMoves) {
	  if (count<10 && rootMoves.size()>9) System.err.print(" ");
	  System.err.print(count+"/"+rootMoves.size()+": "+move);
	  count++;
	  Traxboard t_copy=new Traxboard(tb);
	  try { 
	      t_copy.makeMove(move); 
	  }
	  catch (IllegalMoveException e) { 
	      throw new RuntimeException("This should never happen."); 
	  }
	  score=alphabeta_score(t_copy,ComputerPlayer.searchDepth,Integer.MIN_VALUE,Integer.MAX_VALUE,!maxPlayer);
	  System.err.println(" "+score);
	  if (maxPlayer) {
	      if (score>=best_score) {
	        best_score=score;
		bestmove=move;
		if (best_score==Integer.MAX_VALUE) {
		  outputStats();
		  return bestmove;
		}
	      }
	  } else {
	      if (score<=best_score) {
		  best_score=score;
		  bestmove=move;
		if (best_score==Integer.MIN_VALUE) {
		  outputStats();
		  return bestmove;
		}
	      }
	  }
	}
	outputStats();
	return bestmove;
    }

    private int alphabeta_score(Traxboard tb, int depth, int alpha, int beta, boolean maxPlayer) {
	if ((depth==0) || (tb.isGameOver()!=Traxboard.NOPLAYER)) return eval(tb);
	int value;
	ArrayList<String> candidateMoves=tb.uniqueMoves();
	Collections.shuffle(candidateMoves);
	for (String move : candidateMoves) {
	  Traxboard t_copy=new Traxboard(tb);
	  try { 
	    t_copy.makeMove(move); 
	  }
	  catch (IllegalMoveException e) { 
	    throw new RuntimeException("This should never happen."); 
	  }
	  Openingbook.BookValue bv=book.search(t_copy);
	  if (bv!=null) {
	     if (bv.alwaysPlay) {
		 alwaysHit++;
		 return (tb.whoDidLastMove()==Traxboard.WHITE)?Integer.MIN_VALUE:Integer.MAX_VALUE;
	     }
	     if (bv.neverPlay) {
		 neverHit++;
		 return (tb.whoDidLastMove()==Traxboard.WHITE)?Integer.MIN_VALUE:Integer.MAX_VALUE;
	     }
	 }
	  value=alphabeta_score(t_copy,depth-1,alpha,beta,!maxPlayer);
	  if (maxPlayer && value>=alpha) {
	      alpha=value;
	      if (beta<=alpha) {
		  // beta cut-cuff
		  break;
	      }
	  } 
	  if (!maxPlayer && value<=beta) {
	    beta=value;
	    if (beta<=alpha) {
		// alpha cut-off
		break;
            }
	  }
	}
	if (maxPlayer) return alpha;
	return beta;
    }
}
