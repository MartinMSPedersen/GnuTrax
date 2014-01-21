/* 

Date: Januar 21 - 2014
version 0.1
All source under GPL version 2 
(GNU General Public License - http://www.gnu.org/)
contact traxplayer@gmail.com for more information about this code
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Openingbook
{
  private String inputfile;
  private int maxply;
  private OpeningbookEntry[] theBook;

  public Openingbook(String inputfile) { 
	  this(inputfile,-1);
  }

  public Openingbook(String inputfile, int maxply) { 
    this.inputfile=inputfile;
    this.maxply=maxply;
  }

  public void generateBook() throws IOException {
    File file=new File(inputfile);
    String s;
    BufferedReader reader=new BufferedReader(new FileReader(file));
    while ( (s=reader.readLine()) != null) {
	    ;
    }
  }

  public OpeningbookEntry search(String border, int wtm) {
	  if (theBook == null) return null;
	  return null;
  }

  public String info() {
	  StringBuffer result=new StringBuffer();

	  return result.toString();
  }

}
