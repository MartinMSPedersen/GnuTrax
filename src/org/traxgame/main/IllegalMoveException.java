package org.traxgame.main;

/* 

 Date: 27th of December 2008
 version 0.2
 All source under GPL version 2 
 (GNU General Public License - http://www.gnu.org/)
 contact traxplayer@gmail.com for more information about this code

 */

public class IllegalMoveException extends Exception {
	public IllegalMoveException(String message) {
		super(message);
	}

	public static final long serialVersionUID = 24162462L;
}
