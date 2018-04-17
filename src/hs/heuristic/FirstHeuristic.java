package hs.heuristic;

import hs.representation.Board;

public class FirstHeuristic implements HeuristicFunction {

	private int player;
	private int opponent;

	public FirstHeuristic(int player) {
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

	public double evaluate(Board b) {
		if (b.isGameFinished()) {
			return b.getUtility(player);
		}
		return (double) (b.getPiecesNumber(player) - b.getPiecesNumber(opponent)) / Board.INITIAL_PIECES_NUMBER;
	}

}
