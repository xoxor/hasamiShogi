package hs.test;

import hs.gui.BoardCustomizer;
import hs.gui.GUI;
import hs.representation.Board;
import hs.representation.Boards;
import hs.representation.IllegalMoveException;
import hs.representation.MatrixBoard;
import hs.strategy.SearchStrategy;

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
		System.out.println("Risposta Media " + nome + " = " + sommaTempi / mosse);
		System.out.println("Risposta Massima " + nome + " = " + tempoMax);
		System.out.println("Eccessi " + nome + " = " + eccessi);
	}
}

public class GameTest {
	private static final int WHITEHUMAN = 1, BLACKHUMAN = 2, NOHUMAN = 0;
	private int sleeptime = 100;
	private int maxMoves = Integer.MAX_VALUE;
	private SearchStrategy p1;
	private SearchStrategy p2;
	private int human = NOHUMAN;
	private double nmosse = 0;
	private PlayerStat ps1, ps2;

	public GameTest(SearchStrategy playerBlack, SearchStrategy playerWhite) {
		this.p1 = playerBlack;
		this.p2 = playerWhite;
	}

	public GameTest(SearchStrategy playerBlack) {
		this.p1 = playerBlack;
		human = WHITEHUMAN;
	}

	public GameTest(SearchStrategy playerWhite, boolean humanblack) {
		this.p2 = playerWhite;
		human = BLACKHUMAN;
	}

	public void setSleepMove(int millis) {
		this.sleeptime = millis;
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

		Board board = new MatrixBoard(maxMoves);
		if (customized) {
			BoardCustomizer.setGuiConfiguration(board);
		} else {
			board.setInitialConfiguration();
		}
		GUI gui = new GUI();
		gui.setBoard(board);

		int[] move = { 0, 0, 0, 0 };
		while (!board.isGameFinished()) {
			String s = "";
			long i = 0, j = 0;
			switch (board.currentTurn()) {
			case Board.BLACK:
				if (human == BLACKHUMAN) {
					Boards.decodeMove(gui.getMove(), move);
				} else {
					i = System.currentTimeMillis();
					move = p1.nextMove(board);
					j = System.currentTimeMillis();
					s = "  " + (j - i) + " millis  ";
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
					Boards.decodeMove(gui.getMove(), move);
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
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
			}

		} // while

		System.out.println("\nMOSSE ESEGUITE = " + nmosse);
		if (ps1 != null)
			ps1.printStat();
		System.out.println();
		if (ps2 != null)
			ps2.printStat();
		gui.showWinner();
	}
}