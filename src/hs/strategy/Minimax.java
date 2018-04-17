package hs.strategy;

import hs.representation.Board;

import java.util.Iterator;

public class Minimax implements SearchStrategy {

	private int myColor;
	private int[] decision;
	private double decisionValue;

	public Minimax(int myColor) {
		this.myColor = myColor;
	}

	@Override
	public int[] nextMove(Board b) {
		return minimaxDecision(b);
	}

	private int[] minimaxDecision(Board b) {
		decision = null;
		decisionValue = Double.NEGATIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double minValue = minValue(b);
			if (minValue > decisionValue) {
				decisionValue = minValue;
				decision = move;
			}
			b.unmove();
		}
		return decision;
	}

	private double minValue(Board b) {
		if (isTerminal(b)) {
			return b.getUtility(myColor);
		}
		double v = Double.POSITIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double maxValue = maxValue(b);
			v = (maxValue < v) ? maxValue : v;
			b.unmove();
		}
		return v;
	}

	private double maxValue(Board b) {
		if (isTerminal(b)) {
			return b.getUtility(myColor);
		}
		double v = Double.NEGATIVE_INFINITY;
		Iterator<int[]> legalMovesIt = b.legalMovesIterator();
		while (legalMovesIt.hasNext()) {
			int[] move = legalMovesIt.next();
			b.move(move[0], move[1], move[2], move[3]);
			double minValue = minValue(b);
			v = (minValue > v) ? minValue : v;
			b.unmove();
		}
		return v;
	}

	private boolean isTerminal(Board b) {
		return b.isGameFinished();
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
