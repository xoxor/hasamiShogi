package hs.strategy;

import hs.representation.Board;

public interface SearchStrategy {

	int[] nextMove(Board b);

	int[] getLastMove();
	
	double getLastValue();
	
	long getTimeThreshold();
	
}
