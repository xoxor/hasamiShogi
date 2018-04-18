package hs.test;

import hs.heuristic.*;
import hs.representation.Board;
import hs.strategy.*;

public class Tests {

	static MinimaxH p1 = new MinimaxH(4, 1L, new FirstHeuristic(Board.BLACK));
	static AlphaBetaSearchH p2 = new AlphaBetaSearchH(4, 1000, new NewHeuristic(Board.BLACK));
	static RandomMoves p3 = new RandomMoves();
	static MonteCarloTreeSearch p4 = new MonteCarloTreeSearch(200, 1000L, new NewHeuristic(Board.WHITE), 1D, Board.WHITE);
	static IDAlphaBetaSearchH_BestMove p5 = new IDAlphaBetaSearchH_BestMove(Integer.MAX_VALUE, 1000L, new NewHeuristic(Board.BLACK));
	
	
	public static void main(String[] args) {
		GameTest g = new GameTest(p2, p1);
		// GameTest g = new GameTest(BEA, true) //per dare il nero a human;
		g.setSleepMove(10);
		g.setMaxDepth(60);
		g.play();
		// g.play(true); board customizer
	}

}
