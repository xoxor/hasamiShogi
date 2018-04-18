package hs;

import hs.gui.BoardCustomizer;
import hs.gui.GUI;
import hs.heuristic.HeuristicFunction;
import hs.heuristic.NewHeuristic;
import hs.representation.Board;
import hs.representation.Boards;
import hs.representation.IllegalMoveException;
import hs.representation.MatrixBoard;
import hs.strategy.*;

class PlayerStat {
	private String nome;
	private int mosse, eccessi, sommaTempi;
	private long tempoLimite;
	private long tempoMax;

	public PlayerStat(String name, long tempoLimite) {
		this.nome = name;
		this.tempoLimite = tempoLimite;
	}

	public void update(long i, long j) {
		long diff = j - i;
		tempoMax = diff > tempoMax ? diff : tempoMax;
		sommaTempi += diff;
		if (diff > tempoLimite) {
			eccessi++;
		}
		mosse++;
	}

	public void printStat() {
		System.out.println("Medium Response Time " + nome + " = " + sommaTempi / mosse);
		System.out.println("Maximum Response Time " + nome + " = " + tempoMax);
		System.out.println("Excesses " + nome + " = " + eccessi);
	}
}

public class Game {

	private static final int WHITEHUMAN = 1, BLACKHUMAN = 2, NOHUMAN = 0;
	
	private double nmosse = 0;
	private PlayerStat ps1, ps2;

	private int thres_move;
	private int thres_time;
	private int waiting_time;
	private int human = NOHUMAN;
	private int maxMoves;

	private Board board;
	private SearchStrategy p1;
	private SearchStrategy p2;
	private HeuristicFunction hf;

	public Game(int thres_move, int thres_time, int waiting_time, int algorithm1, int algorithm2, int playerBlackId, int playerWhiteId) {

		this.thres_move = thres_move;
		this.thres_time = thres_time;
		this.waiting_time = waiting_time;

		this.maxMoves=thres_move*2;
		
		board = new MatrixBoard(maxMoves);
		
		switch (algorithm1) {
		case 0:
			human = BLACKHUMAN;
		case 1:
			hf = new NewHeuristic(playerBlackId);
			p1 = new IDAlphaBetaSearchH_BestMove(thres_move, thres_time, hf);
			break;
		case 2:
			hf = new NewHeuristic(playerBlackId);
			p1 = new IDAlphaBetaSearchH(thres_move, thres_time, hf);
			break;
		case 3:
			hf = new NewHeuristic(playerBlackId);
			p1 = new MinimaxH(4, thres_time, hf);
			break;
		case 4:
			hf = new NewHeuristic(playerBlackId);
			p1 = new AlphaBetaSearchH(4, thres_time, hf);
			break;
		case 5:
			hf = new NewHeuristic(playerBlackId);
			p1 = new RandomMoves();
			break;
		case 6:
			hf = new NewHeuristic(playerBlackId);
			p1 = new MonteCarloTreeSearch(thres_move, thres_time, hf, 1D, playerBlackId);
			break;
		default:
			System.out.println("UNKWNOWN ALGORITHM");
			hf = new NewHeuristic(playerBlackId);
			p1 = new IDAlphaBetaSearchH_BestMove(thres_move, thres_time, hf);
		}

		switch (algorithm2) {
		case 0:
			human = WHITEHUMAN;
		case 1:
			hf = new NewHeuristic(playerWhiteId);
			p2 = new IDAlphaBetaSearchH_BestMove(thres_move, thres_time, hf);
			break;
		case 2:
			hf = new NewHeuristic(playerWhiteId);
			p2 = new IDAlphaBetaSearchH(thres_move, thres_time, hf);
			break;

		case 3:
			hf = new NewHeuristic(playerWhiteId);
			p2 = new MinimaxH(2, thres_time, hf);
			break;
		case 4:
			hf = new NewHeuristic(playerWhiteId);
			p2 = new AlphaBetaSearchH(4, thres_time, hf);
			break;
		case 5:
			hf = new NewHeuristic(playerWhiteId);
			p2 = new RandomMoves();
			break;
		case 6:
			hf = new NewHeuristic(playerWhiteId);
			p2 = new MonteCarloTreeSearch(thres_move, thres_time, hf, 1D, playerWhiteId);
			break;
		default:
			System.out.println("UNKWNOWN ALGORITHM");
			hf = new NewHeuristic(playerWhiteId);
			p2 = new IDAlphaBetaSearchH_BestMove(thres_move, thres_time, hf);
		}

	}

	public void setSleepMove(int millis) {
		this.waiting_time = millis;
	}

	public void setMaxDepth(int maxMoves) {
		this.maxMoves = maxMoves;
	}

	public void play() {
		play(false);
	}

	public void play(boolean customized) {
		if (p1 != null) {
			this.ps1 = new PlayerStat("Black", p1.getTimeThreshold());
		}
		if (p2 != null) {
			this.ps2 = new PlayerStat("White", p2.getTimeThreshold());
		}

		if (customized) {
			BoardCustomizer.setGuiConfiguration(board);
		} else {
			board.setInitialConfiguration();
		}
		GUI gui = new GUI();
		gui.setBoard(board);
		
		int[] move = { 0, 0, 0, 0 };
		while (!board.isGameFinished() && nmosse<=maxMoves) {
			String s = "";
			long i = 0, j = 0;
			switch (board.currentTurn()) {
			case Board.BLACK:
				if (human == BLACKHUMAN) {
					i = System.currentTimeMillis();
					Boards.decodeMove(gui.getMove(), move);
					j = System.currentTimeMillis();
					s = "  \t" + (j - i) + " millis  ";
					ps1.update(i, j);
				} else {
					i = System.currentTimeMillis();
					move = p1.nextMove(board);
					j = System.currentTimeMillis();
					s = "  \t" + (j - i) + " millis  ";
					ps1.update(i, j);
				}
				try {
					board.move(move);
					nmosse += 0.5;
					System.out.println("BLACK MOVES = " + Boards.encodeMove(move) + s);
					gui.upgradedGrid();
				} catch (IllegalMoveException e) {
					gui.showError(Boards.encodeMove(move));
				}
				break;
			case Board.WHITE:
				if (human == WHITEHUMAN) {
					i = System.currentTimeMillis();
					Boards.decodeMove(gui.getMove(), move);
					j = System.currentTimeMillis();
					s = "  \t" + (j - i) + " millis  ";
					ps2.update(i, j);
				} else {
					i = System.currentTimeMillis();
					move = p2.nextMove(board);
					j = System.currentTimeMillis();
					s = "  \t" + (j - i) + " millis  ";
					ps2.update(i, j);
				}
				try {
					board.move(move);
					nmosse += 0.5;
					System.out.println("WHITE MOVES = " + Boards.encodeMove(move) + s);
					gui.upgradedGrid();
				} catch (IllegalMoveException e) {
					gui.showError(Boards.encodeMove(move));
				}
				break;
			}// switch

			try {
				Thread.sleep(waiting_time);
			} catch (InterruptedException e) {
			}

		} // while

		System.out.println("\nTOTAL MOVES = " + nmosse);
		if (ps1 != null)
			ps1.printStat();
		System.out.println();
		if (ps2 != null)
			ps2.printStat();
		gui.showWinner();
	}



	public static void main(String[] args) throws Exception {
		try {
			Parameters pars = new Parameters(args);

			int thres_move = pars.thres_move; // maximum number of moves per
			// player
			int thres_time = pars.thres_time; // in milliseconds
			int waiting_time = pars.waiting_time; // in milliseconds

			int algorithm1 = pars.algorithm1;
			int algorithm2 = pars.algorithm2;

			int playerBlackId = pars.blackId;
			int playerWhiteId = pars.whiteId;			

			Game g = new Game(thres_move, thres_time, waiting_time, algorithm1, algorithm2, playerBlackId, playerWhiteId);
			g.play();
		} catch (Exception e) {
			if (e instanceof ParsingException) {
				System.out.println("PARSING ERROR");
				System.out.println("Usage:");
				System.out.println("\t-tm <thres_move> (default Integer.MAX_VALUE moves)");
				System.out.println("\t-tt <thres_time> (default 1000 ms)");
				System.out.println("\t-wt <waiting_time> (default 30000 ms)");
				System.out.println("\t-p1 <algorithm for player 1> (default 0)");
				System.out.println("\t-p2 <algorithm for player 2> (default 1)");
				System.out.println("\t-black <group_id> (default 0)");
				System.out.println("\t-white <group_id> (default 1)");
			} else
				e.printStackTrace();
		}
	}
}

class Parameters {

	public int thres_move = Integer.MAX_VALUE/2;
	public int thres_time = 1000;
	public int algorithm1 = 1;
	public int algorithm2 = 2;
	public int blackId = 0;
	public int whiteId = 1;
	public int waiting_time = 60;

	public Parameters(String[] args) throws ParsingException {
		for (int i = 0; i < args.length; i += 2) {
			if (args[i].charAt(0) != '-')
				throw new ParsingException();
			try {
				if (args[i].equals("-tm"))
					thres_move = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-tt"))
					thres_time = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-wt"))
					waiting_time = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-p1"))
					algorithm1 = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-p2"))
					algorithm2 = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-black"))
					blackId = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-white"))
					whiteId = Integer.valueOf(args[i + 1]);
				else
					throw new ParsingException();
			} catch (Exception e) {
				throw new ParsingException();
			}

		}
	}
}



