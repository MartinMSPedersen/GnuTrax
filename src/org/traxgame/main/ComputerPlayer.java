/* 

Date: 3th of Januar 2009
version 0.1
All source under GPL version 3 or latter
(GNU General Public License - http://www.gnu.org/)
contact traxplayer@gmail.com for more information about this code

*/

package org.traxgame.main;



public abstract class ComputerPlayer
{
  public abstract String computerMove (Traxboard tb);
  public static int searchDepth=0;
  public static int evalType=0;
  public static int seed=0;

  public static void setSearchDepth(String depth) throws NumberFormatException {
    ComputerPlayer.searchDepth=Integer.parseInt(depth);
  }

  public static void setSeed(String seed) throws NumberFormatException {
      int theSeed=Integer.parseInt(seed);
      ComputerPlayer.seed=theSeed;
      TraxUtil.setSeed(theSeed);
  }

  public static void setEvalType(String evaltype) throws NumberFormatException {
    ComputerPlayer.evalType=Integer.parseInt(evaltype);
  }
     
  public String simpleMove(Traxboard tb) {
      if (tb.getNumOfTiles()>3) return null;

      int percent=TraxUtil.getRandom(100);
      switch (tb.getNumOfTiles()) {
       case 0:
        if (percent<50) {
	  return new String("@0/");
        } else {
          return new String("@0+");
        }
      case 1:
        if (tb.getAt(1,1)==Traxboard.NW) {
          if (percent<15) {
            return new String("B1\\");
          }
	  if (percent<90) {
            return new String("@1/");
          } else {
	    return new String("B1+");
	  }
        }
        if (tb.getAt(1,1)==Traxboard.NS) {
          if (percent<5) {
            return new String("A0+");
          }
          if (percent<15) {
            return new String("A0/");
          }
          if (percent<25) {
            return new String("B1/");
          }
          return new String("B1+");
        }
        break; 
      case 2:
        if (tb.getAt(1,1)==Traxboard.NW) {
          if (tb.getAt(1,2)==Traxboard.NE) return new String("A2/");
	  if (tb.getAt(2,1)==Traxboard.WS) return new String("B1/");
        }
	if (tb.getAt(1,2)==Traxboard.NS) {
		if (percent<40) return new String("C1/");
		if (percent<80) return new String("B0+");
		if (percent<90) return new String("B0/");
		return new String("B0\\");
	}
	if (tb.getAt(1,1)==Traxboard.SE) {
	    if (tb.getAt(1,2)==Traxboard.NW) {
		if (percent<50) return new String("@1/");
		if (percent<60) return new String("A2/");
		if (percent<80) return new String("B0+");
		return new String("B2/");
	    }
	    if (tb.getAt(2,1)==Traxboard.NW) {
		if (percent<50) return new String("A3/");
		if (percent<60) return new String("@2/");
		if (percent<80) return new String("B1+");
		return new String("B2/");
	    }
	}
        break;
      case 3:
	if (tb.getAt(2,1)==Traxboard.NS) {
	    if ((tb.getAt(1,1)==Traxboard.ES) && (tb.getAt(3,1)==Traxboard.EN)) {
		if (percent<=20) return new String("B2/");
		return new String("B1/");
	    }
	    if ((tb.getAt(1,1)==Traxboard.WS) && (tb.getAt(3,1)==Traxboard.WN)) {
		if (percent<=20) return new String("@2\\");
		return new String("@1\\");
	    }
	}
        break;
      }

      return null;
    }

}
