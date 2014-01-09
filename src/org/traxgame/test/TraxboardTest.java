package org.traxgame.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.traxgame.IllegalMoveException;
import org.traxgame.Traxboard;

public class TraxboardTest {

	private Traxboard traxboard;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		traxboard = new Traxboard();
	}

	@Test
	public void testGetNumOfTiles() throws IllegalMoveException {
		assertEquals("0 tiles", 0, traxboard.getNumOfTiles());
		traxboard.makeMove("@0/");
		traxboard.makeMove("B1\\");
		traxboard.makeMove("B2/");
		assertEquals("4 tiles", 4, traxboard.getNumOfTiles());
	}

	@Test
	public void testGetRowSize() throws IllegalMoveException {
		assertEquals("0 rows", 0, traxboard.getRowSize());
		traxboard.makeMove("@0/");
		traxboard.makeMove("B1\\");
		traxboard.makeMove("B2/");
		assertEquals("2 rows", 2, traxboard.getRowSize());
	}

	@Test
	public void testGetColSize() throws IllegalMoveException {
		assertEquals("0 cols", 0, traxboard.getColSize());
		traxboard.makeMove("@0/");
		traxboard.makeMove("B1\\");
		traxboard.makeMove("B2/");
		assertEquals("2 cols", 2, traxboard.getColSize());
	}

	@Test
	public void testIsGameOver() throws IllegalMoveException {
		assertEquals("There should be no winner",  Traxboard.NOPLAYER, traxboard.isGameOver());
		traxboard.makeMove("@0/");
		traxboard.makeMove("B1\\");
		traxboard.makeMove("B2/");
		assertEquals("Black should have won",  Traxboard.BLACK, traxboard.isGameOver());
	}

	@Test
	public void testMakeMove() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetLegalTiles() throws IllegalMoveException {
		List<Integer> legalMoves = traxboard.getLegalTiles(0, 0);
		assertEquals("There should be only two moves", 2, legalMoves.size());

		traxboard.makeMove("@0/");
		traxboard.makeMove("B1/");
		legalMoves = traxboard.getLegalTiles(1, 3);
		assertEquals("There should be only 3 moves", 3, legalMoves.size());
	}

}
