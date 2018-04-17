package hs.strategy;

import hs.Timer;
import hs.heuristic.HeuristicFunction;
import hs.representation.Board;

import java.util.Iterator;

public class IDAlphaBetaSearchH implements SearchStrategy {

	private int plyThreshold;
	private long timeThreshold;
	private HeuristicFunction h;

	private boolean exit;
	private int[] decision;
	private double decisionValue;
	private int visited = 0;
	private int maxDepth = 0;

	private Timer timer;

	public IDAlphaBetaSearchH(int movesThreshold, long timeThreshold,
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
		this.timer = new Timer(this.timeThreshold);
		this.h = h;
	}

	@Override
	public int[] nextMove(Board b) {
		timer.start();
		return alphaBetaDecision(b);
	}

	public long getTimeThreshold() {
		return this.timeThreshold;
	}

	private int[] alphaBetaDecision(Board b) {
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;

		decision = null;
		decisionValue = Double.NEGATIVE_INFINITY;

		int depth = 0;
		int depthLimit = 0;
		int lastDepth = 0;
		maxDepth = 0;
		exit = false;
		do {
			visited = 0;
			alpha = Double.NEGATIVE_INFINITY;
			beta = Double.POSITIVE_INFINITY;

			depthLimit++;
			int[] newDecision = null;
			double newDecisionValue = Double.NEGATIVE_INFINITY;

			Iterator<int[]> legalMovesIt = b.legalMovesIterator();
			while (legalMovesIt.hasNext()) {
				if (decision != null && timer.isExpired()) {
					exit = true;
					break;
				}

				int[] move = legalMovesIt.next();
				b.move(move[0], move[1], move[2], move[3]);
				double value = minValue(b, alpha, beta, depth + 1, depthLimit);
				if (value > newDecisionValue) {
					newDecisionValue = value;
					newDecision = move;
				}
				if (newDecisionValue >= beta) {
					b.unmove();
					return newDecision;
				}
				alpha = (newDecisionValue > alpha) ? newDecisionValue : alpha;
				b.unmove();
			}

			if (!exit) {
				decision = newDecision;
				decisionValue = newDecisionValue;
			}
			if (lastDepth == maxDepth) {
				timer.stop();
				return decision;
			} else {
				lastDepth = maxDepth;
			}
		} while (!exit);
		return decision;
	}

	private double maxValue(Board b, double alpha, double beta, int depth,
			int depthLimit) {
		visited++;
		if (depth > maxDepth) {
			maxDepth = depth;
		}

		if (cutoff(b, depth, depthLimit)) {
			return h.evaluate(b, depth);
		}
		double v = Double.NEGATIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext() && !exit) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double minValue = minValue(b, alpha, beta, depth + 1, depthLimit);
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

	private double minValue(Board b, double alpha, double beta, int depth,
			int depthLimit) {
		visited++;
		if (depth > maxDepth) {
			maxDepth = depth;
		}

		if (cutoff(b, depth, depthLimit)) {
			return h.evaluate(b, depth);
		}
		double v = Double.POSITIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext() && !exit) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double maxValue = maxValue(b, alpha, beta, depth + 1, depthLimit);
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

	private boolean cutoff(Board b, int depth, int depthLimit) {
		if (timer.isExpired()) {
			exit = true;
			return true;
		}
		return depth >= depthLimit || depth == plyThreshold
				|| b.isGameFinished();
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

}