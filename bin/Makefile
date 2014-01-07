# Date: 26th of December 2008
# version 0.1
# All source under GPL version 2
# (GNU General Public License - http://www.gnu.org/)
# contact traxplayer@gmail.com for more information about this code

#PATH_TO_JAVAC = /usr/lib/jvm/java-6-sun-1.6.0.10/bin/
PATH_TO_JAVAC=
MAIN_OBJECTS = GnuTrax.java Traxboard.java IllegalMoveException.java TraxUtil.java benchmark.java ComputerPlayerSimple.java ComputerPlayer.java UctNode.java ComputerPlayerUct.java ComputerPlayerAlphaBeta.java


JAVAC = ${PATH_TO_JAVAC}javac
JAVAC_FLAGS =  -Xmaxerrs 10 -Xlint:all -source 1.6

all:	gnutrax 

gnutrax: $(MAIN_OBJECTS)
	$(JAVAC) $(JAVAC_FLAGS) $(MAIN_OBJECTS) && jar cfe gnutrax.jar GnuTrax *.class *.java Makefile LICENSE GnuTrax.sh

.PHONY:	clean tar

tar:
	make clean && cd .. && tar cvf gnutrax/gnutrax.tar gnutrax/* && gzip -9 gnutrax/gnutrax.tar && cp gnutrax/gnutrax.tar.gz .

clean:
	-/bin/rm -f *.class gnutrax.tar.gz *~ \#* 2>/dev/null
