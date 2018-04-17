package hs.strategy;

import hs.heuristic.HeuristicFunction;
import hs.representation.Board;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MinimaxHRandom implements SearchStrategy {

	private int plyThreshold;
	private long timeThreshold;
	private HeuristicFunction h;
	private Random r;

	private int[] decision;
	private double decisionValue;

	public MinimaxHRandom(int movesThreshold, long timeThreshold,
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
		this.r = new Random();
	}

	@Override
	public int[] nextMove(Board b) {
		return minimaxDecision(b);
	}

	private int[] minimaxDecision(Board b) {
		int depth = 0;
		decisionValue = Double.NEGATIVE_INFINITY;

		ArrayList<int[]> legalMoves = b.getLegalMoves();
		int[] bestDecisions = new int[legalMoves.size()];
		int k = 0;
		for (int i = 0; i < legalMoves.size(); i++) {
			int[] move = legalMoves.get(i);
			b.move(move[0], move[1], move[2], move[3]);
			double minValue = minValue(b, depth + 1);
			if (minValue > decisionValue) {
				decisionValue = minValue;
				k = 0;
				bestDecisions[k++] = i;
			} else if (minValue == decisionValue) {
				bestDecisions[k++] = i;
			}
			b.unmove();
		}
		int decisionIndex = bestDecisions[r.nextInt(k)];
		decision = legalMoves.get(decisionIndex);
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
