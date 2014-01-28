# Date: 14th of Januar 2014
# version 0.1
# All source under GPL version 2
# (GNU General Public License - http://www.gnu.org/)
# contact traxplayer@gmail.com for more information about this code

PATH_TO_JAVAC=
MAIN_OBJECTS = GnuTrax.java Traxboard.java IllegalMoveException.java TraxUtil.java benchmark.java ComputerPlayerSimple.java ComputerPlayer.java UctNode.java ComputerPlayerUct.java ComputerPlayerAlphaBeta.java Openingbook.java


JAVAC = ${PATH_TO_JAVAC}javac
JAVAC_FLAGS =  -Xmaxerrs 5 -Xlint:all 
PACKAGE = org.traxgame.main
PACKAGE_DIR = org/traxgame/main/

all:	gnutrax 

gnutrax: 
	ant
	
.PHONY:	clean tar

tar:
	ant package

clean:
	ant clean
