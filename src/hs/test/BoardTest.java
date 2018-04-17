package hs.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import hs.representation.Board;
import hs.representation.IllegalMoveException;
import hs.representation.MatrixBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {

	private Board b;

	@Before
	public void runOnceBeforeEachTest() {
		b = new MatrixBoard();
		b.setInitialConfiguration();
	}

	@After
	public void runOnceAfterEachTest() {
		b = null;
	}

	@Test
	public void testInitialConfiguration() {
		for (int i = 0; i <= 1; i++) {
			for (int j = 0; j <= 8; j++) {
				assertEquals(Board.BLACK, b.get(i, j));
			}
		}
		for (int i = 2; i <= 6; i++) {
			for (int j = 0; j <= 8; j++) {
				assertEquals(Board.EMPTY, b.get(i, j));
			}
		}
		for (int i = 7; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				assertEquals(Board.WHITE, b.get(i, j));
			}
		}
	}

	@Test(expected = IllegalMoveException.class)
	public void testMoveInOpponentsTurn() {
		// can't move in opponent's turn - black moves first
		assertEquals(Board.BLACK, b.currentTurn());
		b.move(7, 0, 6, 0);
	}

	@Test(expected = IllegalMoveException.class)
	public void testMoveToOccupiedPosition() {
		// can't move to occupied positions
		b.move(1, 5, 7, 5);
	}

	@Test(expected = IllegalMoveException.class)
	public void testMoveOutOfCross() {
		assertEquals(Board.BLACK, b.currentTurn());
		b.move(1, 5, 5, 5);
		b.move(7, 0, 6, 0);
		// can't move out of the cross
		b.move(5, 5, 6, 6);
	}

	@Test
	public void testCanMoveToLegalPositions() {
		assertEquals(Board.BLACK, b.currentTurn());
		for (int k = 0; k < 9; k++) {
			b.move(0, k, 2, k);
			if (k % 2 == 0) {
				b.move(7, 0, 6, 0);
			} else {
				b.move(6, 0, 7, 0);
			}
		}
		for (int i = 2; i <= 5; i++) {
			b.setInitialConfiguration();
			for (int j = 0; j < 9; j++) {
				b.move(1, j, i, j);
				if (j % 2 == 0) {
					b.move(7, 0, 6, 0);
				} else {
					b.move(6, 0, 7, 0);
				}
			}
		}
	}

	@Test(expected = IllegalMoveException.class)
	public void testJumpOverTooDistantPiece() {
		assertEquals(Board.BLACK, b.currentTurn());
		b.move(1, 5, 6, 5);
		assertEquals(Board.WHITE, b.currentTurn());
		b.move(7, 5, 4, 5);
	}

	@Test
	public void noHorizontalVictory() {
		for (int j = 0; j < 5; j++) {
			b.move(1, j, 4, j);
			if (j < 4) {
				b.move(7, j, 5, j);
			}
		}
		assertThat(b.isGameFinished(), is(false));
	}

	@Test
	public void noBlackVictoryIfInHome() {
		b.move(1, 2, 2, 2);
		b.move(7, 7, 6, 7);
		b.move(1, 3, 3, 3);
		b.move(7, 6, 5, 6);
		b.move(1, 4, 4, 4);

		assertThat(b.isGameFinished(), is(false));
	}

	@Test
	public void blackVerticalVictory() {
		b.move(1, 2, 3, 2);
		b.move(7, 0, 6, 0);

		b.move(0, 2, 2, 2);
		b.move(6, 0, 7, 0);

		b.move(1, 1, 5, 1);
		b.move(7, 0, 6, 0);

		b.move(0, 1, 4, 1);
		b.move(6, 0, 7, 0);

		b.move(5, 1, 5, 2);
		b.move(7, 0, 6, 0);

		b.move(4, 1, 4, 2);
		b.move(6, 0, 7, 0);

		b.move(1, 3, 6, 3);
		b.move(7, 0, 5, 0);

		b.move(6, 3, 6, 2);

		assertThat(b.isGameFinished(), is(true));
		assertEquals(Board.BLACK_VICTORY, b.getEsit());
	}

	@Test
	public void blackDiagonalVictory() {
		b.move(1, 0, 6, 0);
		b.move(7, 8, 6, 8);

		b.move(1, 1, 5, 1);
		b.move(6, 8, 7, 8);

		b.move(1, 2, 4, 2);
		b.move(7, 8, 6, 8);

		b.move(1, 3, 3, 3);
		b.move(6, 8, 7, 8);

		b.move(1, 4, 2, 4);

		assertThat(b.isGameFinished(), is(true));
		assertEquals(Board.BLACK_VICTORY, b.getEsit());
	}

	@Test
	public void blackInverseDiagonalVictory() {
		b.move(1, 0, 2, 0);
		b.move(7, 8, 6, 8);

		b.move(1, 1, 3, 1);
		b.move(6, 8, 7, 8);

		b.move(1, 2, 4, 2);
		b.move(7, 8, 6, 8);

		b.move(1, 3, 5, 3);
		b.move(6, 8, 7, 8);

		b.move(1, 4, 6, 4);

		assertThat(b.isGameFinished(), is(true));
		assertEquals(Board.BLACK_VICTORY, b.getEsit());
	}

	@Test
	public void horizontalSandwich() {
		b.move(1, 5, 5, 5);
		b.move(7, 4, 5, 4);

		b.move(1, 6, 5, 6);
		b.move(7, 7, 5, 7);

		assertEquals(Board.EMPTY, b.get(5, 5));
		assertEquals(Board.EMPTY, b.get(5, 6));
	}

	@Test
	public void verticalSandwich() {
		b.move(1, 2, 5, 2);
		b.move(7, 2, 6, 2);

		b.move(0, 2, 4, 2);
		b.move(7, 1, 3, 1);

		b.move(1, 8, 2, 8);
		b.move(3, 1, 3, 2);

		assertEquals(Board.EMPTY, b.get(4, 2));
		assertEquals(Board.EMPTY, b.get(5, 2));
	}

	@Test
	public void printLegalMoves() {
		assertThat(b.getLegalMoves().size(), is(54));
		// for (int[] move : b.getLegalMoves()) {
		// System.out.println(Arrays.toString(move));
		// }
	}

	// @Test
	// public void printItMoves() {
	// System.out.println("BEGIN PRINT TEST");
	// System.out.println();
	// Iterator<int[]> legalMovesIt = b.legalMovesIterator();
	// while (legalMovesIt.hasNext()) {
	// System.out.println(Arrays.toString(legalMovesIt.next()));
	// }
	// System.out.println();
	// System.out.println("END PRINT TEST");
	// }

	@Test
	public void legalMovesIteratorTest() {
		ArrayList<int[]> legalMovesArray = b.getLegalMoves();
		assertThat(legalMovesArray.size(), is(54));
		Iterator<int[]> trusted = legalMovesArray.iterator();
		Iterator<int[]> suspected = b.legalMovesIterator();
		System.out.println("BEGIN TEST 1");
		while (trusted.hasNext()) {
			assertThat(suspected.hasNext(), is(true));

			int[] trustedMove = trusted.next();
			int[] suspectedMove = suspected.next();

			System.out.println(Arrays.toString(trustedMove));
			System.out.println(Arrays.toString(suspectedMove));

			assertEquals(trustedMove[0], suspectedMove[0]);
			assertEquals(trustedMove[1], suspectedMove[1]);
			assertEquals(trustedMove[2], suspectedMove[2]);
			assertEquals(trustedMove[3], suspectedMove[3]);
		}
		assertThat(suspected.hasNext(), is(false));
		System.out.println("END TEST 1");
	}

	@Test
	public void legalMovesIteratorTest2() {
		int[][] board = new int[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board[i][j] = Board.EMPTY;
			}
		}
		board[0][0] = Board.BLACK;
		board[5][8] = Board.BLACK;
		board[8][7] = Board.WHITE;
		board[8][8] = Board.WHITE;
		b.setCustomizedConfiguration(board, Board.BLACK);

		ArrayList<int[]> legalMovesArray = b.getLegalMoves();
		// assertThat(legalMovesArray.size(), is(54));
		Iterator<int[]> trusted = legalMovesArray.iterator();
		Iterator<int[]> suspected = b.legalMovesIterator();
		System.out.println("BEGIN TEST");
		System.out.println();
		while (trusted.hasNext()) {
			assertThat(suspected.hasNext(), is(true));

			int[] trustedMove = trusted.next();
			int[] suspectedMove = suspected.next();

			System.out.println("t= " + Arrays.toString(trustedMove));
			System.out.println("s= " + Arrays.toString(suspectedMove));

			assertEquals(trustedMove[0], suspectedMove[0]);
			assertEquals(trustedMove[1], suspectedMove[1]);
			assertEquals(trustedMove[2], suspectedMove[2]);
			assertEquals(trustedMove[3], suspectedMove[3]);
		}
		assertThat(suspected.hasNext(), is(false));
		System.out.println();
		System.out.println("END TEST");
	}

	@Test
	public void testMoveUnmove() { // test for the move/unmove combination
		assertEquals(Board.BLACK, b.currentTurn());
		b.move(1, 5, 2, 5);
		b.move(7, 1, 6, 1);
		b.move(2, 5, 5, 5);
		assertEquals(Board.BLACK, b.get(5, 5));
		assertEquals(Board.EMPTY, b.get(2, 5));
		assertEquals(b.currentTurn(), Board.WHITE);
		assertEquals(b.isGameFinished(), false);
		assertEquals(b.getPiecesNumber(Board.BLACK), 18);
		assertEquals(b.getPiecesNumber(Board.WHITE), 18);
		b.unmove();
		assertEquals(Board.EMPTY, b.get(5, 5));
		assertEquals(Board.BLACK, b.get(2, 5));
		assertEquals(b.currentTurn(), Board.BLACK);
		assertEquals(b.isGameFinished(), false);
		assertEquals(b.getPiecesNumber(Board.BLACK), 18);
		assertEquals(b.getPiecesNumber(Board.WHITE), 18);
	}

}
