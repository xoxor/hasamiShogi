package hs.heuristic;

import hs.representation.Board;

public interface HeuristicFunction {

	double evaluate(Board b, int depth);

}
