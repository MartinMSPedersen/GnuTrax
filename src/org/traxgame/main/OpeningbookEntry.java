/* 

Date: Januar 21 - 2014
version 0.1
All source under GPL version 2 
(GNU General Public License - http://www.gnu.org/)
contact traxplayer@gmail.com for more information about this code
*/

package org.traxgame.main;

public class OpeningbookEntry
{
	private int wtm;
	private String border;
	private int wins, draws, loses;
	private boolean alwaysPlay;

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
  public void incrementWins() { wins++; }
  public void incrementDraws() { draws++; }
  public void incrementLoses() { loses++; }
  
  public int score() {
	  if (alwaysPlay) return 1000;
	  return 0;
  }


}
