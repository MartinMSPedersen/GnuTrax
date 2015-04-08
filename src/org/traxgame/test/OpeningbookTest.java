package org.traxgame.main;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class OpeningbookTest {

    private Traxboard t;
    private Openingbook book;
    private Openingbook.BookValue bv;

    @Before
    public void setUp() { 
    	t=new Traxboard(); 
	book=new Openingbook();
	book.loadBook();
    }

    @Test
    public void neverPlay1() throws IllegalMoveException {
        t.makeMoves("a1c a1d b1l");
	bv=book.search(t);
	assertTrue(bv.neverPlay);
	assertFalse(bv.alwaysPlay);
    }

    @Test
    public void neverPlay2() throws IllegalMoveException {
    	t.makeMoves("@0/ A0/ A0/ A0/ A0/");
	bv=book.search(t);
	assertTrue(bv.neverPlay);
	assertFalse(bv.alwaysPlay);
    }

    @Test
    public void neverPlay3() throws IllegalMoveException {
    	t.makeMoves("@0/ A2/ A3/ A4/ A0/");
	bv=book.search(t);
	assertTrue(bv.neverPlay);
	assertFalse(bv.alwaysPlay);
    }

    @Test
    public void alwaysPlay1() throws IllegalMoveException {
    	t.makeMoves("@0+ B1+ C1/ C0/ B0/ B0/ A5/ D3/ E4+ D5\\ C6\\ F4\\ F5\\ E6\\ E7+ G5\\ G6\\ B0\\ A3+ H5/ H4/ C1/ D1/ D3+");
	bv=book.search(t);
	assertTrue(bv.alwaysPlay);
	assertFalse(bv.neverPlay);
    }

    @Test
    public void empty1() throws IllegalMoveException {
    	bv=book.search(new Traxboard());
	assertTrue(bv==null);
    }

}
