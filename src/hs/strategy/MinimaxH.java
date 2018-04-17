package hs.strategy;

import hs.heuristic.HeuristicFunction;
import hs.representation.Board;

import java.util.Iterator;

public class MinimaxH implements SearchStrategy {

	private int plyThreshold;
	private long timeThreshold;
	private HeuristicFunction h;

	private int[] decision;
	private double decisionValue;

	public MinimaxH(int movesThreshold, long timeThreshold, HeuristicFunction h) {
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
		return minimaxDecision(b);
	}

	private int[] minimaxDecision(Board b) {
		int depth = 0;
		decision = null;
		decisionValue = Double.NEGATIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double minValue = minValue(b, depth + 1);
			if (minValue > decisionValue) {
				decisionValue = minValue;
				decision = move;
			}
			b.unmove();
		}
		return decision;
	}

	private double minValue(Board b, int depth) {
		if (cutoff(b, depth)) {
			return h.evaluate(b, depth);
		}
		double v = Double.POSITIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double maxValue = maxValue(b, depth + 1);
			v = (maxValue < v) ? maxValue : v;
			b.unmove();
		}
		return v;
	}

	private double maxValue(Board b, int depth) {
		if (cutoff(b, depth)) {
			return h.evaluate(b, depth);
		}
		double v = Double.NEGATIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double minValue = minValue(b, depth + 1);
			v = (minValue > v) ? minValue : v;
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
