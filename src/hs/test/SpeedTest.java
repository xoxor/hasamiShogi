package hs.test;

import hs.representation.Board;
import hs.representation.Boards;
import hs.representation.MatrixBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SpeedTest {

	static int playedMoves = 0;
	static int maxBranchingFactor = 0;
	static double meanBranchingFactor = 0.0;
	static int[] lastMove = null;

	public static void main(String[] args) {
		final long SEED = 0L;
		// final long SEED = 11041970L;
		Random r = (SEED != 0) ? new Random(SEED) : new Random();

		final int MOVES_NUMBER = 2000000;

		Board b = new MatrixBoard();
		b.setInitialConfiguration();

		long matrixMillis = time(b, MOVES_NUMBER, r);

		System.out.println("---------- RESULTS ----------");
		System.out.println("TIME= " + matrixMillis + "ms");
		System.out.println("PLAYED_MOVES= " + playedMoves);
		System.out.println("MAX_BRANCHING_FACTOR= " + maxBranchingFactor);
		System.out.println("MEAN_BRANCHING_FACTOR= " + meanBranchingFactor);

		System.out.println();
		System.out.println("BOARD:");
		Boards.drawBoard(b);
		String esit = (b.getEsit() == Board.BLACK_VICTORY) ? "BLACK" : ((b
				.getEsit() == Board.WHITE_VICTORY) ? "WHITE" : "DRAW");
		System.out.println(esit + " WINS!");
		System.out.println("LAST_MOVE= " + Arrays.toString(lastMove));
	}

	private static long time(Board b, int movesNumber, Random r) {
		long begin = System.currentTimeMillis();

		for (int k = 0; k < movesNumber && !b.isGameFinished(); k++) {
			ArrayList<int[]> legalMoves = b.getLegalMoves();
			int size = legalMoves.size();
			maxBranchingFactor = (size > maxBranchingFactor) ? size
					: maxBranchingFactor;
			meanBranchingFactor += size;
			int i = r.nextInt(size);
			int[] move = legalMoves.get(i);
			b.move(move[0], move[1], move[2], move[3]);
			lastMove = move;
			playedMoves++;
		}

		long end = System.currentTimeMillis();
		meanBranchingFactor /= playedMoves;
		return end - begin;
	}

}
