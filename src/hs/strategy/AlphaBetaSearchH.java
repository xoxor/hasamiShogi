package hs.strategy;

import hs.heuristic.HeuristicFunction;
import hs.representation.Board;

import java.util.Iterator;

public class AlphaBetaSearchH implements SearchStrategy {

	private int plyThreshold;
	private long timeThreshold;
	private HeuristicFunction h;

	private int[] decision;
	private double decisionValue;
	private int visited = 0;

	public AlphaBetaSearchH(int movesThreshold, long timeThreshold,
			HeuristicFunction h) {
		if (movesThreshold <= 0) {
			throw new IllegalArgumentException("Moves must be positive! "
					+ movesThreshold);
		}
		if (timeThreshold <= 0) {
			throw new IllegalArgumentException("Time must be positive! "
					+ timeThreshold);
		}
		if (h == null) {
			throw new NullPointerException();
		}
		this.plyThreshold = movesThreshold; // *2
		this.timeThreshold = timeThreshold;
		this.h = h;
	}

	@Override
	public int[] nextMove(Board b) {
		return alphaBetaDecision(b);
	}

	private int[] alphaBetaDecision(Board b) {
		visited = 0;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;

		int depth = 0;
		decision = null;
		decisionValue = Double.NEGATIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double value = minValue(b, alpha, beta, depth + 1);
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

	private double maxValue(Board b, double alpha, double beta, int depth) {
		visited++;
		if (cutoff(b, depth)) {
			return h.evaluate(b, depth);
		}
		double v = Double.NEGATIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double minValue = minValue(b, alpha, beta, depth + 1);
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

	private double minValue(Board b, double alpha, double beta, int depth) {
		visited++;
		if (cutoff(b, depth)) {
			return h.evaluate(b, depth);
		}
		double v = Double.POSITIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double maxValue = maxValue(b, alpha, beta, depth + 1);
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

	private boolean cutoff(Board b, int depth) {
		return depth == plyThreshold || b.isGameFinished();
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
		return this.timeThreshold;
	}

}
