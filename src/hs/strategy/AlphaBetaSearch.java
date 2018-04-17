package hs.strategy;

import hs.representation.Board;

import java.util.Iterator;

public class AlphaBetaSearch implements SearchStrategy {

	private int myColor;
	private int[] decision;
	private double decisionValue;
	private int visited = 0;

	public AlphaBetaSearch(int myColor) {
		this.myColor = myColor;
	}

	@Override
	public int[] nextMove(Board b) {
		return alphaBetaDecision(b);
	}

	private int[] alphaBetaDecision(Board b) {
		visited = 0;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;

		decision = null;
		decisionValue = Double.NEGATIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double value = minValue(b, alpha, beta);
			if (value > decisionValue) {
				decisionValue = value;
				decision = move;
			}
			if (decisionValue >= beta) {
				b.unmove();
				return decision;
			}
			alpha = (decisionValue > alpha) ? decisionValue : alpha;
			b.unmove();
		}
		return decision;
	}

	private double maxValue(Board b, double alpha, double beta) {
		visited++;

		if (b.isGameFinished()) {
			return b.getUtility(myColor);
		}
		double v = Double.NEGATIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double minValue = minValue(b, alpha, beta);
			v = (minValue > v) ? minValue : v;
			if (v >= beta) {
				b.unmove();
				return v;
			}
			alpha = (v > alpha) ? v : alpha;
			b.unmove();
		}
		return v;
	}

	private double minValue(Board b, double alpha, double beta) {
		visited++;

		if (b.isGameFinished()) {
			return b.getUtility(myColor);
		}
		double v = Double.POSITIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double maxValue = maxValue(b, alpha, beta);
			v = (maxValue < v) ? maxValue : v;
			if (v <= alpha) {
				b.unmove();
				return v;
			}
			beta = (v < beta) ? v : beta;
			b.unmove();
		}
		return v;
	}

	@Override
	public int[] getLastMove() {
		// attention, aliasing!
		return decision;
	}

	@Override
	public double getLastValue() {
		return decisionValue;
	}

	@Override
	public long getTimeThreshold() {
		return Long.MAX_VALUE;
	}

}
