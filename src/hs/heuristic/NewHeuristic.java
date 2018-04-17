package hs.heuristic;

import hs.representation.Board;

public class NewHeuristic implements HeuristicFunction {

	private int player;
	private int opponent;

	public NewHeuristic(int player) {
		if (player != Board.BLACK && player != Board.WHITE) {
			throw new IllegalArgumentException();
		}
		this.player = player;
		this.opponent = 1 - player;
	}

	@Override
	public double evaluate(Board b, int depth) {
		return evaluate(b) / depth;
	}

	private double evaluate(Board b) {
		if (b.isGameFinished()) {
			return b.getUtility(player) * 1000;
		}
		double vertical;
		double diagonal;
		double mypieces = b.getPiecesNumber(player);
		double pieces = mypieces - b.getPiecesNumber(opponent);
		if (mypieces < 5) {
			vertical = -checkVertical(b, opponent);
			diagonal = -checkDiagonal(b, opponent);
		} else {
			vertical = checkVertical(b, player) - checkVertical(b, opponent);
			diagonal = checkDiagonal(b, player) - checkDiagonal(b, opponent);
		}
		return (1 / 2 * vertical + diagonal + 5 * pieces);
	}

	private int checkVertical(Board b, int player) {
		final int northOffset = (player == Board.BLACK) ? 2 : 0;
		final int southOffset = (player == Board.BLACK) ? 8 : 6;

		final int point = 5;

		int val = 0;
		for (int j = 0; j < 9; j++) {
			int m = 1;
			for (int i = northOffset; i <= southOffset; i++) {
				if (b.get(i, j) == player) {
					val += m * point;
					m++;
				} else {
					m = 1;
				}
			}
		}
		return val;
	}

	private double checkDiagonal(Board b, int player) {
		final int northOffset = (player == Board.BLACK) ? 2 : 0;
		final int southOffset = (player == Board.BLACK) ? 8 : 6;

		final int point = 5;

		int val = 0;
		for (int i = northOffset + 2; i > northOffset;) {
			for (int j = 0; j < 5;) {
				int m = 1;
				for (int k = i, z = j; k <= southOffset && z < 9; k++, z++) {
					if (b.get(k, z) == player) {
						val += m * point;
						m++;
					} else {
						m = 1;
					}
				}
				if (i == northOffset) {
					j++;
				} else {
					i--;
				}
			}
		}

		for (int i = northOffset + 2; i > northOffset;) {
			for (int j = 8; j > 3;) {
				int m = 1;
				for (int k = i, z = j; k <= southOffset && z >= 0; k++, z--) {
					if (b.get(k, z) == player) {
						val += m * point;
						m++;
					} else {
						m = 1;
					}
				}
				if (i == northOffset) {
					j--;
				} else {
					i--;
				}
			}
		}
		return val;
	}

}
