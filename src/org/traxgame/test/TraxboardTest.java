package org.traxgame.main;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class TraxboardTest {

    private Traxboard t;

    @Before
    public void setUp() { t=new Traxboard(); }

    @Test
    public void testMirrorBoard1() throws IllegalMoveException {
	t.makeMoves("@0+");
	Traxboard t2=Traxboard.mirrorBoard(t);
	assertTrue(t.equals(t2));
    }

    @Test
    public void testMirrorBoard2() throws IllegalMoveException {
	t.makeMoves("@0/");
	Traxboard t2=Traxboard.mirrorBoard(t);
	assertFalse(t.equals(t2));
    }
    
    @Test
    public void testMirrorBoard3() throws IllegalMoveException {
	t.makeMoves("@0/ @1+");
	t=Traxboard.mirrorBoard(t);
	if (!t.getBorder().equals("B-BB-W-BW")) {
	    System.out.println(t.getBorder());
	}
	assertTrue(t.getBorder().equals("B-BB-W-BW"));
    }

    @Test
    public void testGetNumberOfWhiteThreats1() throws IllegalMoveException {
        t.makeMoves("@0/ @1/ A2/ C2+ B3\\ B4\\ C4\\ D1\\");
        assertTrue(t.getNumberOfWhiteThreats()==0);
    }

    @Test
    public void testGetNumberOfBlackThreats1() throws IllegalMoveException {
        t.makeMoves("@0/ @1/ A2/ C2+ B3\\ B4\\ C4\\ D1\\");
        assertTrue(t.getNumberOfBlackThreats()==2);
    }

    @Test
    public void testGetNumberOfWhiteCorners1() throws IllegalMoveException {
        t.makeMoves("@0/ @1/ A2/ C2+ B3\\ B4\\ C4\\ D1\\");
        assertTrue(t.getNumberOfWhiteCorners()==2);
    }

    @Test
    public void testGetNumberOfWhiteCorners2() throws IllegalMoveException {
        t.makeMoves("a1c");
        assertTrue(t.getNumberOfWhiteCorners()==1);
    }

    @Test
    public void testGetNumberOfWhiteCorners3() throws IllegalMoveException {
        t.makeMoves("@0/");
	assertTrue(t.getNumberOfWhiteCorners()==1);
    }

    @Test
    public void testGetNumberOfWhiteCorners4() throws IllegalMoveException {
        t.makeMoves("@0/ A2/ @2/ @2/ @2/ @2/ @2/ @2/");
	assertTrue(t.getNumberOfWhiteCorners()==2);
    }

    @Test
    public void testGetNumberOfBlackCorners1() throws IllegalMoveException {
        t.makeMoves("@0/ @1/ A2/ C2+ B3\\ B4\\ C4\\ D1\\");
        assertTrue(t.getNumberOfBlackCorners() == 4);
    }

    @Test
    public void testGetNumberOfBlackCorners2() throws IllegalMoveException {
        t.makeMoves("@0/ B1+");
        assertTrue("Expected number: 1, got: "+t.getNumberOfBlackCorners(),t.getNumberOfBlackCorners() == 1);
    }

    @Test
    public void testGetNumberOfBlackCorners3() throws IllegalMoveException {
        t.makeMoves("@0/ A2/ @2/ @2/ @2/ @2/ @2/ G1/ H1/");
        assertTrue(t.getNumberOfBlackCorners() == 0);
    }

    @Test
    public void testGetNumberOfBlackCorners4() throws IllegalMoveException {
        t.makeMoves("@0/ B1/ B0/ B0/ B0/ B0/ B0/ B0/ B0/");
        assertTrue(t.getNumberOfBlackCorners() == 0);
    }

    @Test
    public void testGetNumberOfBlackCorners5() throws IllegalMoveException {
        t.makeMoves("@0/ B1+");
        assertTrue(t.getNumberOfBlackCorners() == 1);
    }

    @Test
    public void testGetNumberOfBlackCorners6() throws IllegalMoveException {
        t.makeMoves("a1c");
        assertTrue(t.getNumberOfBlackCorners()==1);
    }

    @Test
    public void testGetNumberOfBlackCorners7() throws IllegalMoveException {
        t.makeMoves("@0/");
	assertTrue(t.getNumberOfBlackCorners()==1);
    }

    @Test
    public void testGetNumberOfBlackCorners8() throws IllegalMoveException {
        t.makeMoves("@0/ @1/ A2/ C2+ B3\\ B4\\ C4\\ D1\\");
	assertTrue(t.getNumberOfBlackCorners()==4);
    }

    @Test
    public void testGetNumberOfBlackCorners9() throws IllegalMoveException {
        t.makeMoves("@0/ A2/ @2/ @2/ @2/ @2/ @2/ @2/");
	assertTrue(t.getNumberOfBlackCorners()==2);
    }

    @Test
    public void testGetNumberOfBlackCorners10() throws IllegalMoveException {
        t.makeMoves("@0/ @1/ @1/ @1/ @1/ @1/ @1/ @1/");
	assertTrue(t.getNumberOfBlackCorners()==0);
    }

    @Test
    public void testIsGameOver1() throws IllegalMoveException {
        t.makeMoves("a1s @1+ c1u b2\\ d2u c3+ c1r b0\\ a1u @2+ d1s f6\\ d7r d0/ e2d f2\\ e4s");
        assertTrue(t.isGameOver() == Traxboard.WHITE);
    }

    @Test
    public void testGetBorder() throws IllegalMoveException {
	t.makeMoves("a1s 1as 1as a1d c2d");
	assertTrue(t.getBorder().equals("WW-W+B-WW-WW-W+B-WW"));
    }

    @Test
    public void testUniqueMoves1() throws IllegalMoveException {
        t.makeMoves("a1c b1s");
        assertTrue(t.uniqueMoves().size() == 17);
    }

    @Test
    public void testUniqueMoves2() throws IllegalMoveException {
        t.makeMoves("@0/ B1/ @1/ A2/ C0+ @3\\ A2+ A4\\ B5+ E5+ E1/ E0+ E0+ B8\\");
        assertTrue(t.uniqueMoves().size() == 43);
    }

    @Test
    public void testUniqueMoves3() throws IllegalMoveException {
        t.makeMoves("a1s a1s a1s a1s a1s c2s");
        assertTrue(t.uniqueMoves().size() ==  15);
    }


    @Test
    public void testUniqueMoves4() throws IllegalMoveException {
        t.makeMoves("@0/ @1/ @1/ C2+ C0/ D2\\ A0+ @3/ F3\\ F4\\ F5\\ G5\\ F6\\ D7/ C6+ B6/");
	if (t.uniqueMoves().size()!=65) {
	  System.out.println("\n"+t);
	  for (String m : t.uniqueMoves()) { System.out.print(m+" "); }
	}
        assertTrue(t.uniqueMoves().size() ==  65);
    }

    @Test
    public void testUniqueMoves5() throws IllegalMoveException {
        t.makeMoves("a1c 1ar a1d 1ar");
	assertTrue(t.uniqueMoves().size() == 11);
    }

    @Test
    public void testUniqueMoves6() throws IllegalMoveException {
        t.makeMoves("a1c a1d");
	assertTrue(t.uniqueMoves().size() == 9);
    }

    @Test
    public void testUniqueMoves7() throws IllegalMoveException {
        t.makeMoves("@0/ @1/ @1/ C2+ C0/ D2\\ A0+ @3/ F3\\ F4\\ F5\\ @1\\ B0/ G7+ @2/ F8/ E7+ E8+ D7+ B5+ B7\\ A5/ B1+ H8\\ A8+ G2/");
	assertTrue(t.uniqueMoves().size() == 12); 
    }

    @Test
    public void testUniqueMoves8() throws IllegalMoveException {
        t.makeMoves("@0/ A0/ B2/ C2/ D2/ D1\\ D0+ D0+ D0/ C1+ B1/ A1+ A2/");
	assertTrue(t.uniqueMoves().size() == 57); 
    }

    @Test
    public void testUniqueMoves9() throws IllegalMoveException {
        t.makeMoves("a1c b1s");
	assertTrue(t.uniqueMoves().size() == 17); 
    }

    @Test
    public void testUniqueMoves10() throws IllegalMoveException {
        t.makeMoves("@0/ B1/ @1/ A2/ C0+ @3\\ A2+ A4\\ B5+ E5+ E1/ E0+ E0+ B8\\");
	assertTrue(t.uniqueMoves().size() == 43); 
    }

    @Test
    public void testUniqueMoves11() throws IllegalMoveException {
        t.makeMoves("@0+ A0/ A3\\ B1/ B0/ C4+ @3/ B0+ A1\\ @4/ B0+ A2+ @3+ D0/ E1/ F1/ G4\\ B8/ A6/ @6/ H5\\ G8+ B3+");
	if (t.uniqueMoves().size()!=29) {
	  System.out.println("\n"+t);
	  for (String m : t.uniqueMoves()) { System.out.print(m+" "); }
	}
	assertTrue(t.uniqueMoves().size() == 29); 
    }

    @Test
    public void testUniqueMoves12() throws IllegalMoveException {
	t.makeMoves("@0+ B1+ B0+ A1\\ A0\\ @2+ D1\\ C0+ E2+ E0\\ D5\\ E5\\ B1/ F1/ F0/");
	if (t.uniqueMoves().size()!=52) {
	  System.out.println("\n"+t);
	  for (String m : t.uniqueMoves()) { System.out.print(m+" "); }
	}
	assertTrue(t.uniqueMoves().size() == 52);
    }

    @Test
    public void testUniqueMoves13() throws IllegalMoveException {
	t.makeMoves("@0/ @1/ @1/ D1/ A0\\ @1\\ C0/ C4+ F4+ F5\\ G5\\ D6+ D7\\ @2/");
	if (t.uniqueMoves().size()!=44) {
	    System.out.println("\n"+t);
	    for (String m : t.uniqueMoves()) { System.out.print(m+" "); }
	}
	assertTrue(t.uniqueMoves().size()==44);
    }

    @Test
    public void testUniqueMoves14() throws IllegalMoveException {
	t.makeMoves("@0+ B1+ C1/ C0/ B0/ B4/ B5/ C4+ A6+ @6/ E4\\ @5+ C1\\ C0\\ C0\\ B1\\ @6/ A5+ @5\\ F1/ A7+ H8+ A4/ C3\\");
	if (t.uniqueMoves().size()!=22) {
	    System.out.println("\n"+t);
	    for (String m : t.uniqueMoves()) { System.out.print(m+" "); }
	}
	assertTrue(t.uniqueMoves().size()==22);
    }

    @Test
    public void testUniqueMoves15() throws IllegalMoveException {
	t.makeMoves("@0+ A0+ @1+ A0+ A0/ B5+ B6+ A0\\ @2/ B0\\ A2\\ @2\\ D2\\ @2/ B3+ F7\\ D8/ F4\\ C8\\ C7+ G3/ G2+ B8/ A3+ A4/ A5/ G5+ H5/ H2/ C1\\");
	if (t.uniqueMoves().size()!=20) {
	    System.out.println("\n"+t);
	    for (String m : t.uniqueMoves()) { System.out.print(m+" "); }
	}
	assertTrue(t.uniqueMoves().size()==20);
    }

    @Test
    public void testUniqueMoves16() throws IllegalMoveException {
	t.makeMoves("@0+ @1/ A2+ @1\\ C3+ @1\\ D4/ C3+ @1\\ D5\\ C5\\ B0/ B6+ E7+ B7+ D1/ @1+ B7+ A7\\ A6\\ @6\\");
	if (t.uniqueMoves().size()!=85) {
	    System.out.println("\n"+t);
	    for (String m : t.uniqueMoves()) { System.out.print(m+" "); }
	}
	assertTrue(t.uniqueMoves().size()==85);
    }

    @Test
    public void testUniqueMoves17() throws IllegalMoveException {
	t.makeMoves("@0/ @1/ @1/ @1/ @1/ @1/ @1/ A0/ A0/ A0/ A0/ A0/ A0/ B1\\ C1\\ D1\\ E1\\ F1\\ G1\\ G2/ G3/ G4/ F4\\");
	if (t.uniqueMoves().size()!=119) {
	    System.out.println("\n"+t.uniqueMoves().size()+"\n"+t);
	    for (String m : t.uniqueMoves()) { System.out.print(m+" "); }
	}
	assertTrue(t.uniqueMoves().size()==119);
    }


    @Test
    public void testUniqueMovesWithMirrors1() throws IllegalMoveException {
        t.makeMoves("@0+ B1+");
	if (t.uniqueMoves_with_mirrors().size()!=14) {
	    System.out.println();
	    System.out.println(t);
	    for (String m : t.uniqueMoves_with_mirrors()) { System.out.println(m); }
	}
	assertTrue("size: "+t.uniqueMoves_with_mirrors().size(),t.uniqueMoves_with_mirrors().size() == 14); 
    }


    @Test
    public void testgetNumOfTiles1() throws IllegalMoveException {
        t.makeMoves("@0+ B1+ A0/");
        assertTrue(t.getNumOfTiles() == 4);
    }

    @Test
    public void testGetBorder1() throws Exception {
        t.makeMoves("a1s b1s 1as");
        assertTrue(t.getBorder().equals("BB-WW-B-W+B-W"));
    }

    @Test
    public void testGetBorder2() throws IllegalMoveException {
        t.makeMoves("a1s 1as 1as a1d c2d");
        assertTrue(t.getBorder().equals("WW-W+B-WW-WW-W+B-WW"));
    }

    @Test
    public void testEquals1() throws IllegalMoveException {
        t.makeMoves("@0+ B1+");
        Traxboard t2=new Traxboard();
        t2.makeMoves("@0+ @1+");
	assertTrue(t.equals(t2));
    }

    @Test
    public void testEquals2() throws IllegalMoveException {
        t.makeMoves("@0+ B1+");
        Traxboard t2=new Traxboard();
        t2.makeMoves("@0+ @1+");
	assertTrue(t2.equals(t));
    }

    @Test
    public void testEquals3() throws IllegalMoveException {
        t.makeMoves("@0+ B1+");
        Traxboard t2=new Traxboard();
        t2.makeMoves("@0/ @1+");
	assertFalse(t.equals(t2));
    }

    @Test
    public void testEquals4() throws IllegalMoveException {
        t.makeMoves("@0+ B1+");
        Traxboard t2=new Traxboard();
        t2.makeMoves("@0/ @1+");
	assertFalse(t2.equals(t));
    }
}

/*
        t = new Traxboard();
        t.makeMoves("a1s 1as 1as a1d c2d");
        moves = t.uniqueMoves();
        if (moves.size() != 12) {
        System.err.println("Test 8: FAILED ("+moves.size()+")\n");
        result++;
        } else {
        System.err.println("Test 008: OK");
        }

        t = new Traxboard();
        t.makeMoves("a1c 1ar b2u b3l");
        moves = t.uniqueMoves();
        if (moves.size() != 11) {
        System.err.println("Test 009: FAILED ("+moves.size()+")\n");
        result++;
        } else {
        System.err.println("Test 009: OK");
        }

        t = new Traxboard();
        t.makeMoves("a1c 1ar b2u b3l c3u c4l");
        moves = t.uniqueMoves();
        if (moves.size() != 13) {
        System.err.println("Test 010: FAILED ("+moves.size()+")\n");
        result++;
        } else {
        System.err.println("Test 010: OK");
        }

        t = new Traxboard();
        t.makeMoves("a1s 1al a3r b3s @1+");
        moves = t.uniqueMoves();
        if (moves.size() != 12) {
        System.err.println("Test 011: FAILED ("+moves.size()+")\n");
        result++;
        } else {
        System.err.println("Test 011: OK");
        }

        t = new Traxboard();
        t.makeMoves("a1s 1as 1as 1as 1as");
        moves = t.uniqueMoves();
        if (moves.size() != 9) {
        System.err.println("Test 012: FAILED ("+moves.size()+")\n");
        result++;
        } else {
        System.err.println("Test 012: OK");
        }

        t = new Traxboard();
        t.makeMoves("a1s 1as 1as 1as");
        moves = t.uniqueMoves();
        if (moves.size() != 7) {
        System.err.println("Test 013: FAILED ("+moves.size()+")\n");
        result++;
        } else {
        System.err.println("Test 013: OK");
        }
        t=new Traxboard();
        t.makeMoves("a1c b1d");
        if (t.getNumberOfWhiteCorners()!=2) {
        System.err.println("Test 019: FAILED ("+t.getNumberOfWhiteCorners()+")");
        System.err.println(t);
        System.err.println(t.getBorder());
        result++;
        System.exit(1);
        } else {
        System.err.println("Test 019: OK");
        }
        if (t.getNumberOfBlackCorners()!=0) {
        System.err.println("Test 020: FAILED ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 020: OK");
        }

        t=new Traxboard();
        t.makeMoves("@0/ @1/ B0/");
        if (t.getNumberOfBlackCorners()!=2) {
        System.err.println(t);
        System.err.println(t.getBorder());
        System.err.println("Test 022: FAILED ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 022: OK");
        }
        if (t.getNumberOfWhiteCorners()!=2) {
        System.err.println("Test 023: FAILED ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 023: OK");
        }
        t=new Traxboard();
        if (t.getNumberOfBlackCorners()!=0) {
        System.err.println("Test 024: FAILED ("+t.getNumberOfBlackCorners()+")\n");
        System.err.println(t);
        result++;
        } else {
        System.err.println("Test 024: OK");
        }
        if (t.getNumberOfWhiteCorners()!=0) {
        System.err.println("Test 025: FAILED ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 025: OK");
        }
        t=new Traxboard();
        if (t.getNumberOfBlackCorners()!=0) {
        System.err.println("Test 026: FAILED ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 026: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ B1/ B0/ B0/ B0/ B0/ B0/ B0/ B0/");
        if (t.getNumberOfWhiteCorners()!=0) {
        System.err.println("Test 027: FAILED ("+t.getNumberOfWhiteCorners()+")\n");
        System.err.println(t);
        System.err.println(t.getBorder());
        result++;
        System.exit(1);
        } else {
        System.err.println("Test 027: OK");
        }
        t=new Traxboard();
        if (t.getNumberOfBlackCorners()!=2) {
        System.err.println("Test 028: FAILED ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 028: OK");
        }
        if (t.getNumberOfWhiteCorners()!=2) {
        System.err.println("Test 029: FAILED ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 029: OK");
        }
        t.makeMove("@2/");
        if (t.getNumberOfBlackCorners()!=0) {
        System.err.println("Test 030: FAILED ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 030: OK");
        }
        if (t.getNumberOfWhiteCorners()!=0) {
        System.err.println("Test 031: FAILED ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 031: OK");
        }
        t=new Traxboard();
        if (t.getNumberOfBlackCorners()!=0) {
        System.err.println("Test 032: FAILED ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 032: OK");
        }
        if (t.getNumberOfWhiteCorners()!=0) {
        System.err.println("Test 033: FAILED ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 033: OK");
        }
        t=new Traxboard();
        t.countCornersSimple();
        t.makeMoves("@0/ b1u c1s c1r");
        if (t.getNumberOfWhiteCorners()!=3) {
        System.err.println("Test 034: Failed ("+t.getNumberOfWhiteCorners()+t.getBorder()+")\n");
        result++;
        } else {
        System.err.println("Test 034: OK");
        }
        if (t.getNumberOfBlackCorners()!=1) {
        System.err.println(t);
        System.err.println(t.getBorder());
        System.err.println("Test 035: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 035: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ b1u c1s c1r");
        t=rotate(t);
        if (t.getNumberOfBlackCorners()!=1) {
        System.err.println("Test 036: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 036: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ b1u c1s c1r");
        t=rotate(t);
        if (t.getNumberOfWhiteCorners()!=3) {
        System.err.println("Test 037: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 037: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ b1u c1s c1r");
        t=rotate(t);
        t=rotate(t);
        if (t.getNumberOfBlackCorners()!=1) {
        System.err.println(t);
        System.err.println(t.getBorder());
        System.err.println("Test 038: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 038: OK");
        }
        if (t.getNumberOfWhiteCorners()!=3) {
        System.err.println("Test 039: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 039: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ b1u c1s c1r");
        t=rotate(t);
        t=rotate(t);
        t=rotate(t);
        if (t.getNumberOfBlackCorners()!=1) {
        System.err.println(t);
        System.err.println(t.getBorder());
        System.err.println("Test 040: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 040: OK");
        }
        if (t.getNumberOfWhiteCorners()!=3) {
        System.err.println("Test 041: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 041: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0+ @1/ @1/ C0/");
        if (t.getNumberOfBlackCorners()!=3) {
        System.err.println("Test 042: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 042: OK");
        }
        if (t.getNumberOfWhiteCorners()!=1) {
        System.err.println("Test 043: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 043: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0+ @1/ @1/ C0/");
        t=rotate(t);
        if (t.getNumberOfBlackCorners()!=3) {
        System.err.println("Test 044: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 044: OK");
        }
        if (t.getNumberOfWhiteCorners()!=1) {
        System.err.println("Test 045: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 045: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0+ @1/ @1/ C0/");
        t=rotate(t);
        t=rotate(t);
        if (t.getNumberOfBlackCorners()!=3) {
        System.err.println("Test 046: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 046: OK");
        }
        if (t.getNumberOfWhiteCorners()!=1) {
        System.err.println("Test 047: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 047: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0+ @1/ @1/ C0/");
        t=rotate(t);
        t=rotate(t);
        t=rotate(t);
        if (t.getNumberOfBlackCorners()!=3) {
        System.err.println("Test 048: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 048: OK");
        }
        if (t.getNumberOfWhiteCorners()!=1) {
        System.err.println("Test 049: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 049: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ B1\\ C1\\");
        if (t.getNumberOfBlackCorners()!=1) {
        System.err.println("Test 050: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 050: OK");
        }
        if (t.getNumberOfWhiteCorners()!=1) {
        System.err.println("Test 051: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 051: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ A0/ B1\\ C1\\");
        if (t.getNumberOfBlackCorners()!=2) {
        System.err.println("Test 052: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 052: OK");
        }
        if (t.getNumberOfWhiteCorners()!=1) {
        System.err.println("Test 053: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 053: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ A0\\ B1/ C1+");
        if (t.getNumberOfBlackCorners()!=1) {
        System.err.println("Test 054: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 054: OK");
        }
        if (t.getNumberOfWhiteCorners()!=1) {
        System.err.println("Test 055: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        System.err.println(t);
        System.err.println(t.getBorder());
        result++;
        } else {
        System.err.println("Test 055: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0+ B1+ C1/ B2\\ D1/ A0/ C0+ E1\\ @3/ C5/ C0/");
        if (t.getNumberOfBlackThreats()!=2) {
        System.err.println(t);
        System.err.println(t.getBorder());
        System.err.println("Test 056: Failed ("+t.getNumberOfBlackThreats()+")\n");
        result++;
        } else {
        System.err.println("Test 056: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ A0/ @2/ C1+ D1+ D0/");
        if (t.getNumberOfBlackCorners()!=2) {
        System.err.println(t);
        System.err.println(t.getBorder());
        System.err.println("Test 057: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 057: OK");
        }
        if (t.getNumberOfWhiteCorners()!=3) {
        System.err.println(t);
        System.err.println(t.getBorder());
        System.err.println("Test 058: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 058: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ A0/ @2/ C1/ D1+ E1+ E0/");
        if (t.getNumberOfBlackThreats()!=1) {
        System.err.println(t);
        System.err.println(t.getBorder());
        System.err.println("Test 059: Failed ("+t.getNumberOfBlackThreats()+")\n");
        result++;
        } else {
        System.err.println("Test 059: OK");
        }
        if (t.getNumberOfBlackCorners()!=3) {
        System.err.println(t);
        System.err.println(t.getBorder());
        System.err.println("Test 060: Failed ("+t.getNumberOfBlackCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 060: OK");
        }
        if (t.getNumberOfWhiteCorners()!=2) {
        System.err.println(t);
        System.err.println(t.getBorder());
        System.err.println("Test 061: Failed ("+t.getNumberOfWhiteCorners()+")\n");
        result++;
        } else {
        System.err.println("Test 061: OK");
        }
        t=new Traxboard();
        if (t.uniqueMoves().size()!=30) {
        System.err.println(t);
        t.uniqueMoves().forEach(m -> System.err.print(m+" "));
        System.err.println();
        System.err.println("Test 063: Failed ("+t.uniqueMoves().size()+")\n");
        result++;
        } else {
        System.err.println("Test 063: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0+ B1+ C1/ @1\\ B2\\ B0+");
        if (t.uniqueMoves().size()!=26) {
        System.err.println(t);
        t.uniqueMoves().forEach(m -> System.err.print(m+" "));
        System.err.println();
        System.err.println("Test 064: Failed ("+t.uniqueMoves().size()+")\n");
        result++;
        } else {
        System.err.println("Test 064: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/");
        if (t.uniqueMoves().size()!=6) {
        System.err.println(t);
        t.uniqueMoves().forEach(m -> System.err.print(m+" "));
        System.err.println();
        System.err.println("Test 065: Failed ("+t.uniqueMoves().size()+")\n");
        result++;
        } else {
        System.err.println("Test 065: OK");
        }
        t=new Traxboard();
        if (t.uniqueMoves().size()!=12) {
        System.err.println(t);
        t.uniqueMoves().forEach(m -> System.err.print(m+" "));
        System.err.println();
        System.err.println("Test 066: Failed ("+t.uniqueMoves().size()+")\n");
        result++;
        } else {
        System.err.println("Test 066: OK");
        }
        t=new Traxboard();
        t.makeMoves("@0/ B1+");
        if (t.getNumberOfBlackThreats()!=0) {
        System.err.println(t);
        System.err.println("Test 069: Failed ("+t.getNumberOfBlackThreats()+")\n");
        result++;
        } else {
        System.err.println("Test 069: OK");
        }
*/
