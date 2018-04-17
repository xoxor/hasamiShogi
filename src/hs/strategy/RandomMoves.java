package hs.strategy;

import hs.representation.Board;

import java.util.ArrayList;
import java.util.Random;

public class RandomMoves implements SearchStrategy {

	private Random r;
	private int[] decision;

	public RandomMoves() {
		r = new Random();
	}

	public RandomMoves(long seed) {
		r = new Random(seed);
	}

	@Override
	public int[] nextMove(Board b) {
		ArrayList<int[]> moves = b.getLegalMoves();
		int size = moves.size();
		decision = moves.get(r.nextInt(size));
		return decision;
	}

	@Override
	public int[] getLastMove() {
		// attention, aliasing!
		return decision;
	}

	@Override
	public double getLastValue() {
		return Double.NaN;
	}

	@Override
	public long getTimeThreshold() {
		return Long.MAX_VALUE;
	}

}
