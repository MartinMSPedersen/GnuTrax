/* 
   
   Date: 7th of April 2015
   version 0.2
   All source under GPL version 3 or latter
   (GNU General Public License - http://www.gnu.org/)
   contact traxplayer@gmail.com for more information about this code
   
*/

package org.traxgame.main;

import java.util.ArrayList;
import java.util.Random;

public class benchmark
{

  public static void main (String[] args)
  {
    long startTime = System.nanoTime();    
    Traxboard tb;
    int game_value;
    ArrayList <String> move_list;
    String random_move;
    int draw = 0, p1 = 0, p2 = 0;
    int game_length;
    final int num_of_games = 40000;
    int r, i;
    Random random_generator = new Random();

    game_length = 0;
    for (i = 0; i < num_of_games; i++) {
      try {
	tb=new Traxboard ();
	do {
	  move_list=tb.uniqueMoves();
	  random_move = move_list.get(random_generator.nextInt(move_list.size()));
	  tb.makeMove(random_move);
	  game_value = tb.isGameOver ();
	  game_length++;
	} while (game_value == Traxboard.NOPLAYER);
      }
      catch (IllegalMoveException e) {
	  /* This should never happen */
	  throw new RuntimeException(e);
      }
      switch (game_value) {
	case Traxboard.DRAW:
	  draw++;
	  break;
	case Traxboard.WHITE:
	  p1++;
	  break;
	case Traxboard.BLACK:
	  p2++;
	  break;
	default:
	  /* This should never happen */
	  throw new RuntimeException("This should never happen.");
      }
    }
    long endTime = System.nanoTime();    
    int elapsedTime = (int)(((endTime-startTime)/1000000000)+0.5);

    System.out.println ("p1=" + p1 + " p2=" + p2 + " draw=" + draw);
    System.out.println();
    System.out.format("Average game length: %.2f%n",(float) game_length / num_of_games);
    System.out.format("Elapsed time: %d seconds.%n",elapsedTime);
  }

}
