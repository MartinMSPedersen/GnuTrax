package org.traxgame.main;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class ComputerPlayerAlphaBetaTest {

    private Traxboard t;
    private ComputerPlayerAlphaBeta cp;

    @Before
    public void setUp() { 
	t=new Traxboard(); 
	cp=new ComputerPlayerAlphaBeta();
    }

    @Test
    public void eval1() throws IllegalMoveException {
	t.makeMoves("@0/ B1\\ A2\\");
	assertTrue(cp.eval(t)==Integer.MIN_VALUE);
    }

    @Test
    public void eval2() throws IllegalMoveException {
	t.makeMoves("@0/ @1\\ A0/");
	assertTrue(cp.eval(t)==Integer.MAX_VALUE);
    }

    @Test
    public void eval3() throws IllegalMoveException {
	t.makeMoves("@0/ B1\\ A2+");
	assertTrue(cp.eval(t)==Integer.MIN_VALUE);
    }

    @Test
    public void eval4() throws IllegalMoveException {
	t.makeMoves("@0/ @1/ A2\\");
	assertTrue(cp.eval(t)==Integer.MIN_VALUE);
    }

    @Test
    public void eval5() throws IllegalMoveException {
	t.makeMoves("@0/ @1/ B0\\");
	assertTrue(cp.eval(t)==Integer.MIN_VALUE);
    }

    @Test
    public void eval6() throws IllegalMoveException {
	t.makeMoves("@0/ @1/ C1/ B2+ D1/ A0+ B4\\");
	assertFalse(cp.eval(t)==Integer.MIN_VALUE);
	assertFalse(cp.eval(t)==Integer.MAX_VALUE);
    }
}
