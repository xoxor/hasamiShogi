package hs.test;

import hs.heuristic.FirstHeuristic;
import hs.heuristic.NewHeuristic;
import hs.representation.Board;
import hs.strategy.AlphaBetaSearch;
import hs.strategy.AlphaBetaSearchH;
import hs.strategy.IDAlphaBetaSearchH;
import hs.strategy.IDAlphaBetaSearchH_BestMove;
import hs.strategy.IDAlphaBetaSearchH_RandomMoves;
import hs.strategy.Minimax;
import hs.strategy.MinimaxH;
import hs.strategy.MonteCarloTreeSearch;
import hs.test.TestBoard.Move;
import hs.util.NodeFSNB;
import hs.util.TreeFSNB;

public class StrategyTest {

	static TreeFSNB<Move> game = new TreeFSNB<>();
	static final int MINIMAX = 0, MINIMAXH = 1, MINIMAXH_ITER = 2;
	static int test = MINIMAX;

	public static void main(String[] args) {
		// first level
		NodeFSNB<Move> n1 = game.addRoot(new Move(-1, Integer.MIN_VALUE));
		// second level
		NodeFSNB<Move> n2 = game.addChild(n1, new Move(1, Integer.MAX_VALUE));
		NodeFSNB<Move> n3 = game.addChild(n1, new Move(2, Integer.MAX_VALUE));
		NodeFSNB<Move> n4 = game.addChild(n1, new Move(3, Integer.MAX_VALUE));
		// third level
		NodeFSNB<Move> n7 = game.addChild(n2, new Move(7, 2));
		NodeFSNB<Move> n6 = game.addChild(n2, new Move(6, 5));
		NodeFSNB<Move> n5 = game.addChild(n2, new Move(5, 14));
	
		NodeFSNB<Move> n10 = game.addChild(n3, new Move(10, 6));
		NodeFSNB<Move> n9 = game.addChild(n3, new Move(9, 4));
		NodeFSNB<Move> n8 = game.addChild(n3, new Move(8, 2));
		
		NodeFSNB<Move> n13 = game.addChild(n4, new Move(13, 8));
		NodeFSNB<Move> n12 = game.addChild(n4, new Move(12, 12));
		NodeFSNB<Move> n11 = game.addChild(n4, new Move(11, 3));

		TestBoard tb = new TestBoard(game);

		System.out.println("RISULTATI");

		System.out.println();
		System.out.println("MINIMAX");
		Minimax mm = new Minimax(Board.BLACK);
		while (!tb.isGameFinished()) {
			int[] next = mm.nextMove(tb);
			System.out.println(next[0]);
			tb.move(next);
		}
		tb.setInitialConfiguration();

		System.out.println();
		System.out.println("MINIMAXH");
		MinimaxH mmh = new MinimaxH(10, 1L, new FirstHeuristic(Board.BLACK));
		while (!tb.isGameFinished()) {
			int[] next = mmh.nextMove(tb);
			System.out.println(next[0]);
			tb.move(next);
		}
		tb.setInitialConfiguration();


		System.out.println();
		System.out.println("ALPHABETA_SEARCH");
		AlphaBetaSearch ab = new AlphaBetaSearch(Board.BLACK);
		while (!tb.isGameFinished()) {
			int[] next = ab.nextMove(tb);
			System.out.println(next[0]);
			tb.move(next);
		}
		tb.setInitialConfiguration();

		System.out.println();
		System.out.println("ALPHABETA_SEARCH_H");
		AlphaBetaSearchH abh = new AlphaBetaSearchH(10, 1L, new FirstHeuristic(
				Board.BLACK));
		while (!tb.isGameFinished()) {
			int[] next = abh.nextMove(tb);
			System.out.println(next[0]);
			tb.move(next);
		}
		tb.setInitialConfiguration();

		System.out.println();
		System.out.println("MCT_SEARCH");
		MonteCarloTreeSearch mct = new MonteCarloTreeSearch(10, 1000L,
				new NewHeuristic(Board.BLACK), 1D, Board.BLACK);
		while (!tb.isGameFinished()) {
			int[] next = mct.nextMove(tb);
			System.out.println(next[0]);
			tb.move(next);
		}
		tb.setInitialConfiguration();

		System.out.println();
		System.out.println("ID_ALPHABETA_H");
		IDAlphaBetaSearchH idabh = new IDAlphaBetaSearchH(10, 1000L,
				new FirstHeuristic(Board.BLACK));
		while (!tb.isGameFinished()) {
			int[] next = idabh.nextMove(tb);
			System.out.println(next[0]);
			tb.move(next);
		}
		tb.setInitialConfiguration();

		System.out.println();
		System.out.println("ID_ALPHABETA_BM");
		IDAlphaBetaSearchH_BestMove idabh3 = new IDAlphaBetaSearchH_BestMove(10, 1000L,
				new FirstHeuristic(Board.BLACK));
		while (!tb.isGameFinished()) {
			int[] next = idabh3.nextMove(tb);
			System.out.println(next[0]);
			tb.move(next);
		}
		tb.setInitialConfiguration();

		
		System.out.println();
		System.out.println("ID_ALPHABETA_H3 RM");
		IDAlphaBetaSearchH_RandomMoves idabh3rm = new IDAlphaBetaSearchH_RandomMoves(10, 1000L,
				new FirstHeuristic(Board.BLACK));
		while (!tb.isGameFinished()) {
			int[] next = idabh3rm.nextMove(tb);
			System.out.println(next[0]);
			tb.move(next);
		}
		tb.setInitialConfiguration();


	}
}
